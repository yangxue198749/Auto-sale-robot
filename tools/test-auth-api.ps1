# test-auth-api.ps1
# Test /auth/me endpoint with authentication

$baseUrl = "http://127.0.0.1:8080"
$today = Get-Date -Format "yyyy-MM-dd"

# Create a session object
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Step 1: Get captcha (establishes session)
Write-Host "Step 1: Getting captcha..."
Invoke-WebRequest -Uri "$baseUrl/createCode" -WebSession $session -UseBasicParsing | Out-Null
Write-Host "Session established"

# Step 2: Login
Write-Host "`nStep 2: Logging in..."
$loginBody = @{
    userName = "admin"
    password = "1"
    validCode = $today
}
$loginResponse = Invoke-WebRequest -Uri "$baseUrl/submitLogin" -Method POST -Body $loginBody -WebSession $session -UseBasicParsing
Write-Host "Login response: $($loginResponse.Content)"

# Step 3: Test /auth/me
Write-Host "`nStep 3: Testing /auth/me endpoint..."
$meResponse = Invoke-WebRequest -Uri "$baseUrl/vms/api/auth/me" -WebSession $session -UseBasicParsing
Write-Host "Status: $($meResponse.StatusCode)"
Write-Host "Response:"
$meResponse.Content | ConvertFrom-Json | Format-List

# Step 4: Test /vms/api/deliveryTask/listAll with auth
Write-Host "`nStep 4: Testing /vms/api/deliveryTask/listAll with auth..."
$tasksResponse = Invoke-WebRequest -Uri "$baseUrl/vms/api/deliveryTask/listAll" -WebSession $session -UseBasicParsing
Write-Host "Status: $($tasksResponse.StatusCode)"
Write-Host "Response preview: $($tasksResponse.Content.Substring(0, [Math]::Min(200, $tasksResponse.Content.Length)))..."

Write-Host "`nTest completed successfully!"
