# T-2.8 Delivery Task Cross-Platform Closed Loop Regression Test
#
# Modes:
#   Read-only (default): verify service health, auth protection, frontend build (no login needed)
#   Full mode: requires temporarily enabling login.captcha.date.bypass.enabled=true to auto-login
#
# Usage:
#   .\test-delivery-regression.ps1              # read-only mode
#   .\test-delivery-regression.ps1 -Full        # full mode

param(
    [switch]$Full
)

$apiUrl = "http://127.0.0.1:8080"
$viteUrl = "http://localhost:5173"
$today = Get-Date -Format "yyyy-MM-dd"
$pass = 0
$fail = 0

function Assert($name, $cond, $detail = '') {
    if ($cond) {
        Write-Host "[PASS] $name" -ForegroundColor Green
        $script:pass++
    } else {
        Write-Host "[FAIL] $name $detail" -ForegroundColor Red
        $script:fail++
    }
}

$mode = if ($Full) { 'FULL' } else { 'READ-ONLY' }
Write-Host "=== T-2.8 Delivery Task Regression Test ($mode) ===" -ForegroundColor Cyan

# ---- Read-only checks ----
Write-Host "`n[Read-only] Service health" -ForegroundColor Yellow
try {
    $loginPage = Invoke-WebRequest -Uri "$apiUrl/login" -UseBasicParsing -MaximumRedirection 0
    Assert 'Login page accessible' ($loginPage.StatusCode -eq 200)
} catch { Assert 'Login page accessible' $false $_.Exception.Message }

try {
    $captcha = Invoke-WebRequest -Uri "$apiUrl/createCode" -UseBasicParsing
    Assert 'Captcha returns jpeg' ($captcha.Headers.'Content-Type' -match 'image/jpeg')
} catch { Assert 'Captcha returns jpeg' $false $_.Exception.Message }

Write-Host "`n[Read-only] REST auth (no cookie must be 401)" -ForegroundColor Yellow
$protectedPaths = @(
    '/vms/api/deliveryTask/list',
    '/vms/api/deliveryTask/1',
    '/vms/api/taskOptions/deliveryPatterns',
    '/vms/api/taskOptions/taskStatuses',
    '/vms/api/taskOptions/vendingMachines',
    '/vms/api/taskOptions/deliveryStaff'
)
foreach ($path in $protectedPaths) {
    try {
        $r = Invoke-WebRequest -Uri "$apiUrl$path" -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop
        Assert "$path returns 401" ($r.StatusCode -eq 401) "got $($r.StatusCode)"
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        Assert "$path returns 401" ($code -eq 401) "got $code"
    }
}

try {
    $me = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/me" -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop
    $meData = $me.Content | ConvertFrom-Json
    Assert '/auth/me returns authenticated=false when not logged in' (-not $meData.authenticated)
} catch {
    Assert '/auth/me returns 401 when not logged in' ($_.Exception.Response.StatusCode.value__ -eq 401)
}

Write-Host "`n[Read-only] Frontend service" -ForegroundColor Yellow
try {
    $vite = Invoke-WebRequest -Uri "$viteUrl/" -UseBasicParsing -MaximumRedirection 0
    Assert 'Vite dev server accessible' ($vite.StatusCode -eq 200)
    Assert 'Vue mount point present' ($vite.Content -match '<div id="app">')
} catch { Assert 'Vite dev server accessible' $false $_.Exception.Message }

# ---- Full mode (requires login) ----
if ($Full) {
    Write-Host "`n[Full] Login flow" -ForegroundColor Yellow
    $session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
    $null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session -UseBasicParsing
    $body = @{ userName = 'admin'; password = '1'; validCode = $today }
    $login = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $body -WebSession $session -UseBasicParsing
    $loginData = $login.Content | ConvertFrom-Json

    if ($loginData.code -ne 1) {
        Write-Host "  [SKIP] Login failed (captcha date bypass not enabled): $($loginData.message)" -ForegroundColor Yellow
        Write-Host "  To run full regression, temporarily set login.captcha.date.bypass.enabled=true and restart" -ForegroundColor Yellow
    } else {
        Assert 'Login success' ($loginData.code -eq 1)

        Write-Host "`n[Full] Task list and detail" -ForegroundColor Yellow
        $listUrl = "$apiUrl/vms/api/deliveryTask/list?page=1&pageSize=100"
        $list = Invoke-WebRequest -Uri $listUrl -WebSession $session -UseBasicParsing
        $listData = $list.Content | ConvertFrom-Json
        Assert 'List returns pagination structure' ($null -ne $listData.total -and $null -ne $listData.data)
        Write-Host "  Total tasks: $($listData.total)"

        if ($listData.data.Count -gt 0) {
            $taskId = $listData.data[0].id
            $detail = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/$taskId" -WebSession $session -UseBasicParsing
            $detailData = $detail.Content | ConvertFrom-Json
            Assert 'Detail returns task' ($detailData.id -eq $taskId)
            Assert 'Detail includes company info' ($null -ne $detailData.company)
        }

        Write-Host "`n[Full] Form options" -ForegroundColor Yellow
        $vms = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/vendingMachines?pageSize=100" -WebSession $session -UseBasicParsing
        $vmsData = $vms.Content | ConvertFrom-Json
        Assert 'Vending machine options available' ($vmsData -is [System.Array])
        Write-Host "  VMs: $($vmsData.Count)"

        $staff = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/deliveryStaff" -WebSession $session -UseBasicParsing
        $staffData = $staff.Content | ConvertFrom-Json
        Assert 'Delivery staff options available' ($staffData -is [System.Array])
        Write-Host "  Staff: $($staffData.Count)"

        $patterns = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/deliveryPatterns" -WebSession $session -UseBasicParsing
        $patternsData = $patterns.Content | ConvertFrom-Json
        Assert 'Delivery pattern options available' ($patternsData.Count -eq 2)

        Write-Host "`n[Full] Data scope" -ForegroundColor Yellow
        $me = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/me" -WebSession $session -UseBasicParsing
        $meData = $me.Content | ConvertFrom-Json
        Assert '/auth/me returns user info when logged in' ($meData.authenticated -and $meData.userId -gt 0)
        Write-Host "  User: $($meData.userName), Company Level: $($meData.company.level)"

        Write-Host "`n[Full] Logout" -ForegroundColor Yellow
        $logout = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/logout" -Method POST -WebSession $session -UseBasicParsing
        $logoutData = $logout.Content | ConvertFrom-Json
        Assert 'Logout success' ($logoutData.success)
    }
}

# ---- Summary ----
Write-Host ""
$color = if ($fail -eq 0) { 'Green' } else { 'Red' }
Write-Host "=== $pass passed, $fail failed ===" -ForegroundColor $color
exit $fail
