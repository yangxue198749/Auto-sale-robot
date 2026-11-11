# Assemble server deploy package into dist/
# Usage: powershell -ExecutionPolicy Bypass -File deploy/server/build-deploy-package.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)   # vms/
$serverDir = $PSScriptRoot
$dist = Join-Path $serverDir 'dist'

if (-not (Test-Path (Join-Path $root 'vms-web/target/vms-web.war'))) {
    Write-Host 'war not found. Run tools/build-release.ps1 first.' -ForegroundColor Red
    exit 1
}

Remove-Item $dist -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $dist | Out-Null
New-Item -ItemType Directory -Path (Join-Path $dist 'sql') | Out-Null

Copy-Item (Join-Path $root 'vms-web/target/vms-web.war') (Join-Path $dist 'ROOT.war')
Copy-Item (Join-Path $serverDir 'docker-compose.yml') $dist
Copy-Item (Join-Path $serverDir '.env.example') (Join-Path $dist '.env.example')
Copy-Item (Join-Path $serverDir 'DEPLOY_RUNBOOK.md') $dist

# init SQLs for docker-entrypoint-initdb.d (run in filename order, first boot only)
$sqlMap = [ordered]@{
    '01-schema.sql'           = 'database/vms_schema.sql'
    '02-autopk.sql'           = 'database/vms_cayenne_pk.sql'
    '03-seed.sql'             = 'database/vms_seed.sql'
    '04-syslog-dict.sql'      = 'database/vms_seed_syslog_dict.sql'
    '05-customer-dict.sql'    = 'database/vms_seed_customer_dict.sql'
    '07-server-overrides.sql' = 'deploy/server/sql/07-server-overrides.sql'
}
foreach ($name in $sqlMap.Keys) {
    Copy-Item (Join-Path $root $sqlMap[$name]) (Join-Path $dist "sql/$name")
}

Write-Host ''
Write-Host "OK deploy package assembled: $dist" -ForegroundColor Green
Write-Host 'Upload to server and follow DEPLOY_RUNBOOK.md'
Get-ChildItem $dist -Recurse | Select-Object FullName, Length | Format-Table -AutoSize
