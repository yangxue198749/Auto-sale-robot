# check-baseline.ps1
# VMS Migration Baseline Check Script

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "VMS Migration Baseline Check" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Git Repository Status
Write-Host "1. Git Repository Status" -ForegroundColor Yellow
Write-Host "----------------------------------------"

# vms/ repository
Set-Location "$PSScriptRoot\.."
$vmsRoot = git rev-parse --show-toplevel 2>&1
$vmsCommit = git log -1 --format="%H" 2>&1
$vmsDirty = (git status --short 2>&1 | Measure-Object -Line).Lines

Write-Host "Backend Repository (vms/):" -ForegroundColor Green
Write-Host "  Git Root: $vmsRoot"
Write-Host "  Latest Commit: $vmsCommit"
Write-Host "  Dirty Files: $vmsDirty"
Write-Host ""

# vms-ui/ repository (inside AI_Company)
Set-Location "$PSScriptRoot\..\..\vms-ui"
$uiRoot = git rev-parse --show-toplevel 2>&1
$uiCommit = git log -1 --format="%H" 2>&1
$uiDirty = (git status --short 2>&1 | Measure-Object -Line).Lines

Write-Host "Frontend Repository (vms-ui/):" -ForegroundColor Green
Write-Host "  Git Root: $uiRoot (Note: inside AI_Company repo)"
Write-Host "  AI_Company Latest Commit: $uiCommit"
Write-Host "  vms-ui Dirty Files: $uiDirty"
Write-Host ""

# 2. Tech Stack Versions
Write-Host "2. Tech Stack Versions" -ForegroundColor Yellow
Write-Host "----------------------------------------"

$javaVersion = java -version 2>&1 | Select-Object -First 1
Write-Host "Java: $javaVersion" -ForegroundColor Green

$mvnVersion = mvn -version 2>&1 | Select-Object -First 1
Write-Host "Maven: $mvnVersion" -ForegroundColor Green

$nodeVersion = node --version
Write-Host "Node.js: $nodeVersion" -ForegroundColor Green

$npmVersion = npm --version
Write-Host "npm: $npmVersion" -ForegroundColor Green

Write-Host ""

# 3. Backend Dependencies
Write-Host "3. Backend Key Dependencies" -ForegroundColor Yellow
Write-Host "----------------------------------------"

Set-Location "$PSScriptRoot\.."
$pomContent = Get-Content pom.xml -Raw

$springVersion = if ($pomContent -match '<spring\.version>([^<]+)</spring\.version>') { $matches[1] } else { "N/A" }
$cayenneVersion = if ($pomContent -match '<cayenne\.version>([^<]+)</cayenne\.version>') { $matches[1] } else { "N/A" }
$shiroVersion = if ($pomContent -match '<shiro\.version>([^<]+)</shiro\.version>') { $matches[1] } else { "N/A" }

Write-Host "Spring Framework: $springVersion" -ForegroundColor Green
Write-Host "Apache Cayenne: $cayenneVersion" -ForegroundColor Green
Write-Host "Apache Shiro: $shiroVersion" -ForegroundColor Green

Write-Host ""

# 4. Frontend Dependencies
Write-Host "4. Frontend Key Dependencies" -ForegroundColor Yellow
Write-Host "----------------------------------------"

Set-Location "$PSScriptRoot\..\..\vms-ui"
if (Test-Path "package.json") {
    $packageJson = Get-Content package.json -Raw | ConvertFrom-Json

    $vueVersion = $packageJson.dependencies.vue
    $vueRouterVersion = $packageJson.dependencies.'vue-router'
    $piniaVersion = $packageJson.dependencies.pinia
    $viteVersion = $packageJson.devDependencies.vite
    $tsVersion = $packageJson.devDependencies.typescript

    Write-Host "Vue: $vueVersion" -ForegroundColor Green
    Write-Host "Vue Router: $vueRouterVersion" -ForegroundColor Green
    Write-Host "Pinia: $piniaVersion" -ForegroundColor Green
    Write-Host "Vite: $viteVersion" -ForegroundColor Green
    Write-Host "TypeScript: $tsVersion" -ForegroundColor Green
} else {
    Write-Host "package.json not found" -ForegroundColor Red
}

Write-Host ""

# 5. Service Status Check
Write-Host "5. Service Status Check" -ForegroundColor Yellow
Write-Host "----------------------------------------"

$services = @(
    @{Name="Tomcat"; Port=8080},
    @{Name="Vite Dev"; Port=5173},
    @{Name="MySQL"; Port=3306},
    @{Name="Redis"; Port=6379},
    @{Name="RabbitMQ"; Port=5672},
    @{Name="MCS Mock"; Port=8011}
)

foreach ($svc in $services) {
    $listening = netstat -ano | Select-String ":$($svc.Port).*LISTENING"
    if ($listening) {
        Write-Host "$($svc.Name) (Port $($svc.Port)): Running" -ForegroundColor Green
    } else {
        Write-Host "$($svc.Name) (Port $($svc.Port)): Not Running" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Baseline Check Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
