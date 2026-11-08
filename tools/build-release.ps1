# VMS release build: frontend + backend war
# Usage: powershell -ExecutionPolicy Bypass -File tools/build-release.ps1
$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Write-Host "== VMS release build ==" -ForegroundColor Cyan

Push-Location (Join-Path $root "vms-ui")
try {
    Write-Host "== npm ci + build ==" -ForegroundColor Cyan
    npm ci
    if ($LASTEXITCODE -ne 0) { throw "npm ci failed" }
    npm run build
    if ($LASTEXITCODE -ne 0) { throw "npm run build failed" }
} finally {
    Pop-Location
}

Push-Location $root
try {
    Write-Host "== mvn package ==" -ForegroundColor Cyan
    mvn -DskipTests package
    if ($LASTEXITCODE -ne 0) { throw "mvn package failed" }
} finally {
    Pop-Location
}

$war = Join-Path $root "vms-web/target/vms-web.war"
if (-not (Test-Path $war)) { throw "war not found: $war" }
Write-Host "OK: $war" -ForegroundColor Green
Write-Host "Deploy to Tomcat / run: mvn -pl vms-web cargo:run" -ForegroundColor Green
