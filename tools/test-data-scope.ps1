#!/usr/bin/env pwsh
# test-data-scope.ps1
# Test data scope filtering for different company levels

$baseUrl = "http://localhost:5173"
$apiUrl = "http://127.0.0.1:8080"
$today = Get-Date -Format "yyyy-MM-dd"

Write-Host "=== Data Scope Filtering Test ===" -ForegroundColor Cyan
Write-Host ""

# Test function
function Test-UserScope {
    param(
        [string]$username,
        [string]$expectedCount,
        [string]$description
    )

    Write-Host "Testing: $description" -ForegroundColor Yellow
    Write-Host "Username: $username" -ForegroundColor Gray

    # Create session
    $session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

    # Get captcha
    $null = Invoke-WebRequest -Uri "$apiUrl/createCode" -WebSession $session -UseBasicParsing

    # Login
    $loginBody = @{
        userName = $username
        password = "1"
        validCode = $today
    }
    $loginResponse = Invoke-WebRequest -Uri "$apiUrl/submitLogin" -Method POST -Body $loginBody -WebSession $session -UseBasicParsing
    $loginData = $loginResponse.Content | ConvertFrom-Json

    if ($loginData.code -ne 1) {
        Write-Host "  [FAIL] Login failed: $($loginData.message)" -ForegroundColor Red
        return $false
    }

    # Get user info
    $meResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/auth/me" -WebSession $session -UseBasicParsing
    $meData = $meResponse.Content | ConvertFrom-Json

    Write-Host "  User: $($meData.userName) (Company Level: $($meData.company.level))" -ForegroundColor Gray

    # Get delivery tasks
    $tasksResponse = Invoke-WebRequest -Uri "$apiUrl/vms/api/deliveryTask/listAll" -WebSession $session -UseBasicParsing
    $tasksData = $tasksResponse.Content | ConvertFrom-Json

    $actualCount = $tasksData.Count
    Write-Host "  Tasks returned: $actualCount (expected: $expectedCount)" -ForegroundColor Gray

    if ($actualCount -eq $expectedCount) {
        Write-Host "  [PASS] Correct number of tasks returned" -ForegroundColor Green

        # Show task details
        foreach ($task in $tasksData) {
            $companyLevel = if ($task.company) { $task.company.level } else { "N/A" }
            $companyId = if ($task.company) { $task.company.id } else { "N/A" }
            Write-Host "    - Task $($task.id): $($task.title) (Company ID: $companyId, Level: $companyLevel)" -ForegroundColor DarkGray
        }
        return $true
    } else {
        Write-Host "  [FAIL] Incorrect number of tasks returned" -ForegroundColor Red
        Write-Host "  Tasks returned:" -ForegroundColor Gray
        foreach ($task in $tasksData) {
            Write-Host "    - Task $($task.id): $($task.title)" -ForegroundColor Gray
        }
        return $false
    }

    Write-Host ""
}

# Run tests
$results = @()

# Test 1: Level 1 user should see all 6 tasks
$results += Test-UserScope -username "test_level1" -expectedCount 7 -description "Level 1 Company User (should see all tasks)"
Write-Host ""

# Test 2: Level 2 user should see 4 tasks (own company + child companies)
$results += Test-UserScope -username "test_level2a" -expectedCount 4 -description "Level 2 Company User (should see own + child companies)"
Write-Host ""

# Test 3: Level 3 user should see only 1 task (own company only)
$results += Test-UserScope -username "test_level3a1" -expectedCount 1 -description "Level 3 Company User (should see only own company)"
Write-Host ""

# Summary
Write-Host "=== Test Summary ===" -ForegroundColor Cyan
$passed = ($results | Where-Object { $_ -eq $true }).Count
$total = $results.Count
Write-Host "Passed: $passed / $total" -ForegroundColor $(if ($passed -eq $total) { "Green" } else { "Red" })

if ($passed -eq $total) {
    Write-Host "All tests passed!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "Some tests failed!" -ForegroundColor Red
    exit 1
}
