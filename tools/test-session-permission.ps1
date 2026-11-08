# T-1.7 会话与权限验收测试

$apiUrl = "http://127.0.0.1:8080"

Write-Host "=== T-1.7 Session & Permission Test ===" -ForegroundColor Cyan

# Test 1: Access protected resource without cookie
Write-Host "`n[Test 1] Access without cookie" -ForegroundColor Yellow
$response1 = curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/vms/api/deliveryTask/listAll
if ($response1 -eq "401") {
    Write-Host "  [PASS] Returns 401 without cookie" -ForegroundColor Green
} else {
    Write-Host "  [FAIL] Expected 401, got $response1" -ForegroundColor Red
}

# Test 2: Login and access protected resource
Write-Host "`n[Test 2] Login and access" -ForegroundColor Yellow
$session2 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session2 -UseBasicParsing
# Use date bypass for testing (enable in config if needed)
$today = Get-Date -Format "yyyy-MM-dd"
$loginBody = @{ userName='admin'; password='1'; validCode=$today }
$loginResponse = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $loginBody -WebSession $session2 -UseBasicParsing
$loginData = $loginResponse.Content | ConvertFrom-Json

if ($loginData.code -eq 1) {
    Write-Host "  Login successful" -ForegroundColor Gray
    $protectedResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/listAll" -WebSession $session2 -UseBasicParsing
    if ($protectedResponse.StatusCode -eq 200) {
        Write-Host "  [PASS] Can access protected resource after login" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] Cannot access protected resource" -ForegroundColor Red
    }
} else {
    Write-Host "  [SKIP] Login failed (date bypass may be disabled)" -ForegroundColor Yellow
}

# Test 3: Logout and verify session invalidated
Write-Host "`n[Test 3] Logout" -ForegroundColor Yellow
if ($loginData.code -eq 1) {
    $logoutResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/logout" -Method POST -WebSession $session2 -UseBasicParsing
    $logoutData = $logoutResponse.Content | ConvertFrom-Json
    if ($logoutData.success -eq $true) {
        Write-Host "  Logout successful" -ForegroundColor Gray
        # Try to access protected resource after logout
        try {
            $afterLogout = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/listAll" -WebSession $session2 -UseBasicParsing -ErrorAction Stop
            Write-Host "  [FAIL] Can still access after logout" -ForegroundColor Red
        } catch {
            if ($_.Exception.Response.StatusCode.value__ -eq 401) {
                Write-Host "  [PASS] Session invalidated after logout" -ForegroundColor Green
            } else {
                Write-Host "  [FAIL] Unexpected error after logout" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "  [FAIL] Logout failed" -ForegroundColor Red
    }
} else {
    Write-Host "  [SKIP] Not logged in" -ForegroundColor Yellow
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Cyan
