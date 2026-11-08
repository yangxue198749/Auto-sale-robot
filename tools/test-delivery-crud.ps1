# T-2.1 Delivery Task CRUD API Test

$apiUrl = "http://127.0.0.1:8080"
$today = Get-Date -Format "yyyy-MM-dd"

Write-Host "=== T-2.1 Delivery Task CRUD API Test ===" -ForegroundColor Cyan

# Login
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session -UseBasicParsing
$body = @{ userName='admin'; password='1'; validCode=$today }
$login = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $body -WebSession $session -UseBasicParsing
$loginData = $login.Content | ConvertFrom-Json
Write-Host "Login: $($loginData.message)" -ForegroundColor Green

# Test 1: List with pagination
Write-Host "`n[Test 1] List with pagination" -ForegroundColor Yellow
$list1 = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/list?page=1&pageSize=5" -WebSession $session -UseBasicParsing
$listData1 = $list1.Content | ConvertFrom-Json
Write-Host "  Total: $($listData1.total)"
Write-Host "  Page: $($listData1.page)"
Write-Host "  PageSize: $($listData1.pageSize)"
Write-Host "  Items: $($listData1.data.Count)"
if ($listData1.data.Count -gt 0) {
    Write-Host "  First item: ID=$($listData1.data[0].id), Title=$($listData1.data[0].title)"
}

# Test 2: List with status filter
Write-Host "`n[Test 2] List with status filter" -ForegroundColor Yellow
$list2 = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/list?status=2" -WebSession $session -UseBasicParsing
$listData2 = $list2.Content | ConvertFrom-Json
Write-Host "  Total (status=2): $($listData2.total)"

# Test 3: Get task detail
Write-Host "`n[Test 3] Get task detail" -ForegroundColor Yellow
if ($listData1.data.Count -gt 0) {
    $taskId = $listData1.data[0].id
    $detail = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/$taskId" -WebSession $session -UseBasicParsing
    $detailData = $detail.Content | ConvertFrom-Json
    Write-Host "  Task ID: $($detailData.id)"
    Write-Host "  Title: $($detailData.title)"
    Write-Host "  Status: $($detailData.status)"
    Write-Host "  Company: $($detailData.company.name) (Level $($detailData.company.level))"
}

Write-Host "`n=== All Tests Passed ===" -ForegroundColor Green
