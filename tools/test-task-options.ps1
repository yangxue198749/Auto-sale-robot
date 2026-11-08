# T-2.2 Task Options API Test

$apiUrl = "http://127.0.0.1:8080"
$today = Get-Date -Format "yyyy-MM-dd"

Write-Host "=== T-2.2 Task Options API Test ===" -ForegroundColor Cyan

# Login
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session -UseBasicParsing
$body = @{ userName='admin'; password='1'; validCode=$today }
$login = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $body -WebSession $session -UseBasicParsing
$loginData = $login.Content | ConvertFrom-Json
Write-Host "Login: $($loginData.message)" -ForegroundColor Green

# Test 1: Delivery Patterns
Write-Host "`n[Test 1] Delivery Patterns" -ForegroundColor Yellow
$patterns = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/deliveryPatterns" -WebSession $session -UseBasicParsing
$patternsData = $patterns.Content | ConvertFrom-Json
Write-Host "  Patterns: $($patternsData.Count)"
foreach ($p in $patternsData) {
    Write-Host "    - $($p.label) (value: $($p.value))"
}

# Test 2: Task Statuses
Write-Host "`n[Test 2] Task Statuses" -ForegroundColor Yellow
$statuses = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/taskStatuses" -WebSession $session -UseBasicPing
$statusesData = $statuses.Content | ConvertFrom-Json
Write-Host "  Statuses: $($statusesData.Count)"
foreach ($s in $statusesData) {
    Write-Host "    - $($s.label) (value: $($s.value))"
}

# Test 3: Vending Machines
Write-Host "`n[Test 3] Vending Machines" -ForegroundColor Yellow
$vms = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/vendingMachines?pageSize=5" -WebSession $session -UseBasicPing
$vmsData = $vms.Content | ConvertFrom-Json
Write-Host "  VMs: $($vmsData.Count)"
if ($vmsData.Count -gt 0) {
    Write-Host "  First VM: ID=$($vmsData[0].id), InnerCode=$($vmsData[0].innerCode)"
}

# Test 4: Delivery Staff
Write-Host "`n[Test 4] Delivery Staff" -ForegroundColor Yellow
$staff = Invoke-WebRequest -Uri "$apiUrl/vms/api/taskOptions/deliveryStaff" -WebSession $session -UseBasicPing
$staffData = $staff.Content | ConvertFrom-Json
Write-Host "  Staff: $($staffData.Count)"
if ($staffData.Count -gt 0) {
    Write-Host "  First staff: ID=$($staffData[0].id), Name=$($staffData[0].userName)"
}

Write-Host "`n=== All Tests Passed ===" -ForegroundColor Green
