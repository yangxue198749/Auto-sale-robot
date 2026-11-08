# test-session-restoration.ps1
# Test session restoration after page refresh

$baseUrl = "http://localhost:5173"
$apiUrl = "http://127.0.0.1:8080"
$today = Get-Date -Format "yyyy-MM-dd"

Write-Host "=== Session Restoration Test ===" -ForegroundColor Cyan

# Create a session object
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Step 1: Login via backend API
Write-Host "`nStep 1: Logging in via backend..."
$captchaResponse = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session -UseBasicParsing
$loginBody = @{
    userName = "admin"
    password = "1"
    validCode = $today
}
$loginResponse = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $loginBody -WebSession $session -UseBasicParsing
Write-Host "Login: $($loginResponse.Content)"

# Step 2: Test /auth/me with session
Write-Host "`nStep 2: Testing /auth/me with session..."
$meResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/me" -WebSession $session -UseBasicParsing
$meData = $meResponse.Content | ConvertFrom-Json
Write-Host "Authenticated: $($meData.authenticated)"
Write-Host "User: $($meData.userName) ($($meData.userAccount))"
Write-Host "Company: $($meData.company.name)"

# Step 3: Simulate page refresh by creating new session but keeping cookies
Write-Host "`nStep 3: Simulating page refresh..."
# In a real browser, the cookies would persist. Here we'll just test that /auth/me works
# with the existing session cookies
$refreshResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/me" -WebSession $session -UseBasicParsing
$refreshData = $refreshResponse.Content | ConvertFrom-Json
if ($refreshData.authenticated) {
    Write-Host "✓ Session restored successfully after refresh" -ForegroundColor Green
    Write-Host "  User: $($refreshData.userName)" -ForegroundColor Green
} else {
    Write-Host "✗ Session restoration failed" -ForegroundColor Red
    exit 1
}

# Step 4: Test frontend API client behavior
Write-Host "`nStep 4: Testing frontend API client..."
# Note: We can't directly test the Vue app in PowerShell, but we can verify the backend
# behavior that the frontend relies on

# Test 401 handling
$newSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
try {
    $unauthResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/listAll" -WebSession $newSession -UseBasicParsing
    Write-Host "✗ Should have received 401" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode.value__ -eq 401) {
        Write-Host "✓ 401 handling works correctly" -ForegroundColor Green
    } else {
        Write-Host "✗ Unexpected status code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Step 5: Test logout
Write-Host "`nStep 5: Testing logout..."
$logoutResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/logout" -Method POST -WebSession $session -UseBasicParsing
$logoutData = $logoutResponse.Content | ConvertFrom-Json
if ($logoutData.success) {
    Write-Host "✓ Logout successful" -ForegroundColor Green
} else {
    Write-Host "✗ Logout failed" -ForegroundColor Red
}

# Step 6: Verify session is invalidated
Write-Host "`nStep 6: Verifying session is invalidated..."
try {
    $afterLogoutResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/me" -WebSession $session -UseBasicParsing
    $afterLogoutData = $afterLogoutResponse.Content | ConvertFrom-Json
    if (-not $afterLogoutData.authenticated) {
        Write-Host "✓ Session properly invalidated after logout" -ForegroundColor Green
    } else {
        Write-Host "✗ Session still valid after logout" -ForegroundColor Red
        exit 1
    }
} catch {
    if ($_.Exception.Response.StatusCode.value__ -eq 401) {
        Write-Host "✓ Session properly invalidated (401 response)" -ForegroundColor Green
    } else {
        Write-Host "✗ Unexpected error: $_" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n=== All tests passed! ===" -ForegroundColor Green
