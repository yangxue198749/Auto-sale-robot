# T-1.6 Login Security Test Script

$apiUrl = "http://127.0.0.1:8080"
$today = Get-Date -Format "yyyy-MM-dd"

Write-Host "=== T-1.6 Login Security Test ===" -ForegroundColor Cyan

# Test 1: Wrong captcha (should fail)
Write-Host "`n[Test 1] Wrong captcha login" -ForegroundColor Yellow
$session1 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session1 -UseBasicParsing
$loginBody = @{ userName='admin'; password='1'; validCode='wrong' }
$loginResponse = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $loginBody -WebSession $session1 -UseBasicParsing
$loginData = $loginResponse.Content | ConvertFrom-Json
if ($loginData.code -eq 4) {
    Write-Host "  [PASS] Wrong captcha rejected" -ForegroundColor Green
} else {
    Write-Host "  [FAIL] Wrong captcha not rejected: $($loginData.message)" -ForegroundColor Red
}

# Test 2: Date bypass (should fail in production config)
Write-Host "`n[Test 2] Date bypass captcha (production config)" -ForegroundColor Yellow
$session2 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session2 -UseBasicParsing
$loginBody2 = @{ userName='admin'; password='1'; validCode=$today }
$loginResponse2 = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $loginBody2 -WebSession $session2 -UseBasicParsing
$loginData2 = $loginResponse2.Content | ConvertFrom-Json
if ($loginData2.code -eq 4) {
    Write-Host "  [PASS] Date bypass rejected (production correct)" -ForegroundColor Green
} elseif ($loginData2.code -eq 1) {
    Write-Host "  [WARN] Date bypass accepted (dev environment)" -ForegroundColor Yellow
} else {
    Write-Host "  [FAIL] Unexpected response: $($loginData2.message)" -ForegroundColor Red
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Cyan
