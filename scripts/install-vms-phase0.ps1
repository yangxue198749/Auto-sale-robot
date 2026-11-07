<#
VMS Phase 0 - minimal runtime installer (Windows / PowerShell 5.1)
Source: vms/docs/INSTALLATION_GUIDE.md v1.0

RUN FROM AN ELEVATED POWERSHELL:
  powershell -ExecutionPolicy Bypass -File .\install-vms-phase0.ps1

Idempotent: completed steps are skipped on re-run. Never re-imports the schema
into an existing populated vms database.

Version pins (official compatibility matrix https://www.rabbitmq.com/docs/which-erlang):
  Erlang OTP 26.2.5.13  + RabbitMQ 3.12.13  => Erlang 26.0..26.2.x supported.
Installers are the official GitHub release assets, pre-downloaded to $InstallerDir.

SECURITY NOTE: INSTALLATION_GUIDE.md hardcodes "vms_password_2026" in a git
tracked file. This script DOES NOT use it. It generates a random 24-char
alphanumeric password at runtime instead.
#>
[CmdletBinding()]
param(
    [string]$InstallerDir = 'E:\Projects\_installers\vms-phase0',
    [string]$MysqlAdminUser = 'root'
)

$ErrorActionPreference = 'Stop'

$ProjectVmsDir = Split-Path -Parent $PSScriptRoot
$SchemaPath   = Join-Path $ProjectVmsDir 'database\vms_schema.sql'
$ErlangExe    = Join-Path $InstallerDir 'otp_win64_26.2.5.13.exe'
$RabbitExe    = Join-Path $InstallerDir 'rabbitmq-server-3.12.13.exe'
$CredFile     = Join-Path $InstallerDir 'credentials.txt'
$MysqlExe     = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
$RedisCli     = 'C:\Program Files\Redis\redis-cli.exe'
$ErlangHome   = 'C:\Program Files\Erlang OTP'
$RabbitRoot   = 'C:\Program Files\RabbitMQ Server'

function Write-Step($msg) { Write-Host "`n=== $msg ===" -ForegroundColor Cyan }
function Assert-Admin {
    $principal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
    if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
        throw 'NOT ELEVATED. Open PowerShell as Administrator and run this script again. RabbitMQ Windows service requires an administrative install.'
    }
}
function Add-ToMachinePath($dir) {
    $current = [Environment]::GetEnvironmentVariable('Path', 'Machine')
    $parts = $current -split ';'
    if ($parts -notcontains $dir) {
        [Environment]::SetEnvironmentVariable('Path', ($current.TrimEnd(';') + ';' + $dir), 'Machine')
    }
    if (($env:Path -split ';') -notcontains $dir) { $env:Path += ';' + $dir }
}
function Get-PlainText($secure) {
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try { return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr) }
}
function New-VmsPassword {
    $chars = 'abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789'.ToCharArray()
    $bytes = New-Object byte[] 24
    $rng = New-Object Security.Cryptography.RNGCryptoServiceProvider
    $rng.GetBytes($bytes)
    -join ($bytes | ForEach-Object { $chars[$_ % $chars.Length] })
}
function Get-RabbitSbin {
    $dir = Get-ChildItem -Path $RabbitRoot -Directory -Filter 'rabbitmq_server-*' -ErrorAction SilentlyContinue |
           Sort-Object Name -Descending | Select-Object -First 1
    if (-not $dir) { throw "RabbitMQ install directory not found under $RabbitRoot" }
    $sbin = Join-Path $dir.FullName 'sbin'
    if (-not (Test-Path (Join-Path $sbin 'rabbitmqctl.bat'))) { throw "rabbitmqctl.bat not found in $sbin" }
    return $sbin
}
function Invoke-Cli($file, $arguments) {
    # Native stderr becomes an ErrorRecord; with $ErrorActionPreference='Stop'
    # that would abort the call. Suppress it locally and rely on the exit code.
    $prev = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $out = & $file @arguments 2>&1
        $code = $LASTEXITCODE
        # Never echo $arguments: some calls carry generated passwords.
        if ($code -ne 0) { throw "Command failed (exit $code): $(Split-Path $file -Leaf) :: $(($out | Select-Object -Last 5) -join ' | ')" }
        return $out
    } finally { $ErrorActionPreference = $prev }
}
function Wait-RabbitReady($rabbitSbin, $timeoutSeconds = 90) {
    # After a service restart the Erlang node is reachable before the 'rabbit'
    # application is up. A fixed sleep is not enough; poll until CLI confirms.
    $prev = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)
    $last = ''
    try {
        do {
            Start-Sleep -Seconds 2
            $out = (& (Join-Path $rabbitSbin 'rabbitmqctl.bat') status 2>&1 | Out-String)
            if ($LASTEXITCODE -eq 0 -and $out -match 'Status of node') { return }
            $last = (($out -split "`r?`n" | Where-Object { $_ -match '\S' } | Select-Object -Last 2) -join ' | ')
        } until ((Get-Date) -gt $deadline)
        throw "RabbitMQ node not ready within ${timeoutSeconds}s after restart. Last CLI error: $last"
    } finally { $ErrorActionPreference = $prev }
}

Assert-Admin
Write-Step 'Preflight'
foreach ($f in @($SchemaPath, $ErlangExe, $RabbitExe, $MysqlExe, $RedisCli)) {
    if (-not (Test-Path $f)) { throw "Missing required file: $f" }
    Write-Host "  OK  $f"
}
if (-not ((Get-Service MySQL80 -ErrorAction SilentlyContinue).Status -eq 'Running')) { throw 'MySQL80 service is not running' }
if (-not ((Get-Service Redis   -ErrorAction SilentlyContinue).Status -eq 'Running')) { throw 'Redis service is not running' }

# ---------------------------------------------------------------- 1. Erlang
Write-Step '1/6  Erlang OTP 26.2.5.13'
if (-not (Test-Path (Join-Path $ErlangHome 'bin\erl.exe'))) {
    Write-Host '  Installing silently (/S) ...'
    $p = Start-Process -FilePath $ErlangExe -ArgumentList '/S' -Wait -PassThru -NoNewWindow
    if ($p.ExitCode -ne 0) { throw "Erlang installer exited with $($p.ExitCode)" }
} else { Write-Host '  Already installed, skipping.' }
if (-not (Test-Path (Join-Path $ErlangHome 'bin\erl.exe'))) { throw 'erl.exe not found after installation' }
[Environment]::SetEnvironmentVariable('ERLANG_HOME', $ErlangHome, 'Machine')
$env:ERLANG_HOME = $ErlangHome
Add-ToMachinePath (Join-Path $ErlangHome 'bin')
$otp = & (Join-Path $ErlangHome 'bin\erl.exe') -noshell -eval "io:format('~s',[erlang:system_info(otp_release)]),halt()."
Write-Host "  OTP release: $otp"
if ($otp -notmatch '^26') { throw "Expected OTP 26.x, got $otp" }

# ------------------------------------------------------------ 2. RabbitMQ
Write-Step '2/6  RabbitMQ Server 3.12.13'
if (-not (Get-Service RabbitMQ -ErrorAction SilentlyContinue)) {
    Write-Host '  Installing silently (/S) ...'
    $p = Start-Process -FilePath $RabbitExe -ArgumentList '/S' -Wait -PassThru -NoNewWindow
    if ($p.ExitCode -ne 0) { throw "RabbitMQ installer exited with $($p.ExitCode)" }
} else { Write-Host '  Service already exists, skipping installer.' }
$sbin = Get-RabbitSbin
Add-ToMachinePath $sbin
$env:RABBITMQ_SERVER = Split-Path -Parent $sbin

# The broker service runs as LocalSystem and reads the cookie from the system
# profile; an elevated CLI reads %USERPROFILE%\.erlang.cookie. Step 1 already
# runs erl.exe, which creates a DIFFERENT random cookie in %USERPROFILE%.
# A conditional copy is not enough: probe with the current cookie first, then
# force each known service cookie source until rabbitmqctl authenticates.
$userCookie = Join-Path $env:USERPROFILE '.erlang.cookie'
$cookieSources = @(
    (Join-Path $env:SystemRoot '.erlang.cookie'),
    (Join-Path $env:SystemRoot 'system32\config\systemprofile\.erlang.cookie')
) | Where-Object { Test-Path $_ }

$svc = Get-Service RabbitMQ
if ($svc.Status -ne 'Running') { Start-Service RabbitMQ }

$ok = $false
$lastErr = ''
$prevEap = $ErrorActionPreference
$ErrorActionPreference = 'Continue'   # native stderr must not abort the probe
foreach ($source in (@($null) + @($cookieSources))) {
    if ($source) {
        Copy-Item $source $userCookie -Force
        Write-Host "  Trying Erlang cookie from $source"
    } else {
        Write-Host '  Probing with existing user Erlang cookie ...'
    }
    $deadline = (Get-Date).AddSeconds(40)
    do {
        Start-Sleep -Seconds 3
        try {
            $probe = (& (Join-Path $sbin 'rabbitmqctl.bat') status 2>&1 | Out-String)
            $ok = ($LASTEXITCODE -eq 0)
            if (-not $ok) { $lastErr = (($probe -split "`r?`n" | Where-Object { $_ -match '\S' } | Select-Object -Last 3) -join ' | ') }
        } catch { $lastErr = $_.Exception.Message }
    } until ($ok -or (Get-Date) -gt $deadline)
    if ($ok) { break }
}
$ErrorActionPreference = $prevEap
if (-not $ok) {
    throw "rabbitmqctl cannot authenticate with the node. Tried user cookie and $(@($cookieSources).Count) service cookie source(s). Last CLI error: $lastErr"
}
Write-Host '  Node is up (rabbitmqctl authenticated).'

# ------------------------------------------------------- 3. Management UI
Write-Step '3/6  Management plugin'
$prevEap = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
$pluginList = (& (Join-Path $sbin 'rabbitmq-plugins.bat') list -e 2>&1 | Out-String)
$ErrorActionPreference = $prevEap
if ($pluginList -match 'rabbitmq_management') {
    Write-Host '  rabbitmq_management already enabled, skipping restart (idempotent).'
} else {
    Invoke-Cli (Join-Path $sbin 'rabbitmq-plugins.bat') @('enable', 'rabbitmq_management') | Out-Null
    Write-Host '  Plugin enabled; restarting service ...'
    Restart-Service RabbitMQ -Force
}
Wait-RabbitReady $sbin
Write-Host '  Management plugin running, node ready.'

# ------------------------------------------------------ 4. Vhost + user
Write-Step '4/6  RabbitMQ vhost and service account'
Wait-RabbitReady $sbin 30
$VmsPass = New-VmsPassword
$prevEap = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
$vhosts = (& (Join-Path $sbin 'rabbitmqctl.bat') list_vhosts 2>&1 | Out-String)
$users  = (& (Join-Path $sbin 'rabbitmqctl.bat') list_users 2>&1 | Out-String)
$ErrorActionPreference = $prevEap
if ($vhosts -notmatch '(?m)^vms\s*$') {
    Invoke-Cli (Join-Path $sbin 'rabbitmqctl.bat') @('add_vhost', 'vms') | Out-Null
}
if ($users -notmatch '(?m)^vms_user\s') {
    Invoke-Cli (Join-Path $sbin 'rabbitmqctl.bat') @('add_user', 'vms_user', $VmsPass) | Out-Null
} else {
    Invoke-Cli (Join-Path $sbin 'rabbitmqctl.bat') @('change_password', 'vms_user', $VmsPass) | Out-Null
}
Invoke-Cli (Join-Path $sbin 'rabbitmqctl.bat') @('set_permissions', '-p', 'vms', 'vms_user', '.*', '.*', '.*') | Out-Null
Invoke-Cli (Join-Path $sbin 'rabbitmqctl.bat') @('set_user_tags', 'vms_user', 'administrator') | Out-Null
Write-Host '  vhost=vms  user=vms_user  tag=administrator'

# ------------------------------------------------------------- 5. MySQL
Write-Step '5/6  MySQL vms schema (85 tables)'
$RootSecure = Read-Host 'MySQL root password (input is hidden, not stored)' -AsSecureString
$env:MYSQL_PWD = Get-PlainText $RootSecure
$dbCount = (& $MysqlExe -u $MysqlAdminUser -N -B -e "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name='vms';" 2>&1)
if ($LASTEXITCODE -ne 0) { throw "MySQL root login failed: $dbCount" }
$tableCount = 0
if ($dbCount -match '1') {
    $tableCount = [int]((& $MysqlExe -u $MysqlAdminUser -N -B -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='vms';" 2>&1) | Select-Object -First 1)
}
if ($tableCount -eq 0) {
    # The project path contains non-ASCII characters; some MySQL clients
    # mishandle them inside `source`, so stage the file on an ASCII-only path.
    $tempSchema = Join-Path $env:TEMP 'vms_schema_phase0.sql'
    Copy-Item $SchemaPath $tempSchema -Force
    try {
        $schemaArg = $tempSchema -replace '\\', '/'
        Write-Host '  Importing vms_schema.sql ...'
        $out = & $MysqlExe -u $MysqlAdminUser "--execute=source $schemaArg" 2>&1
        if ($LASTEXITCODE -ne 0) { throw "Schema import failed: $out" }
    } finally {
        Remove-Item $tempSchema -Force -ErrorAction SilentlyContinue
    }
} else {
    Write-Host "  Database vms already has $tableCount tables, skipping import (idempotent)."
}
Invoke-Cli $MysqlExe @("-u", $MysqlAdminUser, "--execute=CREATE USER IF NOT EXISTS 'vms_user'@'localhost' IDENTIFIED BY '$VmsPass'; GRANT ALL PRIVILEGES ON vms.* TO 'vms_user'@'localhost'; FLUSH PRIVILEGES;") | Out-Null
# Keep MYSQL_PWD until after verification so the 85-table check is not skipped.
Write-Host '  user=vms_user  database=vms'

# ---------------------------------------------------------- 6. Credentials
Write-Step '6/6  Connection info'
$cred = @"
VMS Phase 0 connection info - generated $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
KEEP THIS FILE OUT OF GIT. Delete it after copying to your password manager.

MySQL    : localhost:3306  db=vms      user=vms_user  password=$VmsPass
Redis    : localhost:6379  (no password)
RabbitMQ : localhost:5672  vhost=vms   user=vms_user  password=$VmsPass
Mgmt UI  : http://localhost:15672      guest/guest (localhost only)
"@
Set-Content -Path $CredFile -Value $cred -Encoding UTF8
Write-Host $cred

Write-Step 'Verification'
& (Join-Path $PSScriptRoot 'verify-vms-phase0.ps1')
$env:MYSQL_PWD = $null

<# ROLLBACK (elevated PowerShell) - run only after confirming no other project uses these services:
  Stop-Service RabbitMQ
  & "C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.13\sbin\rabbitmq-service.bat" remove
  # Uninstall "Erlang OTP 26.2.5" and "RabbitMQ Server 3.12.13" from Programs and Features
  [Environment]::SetEnvironmentVariable('ERLANG_HOME', $null, 'Machine')
  # Remove the two PATH entries added by Add-ToMachinePath
  # MySQL: DROP DATABASE vms;  <-- DESTRUCTIVE, irreversible, requires explicit confirmation
  Remove-Item 'E:\Projects\_installers\vms-phase0\credentials.txt'
#>
