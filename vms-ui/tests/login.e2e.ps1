[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ErrorActionPreference = 'Stop'

$baseUrl = 'http://localhost:5173'
$pass = 0
$fail = 0

function Assert($name, $cond, $detail) {
    if ($cond) {
        Write-Host "[PASS] $name" -ForegroundColor Green
        $script:pass++
    } else {
        Write-Host "[FAIL] $name $detail" -ForegroundColor Red
        $script:fail++
    }
}

$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

$homePage = Invoke-WebRequest -Uri "$baseUrl/" -UseBasicParsing -MaximumRedirection 0
Assert 'Vue homepage HTML' ($homePage.Content -match '<div id="app">') 'no #app'

$captcha = Invoke-WebRequest -Uri "$baseUrl/api/createCode" -WebSession $session -UseBasicParsing
Assert 'captcha is jpeg' ($captcha.Headers.'Content-Type' -match 'image/jpeg')

$today = Get-Date -Format 'yyyy-MM-dd'
$body = @{ userName = 'admin'; password = '1'; validCode = $today }
$login = Invoke-WebRequest -Uri "$baseUrl/api/submitLogin" -Method POST -Body $body -ContentType 'application/x-www-form-urlencoded' -WebSession $session -UseBasicParsing
$loginObj = $login.Content | ConvertFrom-Json
Assert 'login code=1' ($loginObj.code -eq 1) $login.Content
Assert 'login msg' ($loginObj.message -match 'admin')

$cookies = $session.Cookies.GetCookies($baseUrl)
$toucharm = $cookies | Where-Object { $_.Name -eq 'TOUCHARM_VMS' }
Assert 'TOUCHARM_VMS cookie set' ($null -ne $toucharm)
Assert 'cookie HttpOnly' ($toucharm.HttpOnly)

$workbench = Invoke-WebRequest -Uri "$baseUrl/api/workbench/list" -WebSession $session -UseBasicParsing -MaximumRedirection 0
Assert 'workbench 200 with cookie' ($workbench.StatusCode -eq 200)

$tasks = Invoke-WebRequest -Uri "$baseUrl/api/vms/api/deliveryTask/listAll" -WebSession $session -UseBasicParsing
$taskArr = $tasks.Content | ConvertFrom-Json
Assert 'REST returns array' ($taskArr -is [System.Array])
Assert 'task has id' ($taskArr[0].id -gt 0)

# 11. REST 端点鉴权：已登录 200
Assert 'REST 200 with cookie' ($tasks.StatusCode -eq 200)

# 12. REST 端点鉴权：未登录 401
$anon2 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$got401 = $false
try {
    $null = Invoke-WebRequest -Uri "$baseUrl/api/vms/api/deliveryTask/listAll" -WebSession $anon2 -UseBasicParsing -ErrorAction Stop
} catch {
    if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -eq 401) {
        $got401 = $true
    }
}
Assert 'REST 401 without cookie' $got401

$anon = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$blocked = $false
try {
    $null = Invoke-WebRequest -Uri "$baseUrl/api/workbench/list" -WebSession $anon -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop
} catch {
    $blocked = $true
}
Assert 'no-cookie blocked (throws on redirect)' $blocked

Write-Host ""
Write-Host "=== $pass passed, $fail failed ==="
exit $fail
