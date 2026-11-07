<#
VMS Phase 0 acceptance verifier - maps 1:1 to INSTALLATION_GUIDE.md section 3/6.
Read-only. Safe to run without elevation.

  powershell -ExecutionPolicy Bypass -File .\verify-vms-phase0.ps1

To include the MySQL table-count check, set MYSQL_PWD in the shell first
(never store it in a file):
  $env:MYSQL_PWD = '...'; powershell -File .\verify-vms-phase0.ps1
Exit code 0 = no FAIL, 1 = at least one FAIL.
#>
[CmdletBinding()]
param(
    [string]$MysqlUser = 'root'
)

$ErrorActionPreference = 'Continue'
$MysqlExe   = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
$RedisCli   = 'C:\Program Files\Redis\redis-cli.exe'
$ErlangHome = [Environment]::GetEnvironmentVariable('ERLANG_HOME', 'Machine')
if (-not $ErlangHome) { $ErlangHome = 'C:\Program Files\Erlang OTP' }
$ErlExe     = Join-Path $ErlangHome 'bin\erl.exe'
$RabbitRoot = 'C:\Program Files\RabbitMQ Server'

# Refresh process-level env: the installer writes Machine-level variables,
# which a shell started before the install does not inherit.
if (-not $env:ERLANG_HOME) { $env:ERLANG_HOME = $ErlangHome }
$erlBin = Join-Path $ErlangHome 'bin'
if (($env:Path -split ';') -notcontains $erlBin) { $env:Path += ';' + $erlBin }

$results = @()
function Add-Result($name, $status, $detail) {
    $script:results += [pscustomobject]@{ Check = $name; Status = $status; Detail = $detail }
}

# --- MySQL -------------------------------------------------------------
try {
    $v = (& $MysqlExe --version 2>&1 | Out-String).Trim()
    if ($v -match 'Ver 8\.') { Add-Result 'MySQL 8.0+ installed' 'PASS' $v }
    else { Add-Result 'MySQL 8.0+ installed' 'FAIL' $v }
} catch { Add-Result 'MySQL 8.0+ installed' 'FAIL' $_.Exception.Message }

try {
    $svc = (Get-Service MySQL80 -ErrorAction Stop).Status
    Add-Result 'MySQL80 service running' $(if ($svc -eq 'Running') {'PASS'} else {'FAIL'}) "$svc"
} catch { Add-Result 'MySQL80 service running' 'FAIL' $_.Exception.Message }

if ($env:MYSQL_PWD) {
    try {
        $n = (& $MysqlExe -u $MysqlUser -N -B -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='vms';" 2>&1 | Select-Object -First 1)
        if ($LASTEXITCODE -ne 0) { throw "$n" }
        $n = [int]$n
        Add-Result 'vms database = 85 tables' $(if ($n -eq 85) {'PASS'} else {'FAIL'}) "table_count=$n expected=85"
    } catch { Add-Result 'vms database = 85 tables' 'FAIL' $_.Exception.Message }
} else {
    Add-Result 'vms database = 85 tables' 'SKIP' 'MYSQL_PWD not set in this shell'
}

# --- Redis -------------------------------------------------------------
try {
    $ping = (& $RedisCli ping 2>&1 | Out-String).Trim()
    Add-Result 'Redis PING' $(if ($ping -eq 'PONG') {'PASS'} else {'FAIL'}) $ping
} catch { Add-Result 'Redis PING' 'FAIL' $_.Exception.Message }

try {
    $info = (& $RedisCli info server 2>&1 | Out-String)
    if ($info -match 'redis_version:([0-9.]+)') {
        $rv = [version]$Matches[1]
        if ($rv.Major -ge 6) { Add-Result 'Redis 6.0+ (guide req)' 'PASS' "$rv" }
        else { Add-Result 'Redis 6.0+ (guide req)' 'FAIL' "$rv (installed 3.0.504 does not meet the 6.0+ requirement)" }
    } else { Add-Result 'Redis 6.0+ (guide req)' 'FAIL' 'version not readable' }
} catch { Add-Result 'Redis 6.0+ (guide req)' 'FAIL' $_.Exception.Message }

try {
    $bindOut = (& $RedisCli CONFIG GET bind 2>&1 | Out-String)
    $lines = @($bindOut -split "`r?`n" | Where-Object { $_ -match '\S' })
    $value = '(empty)'
    if ($lines.Count -ge 2) {
        $raw = ($lines[1] -replace '^\s*\d+\)\s*', '').Trim().Trim('"')
        if ($raw) { $value = $raw }
    }
    if ($value -eq '127.0.0.1') { Add-Result 'Redis bind=127.0.0.1' 'PASS' $value }
    else { Add-Result 'Redis bind=127.0.0.1' 'WARN' "bind=$value -> listens on all interfaces (local dev exposure)" }
} catch { Add-Result 'Redis bind=127.0.0.1' 'WARN' $_.Exception.Message }

# vms-service/.../MyJedisCluster.java subscribes to "__keyevent@0__:expired".
# With notify-keyspace-events empty, that subscriber never receives anything
# and the Phase 0 business loop breaks even though Redis itself responds.
try {
    $cfg = @(& $RedisCli CONFIG GET notify-keyspace-events 2>&1)
    $value = if ($cfg.Count -ge 2) { $cfg[1].Trim() } else { '(unknown)' }
    if ($value -match 'E' -and $value -match 'x') {
        Add-Result 'Redis expired-key events (Ex)' 'PASS' "notify-keyspace-events=$value"
    } else {
        Add-Result 'Redis expired-key events (Ex)' 'FAIL' "notify-keyspace-events='$value' -> __keyevent@0__:expired subscriber gets no events"
    }
} catch { Add-Result 'Redis expired-key events (Ex)' 'FAIL' $_.Exception.Message }

# --- Erlang ------------------------------------------------------------
$eh = [Environment]::GetEnvironmentVariable('ERLANG_HOME', 'Machine')
Add-Result 'ERLANG_HOME set (Machine)' $(if ($eh) {'PASS'} else {'FAIL'}) "$eh"

try {
    $otp = (& $ErlExe -noshell -eval "io:format('~s',[erlang:system_info(otp_release)]),halt()." 2>&1 | Out-String).Trim()
    if ($otp -match '^26') { Add-Result 'erl reports OTP 26.x' 'PASS' $otp }
    else { Add-Result 'erl reports OTP 26.x' 'FAIL' "otp_release='$otp'" }
} catch { Add-Result 'erl reports OTP 26.x' 'FAIL' "erl.exe not runnable: $($_.Exception.Message)" }

# --- RabbitMQ ----------------------------------------------------------
try {
    $svc = (Get-Service RabbitMQ -ErrorAction Stop).Status
    Add-Result 'RabbitMQ service running' $(if ($svc -eq 'Running') {'PASS'} else {'FAIL'}) "$svc"
} catch { Add-Result 'RabbitMQ service running' 'FAIL' 'service not installed' }

$sbin = $null
if (Test-Path $RabbitRoot) {
    $dir = Get-ChildItem $RabbitRoot -Directory -Filter 'rabbitmq_server-*' -ErrorAction SilentlyContinue |
           Sort-Object Name -Descending | Select-Object -First 1
    if ($dir) { $sbin = Join-Path $dir.FullName 'sbin' }
}
if ($sbin -and (Test-Path (Join-Path $sbin 'rabbitmqctl.bat'))) {
    try {
        $out = (& (Join-Path $sbin 'rabbitmqctl.bat') status 2>&1 | Out-String)
        if ($LASTEXITCODE -eq 0 -and $out -match 'Status of node') { Add-Result 'rabbitmqctl status' 'PASS' (($out -split "`r?`n" | Select-Object -First 2) -join ' | ') }
        else { Add-Result 'rabbitmqctl status' 'FAIL' (($out -split "`r?`n" | Select-Object -Last 3) -join ' | ') }
    } catch { Add-Result 'rabbitmqctl status' 'FAIL' $_.Exception.Message }

    try {
        $plugins = (& (Join-Path $sbin 'rabbitmq-plugins.bat') list -e 2>&1 | Out-String)
        if ($plugins -match 'rabbitmq_management') { Add-Result 'management plugin enabled' 'PASS' 'rabbitmq_management' }
        else { Add-Result 'management plugin enabled' 'FAIL' 'not enabled' }
    } catch { Add-Result 'management plugin enabled' 'FAIL' $_.Exception.Message }

    try {
        $vh = (& (Join-Path $sbin 'rabbitmqctl.bat') list_vhosts 2>&1 | Out-String)
        $us = (& (Join-Path $sbin 'rabbitmqctl.bat') list_users 2>&1 | Out-String)
        $okV = $vh -match '(?m)^vms\s*$'
        $okU = $us -match '(?m)^vms_user\s'
        Add-Result 'vhost vms + user vms_user' $(if ($okV -and $okU) {'PASS'} else {'FAIL'}) "vhost=$okV user=$okU"
    } catch { Add-Result 'vhost vms + user vms_user' 'FAIL' $_.Exception.Message }
} else {
    Add-Result 'rabbitmqctl status' 'SKIP' 'RabbitMQ sbin not installed yet'
    Add-Result 'management plugin enabled' 'SKIP' 'RabbitMQ sbin not installed yet'
    Add-Result 'vhost vms + user vms_user' 'SKIP' 'RabbitMQ sbin not installed yet'
}

try {
    $r = Invoke-WebRequest -Uri 'http://localhost:15672' -UseBasicParsing -TimeoutSec 8
    Add-Result 'Mgmt UI http://localhost:15672' $(if ($r.StatusCode -eq 200) {'PASS'} else {'FAIL'}) "HTTP $($r.StatusCode)"
} catch { Add-Result 'Mgmt UI http://localhost:15672' 'FAIL' $_.Exception.Message }

# --- Ports -------------------------------------------------------------
try {
    $listening = @(Get-NetTCPConnection -State Listen -ErrorAction Stop | ForEach-Object { $_.LocalPort } | Sort-Object -Unique)
    foreach ($spec in @(@(3306,'MySQL'), @(6379,'Redis'), @(5672,'RabbitMQ AMQP'), @(15672,'RabbitMQ Mgmt'))) {
        $port = $spec[0]; $label = $spec[1]
        Add-Result "Port $port LISTENING ($label)" $(if ($listening -contains $port) {'PASS'} else {'FAIL'}) ""
    }
} catch { Add-Result 'Port checks' 'FAIL' $_.Exception.Message }

# --- Report ------------------------------------------------------------
$results | Format-Table -AutoSize | Out-String -Width 160 | Write-Host
$pass = @($results | Where-Object Status -eq 'PASS').Count
$fail = @($results | Where-Object Status -eq 'FAIL').Count
$warn = @($results | Where-Object Status -eq 'WARN').Count
$skip = @($results | Where-Object Status -eq 'SKIP').Count
Write-Host "SUMMARY: PASS=$pass FAIL=$fail WARN=$warn SKIP=$skip"
if ($fail -gt 0) { exit 1 }
