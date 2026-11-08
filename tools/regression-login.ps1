# VMS login-state regression
# Requires: login.captcha.date.bypass.enabled=true (temporary, local/test only)
# Usage: powershell -ExecutionPolicy Bypass -File tools/regression-login.ps1
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ErrorActionPreference = 'Stop'

$base = 'http://127.0.0.1:8080'
$script:pass = 0
$script:fail = 0

function Assert($name, $cond, $detail = '') {
    if ($cond) {
        Write-Host "[PASS] $name" -ForegroundColor Green
        $script:pass++
    } else {
        Write-Host "[FAIL] $name $detail" -ForegroundColor Red
        $script:fail++
    }
}

function Get-Status($url, $session) {
    try {
        $r = Invoke-WebRequest -Uri $url -WebSession $session -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop
        return [int]$r.StatusCode
    } catch {
        if ($_.Exception.Response) { return [int]$_.Exception.Response.StatusCode }
        return 0
    }
}

function Get-Json($url, $session) {
    $r = Invoke-WebRequest -Uri $url -WebSession $session -UseBasicParsing
    return $r.Content | ConvertFrom-Json
}

function Post-Form($path, $body, $session) {
    $r = Invoke-WebRequest -Uri "$base$path" -Method POST -Body $body `
        -ContentType 'application/x-www-form-urlencoded' -WebSession $session -UseBasicParsing
    return $r.Content | ConvertFrom-Json
}

# ---------- 1. Anonymous access must be 401 ----------
$anon = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$listPaths = @(
    '/vms/api/auth/me',
    '/vms/api/mtVm/list',
    '/vms/api/vmModel/list',
    '/vms/api/mtNode/list',
    '/vms/api/sku/list',
    '/vms/api/skuType/list',
    '/vms/api/dictionary/list',
    '/vms/api/sysType/list',
    '/vms/api/taskOptions/deliveryPatterns',
    '/vms/api/taskOptions/taskStatuses',
    '/vms/api/taskOptions/vendingMachines',
    '/vms/api/taskOptions/deliveryStaff',
    '/vms/api/taskSearch/list',
    '/vms/api/taskSearch/taskTypes',
    '/vms/api/deliveryTask/list',
    '/vms/api/repairTask/list',
    '/vms/api/deployTask/list',
    '/vms/api/withdrawTask/list',
    '/vms/api/vmPrice/strategies',
    '/vms/api/stVmChannel/list',
    '/vms/api/stShortcuts/list',
    '/vms/api/stShortcuts/resources',
    '/vms/api/stVmText/list',
    '/vms/api/stVmEnergySave/list',
    '/vms/api/theme/list',
    '/vms/api/stSkuPrice/list',
    '/vms/api/stSkuEvent/list',
    '/vms/api/stVmEvent/list',
    '/vms/api/mtVmEvent/list',
    '/vms/api/mtVmEvent/strategies/sku',
    '/vms/api/mtVmEvent/strategies/vm',
    '/vms/api/mtVmEvent/candidates/sku',
    '/vms/api/mtVmEvent/candidates/vm',
    '/vms/api/vmTheme/themes',
    '/vms/api/vmTheme/resources'
)

Write-Host "== 1. Anonymous 401 ==" -ForegroundColor Cyan
foreach ($p in $listPaths) {
    $s = Get-Status "$base$p" $anon
    Assert "anon401 $p" ($s -eq 401) "got $s"
}

# ---------- 2. Login ----------
Write-Host "== 2. Login ==" -ForegroundColor Cyan
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$today = Get-Date -Format 'yyyy-MM-dd'
$login = Post-Form '/submitLogin' @{ userName = 'admin'; password = '1'; validCode = $today } $sess
Assert 'login code=1' ($login.code -eq 1) ($login | ConvertTo-Json -Compress)
$cookie = $sess.Cookies.GetCookies($base) | Where-Object { $_.Name -eq 'TOUCHARM_VMS' }
Assert 'TOUCHARM_VMS cookie' ($null -ne $cookie)

# ---------- 3. Authenticated list endpoints = 200 ----------
Write-Host "== 3. Authenticated 200 ==" -ForegroundColor Cyan
foreach ($p in $listPaths) {
    $s = Get-Status "$base$p" $sess
    Assert "auth200 $p" ($s -eq 200) "got $s"
}

# ---------- 4. Device-scoped endpoints ----------
Write-Host "== 4. Device-scoped ==" -ForegroundColor Cyan
$vmPage = Get-Json "$base/vms/api/mtVm/list?page=1&pageSize=5" $sess
$vmId = $null
if ($vmPage.data -and $vmPage.data.Count -gt 0) { $vmId = $vmPage.data[0].id }
if ($null -ne $vmId) {
    $devicePaths = @(
        "/vms/api/vmChannel/$vmId/list",
        "/vms/api/vmEnergy/$vmId/list",
        "/vms/api/vmShortcuts/$vmId/list",
        "/vms/api/vmText/$vmId/list",
        "/vms/api/vmTheme/$vmId/entrances",
        "/vms/api/mtVm/$vmId"
    )
    foreach ($p in $devicePaths) {
        $s = Get-Status "$base$p" $sess
        Assert "device200 $p" ($s -eq 200) "got $s"
    }
} else {
    Write-Host "[SKIP] no vm data" -ForegroundColor Yellow
}

$modelPage = Get-Json "$base/vms/api/vmModel/list?page=1&pageSize=5" $sess
if ($modelPage.data -and $modelPage.data.Count -gt 0) {
    $modelId = $modelPage.data[0].id
    $s = Get-Status "$base/vms/api/vmModel/$modelId/channels" $sess
    Assert "model channels 200 (id=$modelId)" ($s -eq 200) "got $s"
}

# ---------- 5. Detail endpoints from lists ----------
Write-Host "== 5. Detail endpoints ==" -ForegroundColor Cyan
function Test-FirstDetail($listUrl, $idField, $detailFormat) {
    $page = Get-Json "$base$listUrl" $sess
    if ($page.data -and $page.data.Count -gt 0) {
        $id = $page.data[0].$idField
        $s = Get-Status ("$base" + ($detailFormat -f $id)) $sess
        Assert "detail $listUrl id=$id" ($s -eq 200) "got $s"
    } else {
        Write-Host "[SKIP] empty list $listUrl" -ForegroundColor Yellow
    }
}
Test-FirstDetail '/vms/api/mtVm/list?pageSize=5' 'id' '/vms/api/mtVm/{0}'
Test-FirstDetail '/vms/api/mtNode/list?pageSize=5' 'id' '/vms/api/mtNode/{0}'
Test-FirstDetail '/vms/api/sku/list?pageSize=5' 'id' '/vms/api/sku/{0}'
Test-FirstDetail '/vms/api/stVmChannel/list?pageSize=5' 'strategyId' '/vms/api/stVmChannel/{0}'
Test-FirstDetail '/vms/api/stShortcuts/list?pageSize=5' 'strategyId' '/vms/api/stShortcuts/{0}'
Test-FirstDetail '/vms/api/stVmText/list?pageSize=5' 'strategyId' '/vms/api/stVmText/{0}'
Test-FirstDetail '/vms/api/stVmEnergySave/list?pageSize=5' 'strategyId' '/vms/api/stVmEnergySave/{0}'
Test-FirstDetail '/vms/api/stSkuPrice/list?pageSize=5' 'strategyId' '/vms/api/stSkuPrice/{0}'
Test-FirstDetail '/vms/api/stSkuEvent/list?pageSize=5' 'strategyId' '/vms/api/stSkuEvent/{0}'
Test-FirstDetail '/vms/api/stVmEvent/list?pageSize=5' 'strategyId' '/vms/api/stVmEvent/{0}'
Test-FirstDetail '/vms/api/mtVmEvent/list?pageSize=5' 'eventId' '/vms/api/mtVmEvent/{0}'
Test-FirstDetail '/vms/api/theme/list?pageSize=5' 'id' '/vms/api/theme/{0}/preview'

# ---------- 6. Write cycles (create -> verify -> delete) ----------
Write-Host "== 6. Write cycles ==" -ForegroundColor Cyan
$stamp = Get-Date -Format 'yyyyMMddHHmmss'
$qaName = "QA-REGRESSION-$stamp"

# 6.1 Text strategy
$r = Post-Form '/vms/api/stVmText/create' @{ strategyName = $qaName; strategyDesc = 'regression'; textContents = 'regression text' } $sess
Assert 'text create code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
$textList = Get-Json ("$base/vms/api/stVmText/list?searchStVmName=" + $qaName) $sess
if ($textList.total -ge 1) {
    $textId = $textList.data[0].strategyId
    $r = Post-Form '/vms/api/stVmText/edit' @{ strategyId = $textId; strategyName = $qaName; strategyDesc = 'regression-edit'; textContents = 'regression text v2' } $sess
    Assert 'text edit code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    $r = Post-Form '/vms/api/stVmText/delete' @{ ids = $textId } $sess
    Assert 'text delete code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
} else {
    Assert 'text create visible in list' $false 'not found'
}

# 6.2 Sku price strategy
$skuPage = Get-Json "$base/vms/api/sku/list?page=1&pageSize=1" $sess
if ($skuPage.data -and $skuPage.data.Count -gt 0) {
    $skuId = $skuPage.data[0].id
    $r = Post-Form '/vms/api/stSkuPrice/save' @{ strategyName = $qaName; strategyDesc = 'regression'; skuIds = "$skuId"; realPrices = '199' } $sess
    Assert 'price create code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    $priceList = Get-Json ("$base/vms/api/stSkuPrice/list?searchValue=" + $qaName) $sess
    if ($priceList.total -ge 1) {
        $priceId = $priceList.data[0].strategyId
        $r = Post-Form '/vms/api/stSkuPrice/delete' @{ ids = $priceId } $sess
        Assert 'price delete code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    } else {
        Assert 'price visible in list' $false 'not found'
    }
} else {
    Write-Host "[SKIP] no sku data for price cycle" -ForegroundColor Yellow
}

# 6.3 Vm event strategy
$r = Post-Form '/vms/api/stVmEvent/save' @{ strategyName = $qaName; strategyDesc = 'regression'; discountType = '2'; discountValue = '50' } $sess
Assert 'vm-event create code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
$veList = Get-Json ("$base/vms/api/stVmEvent/list?searchValue=" + $qaName) $sess
if ($veList.total -ge 1) {
    $veId = $veList.data[0].strategyId
    $r = Post-Form '/vms/api/stVmEvent/delete' @{ ids = $veId } $sess
    Assert 'vm-event delete code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
} else {
    Assert 'vm-event visible in list' $false 'not found'
}

# 6.4 Sku event strategy
if ($skuPage.data -and $skuPage.data.Count -gt 0) {
    $r = Post-Form '/vms/api/stSkuEvent/save' @{ strategyName = $qaName; strategyDesc = 'regression'; skuIds = "$skuId"; discountTypes = '2'; discountValues = '10' } $sess
    Assert 'sku-event create code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    $seList = Get-Json ("$base/vms/api/stSkuEvent/list?searchValue=" + $qaName) $sess
    if ($seList.total -ge 1) {
        $seId = $seList.data[0].strategyId
        $r = Post-Form '/vms/api/stSkuEvent/delete' @{ ids = $seId } $sess
        Assert 'sku-event delete code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    } else {
        Assert 'sku-event visible in list' $false 'not found'
    }
}

# 6.5 Channel strategy (requires a vm model)
$modelPage2 = Get-Json "$base/vms/api/vmModel/list?page=1&pageSize=1" $sess
$modelId2 = $null
if ($modelPage2.data -and $modelPage2.data.Count -gt 0) { $modelId2 = $modelPage2.data[0].id }
if ($null -ne $modelId2) {
    $r = Post-Form '/vms/api/stVmChannel/create' @{ vmModelId = $modelId2; strategyName = $qaName; strategyDesc = 'regression' } $sess
    Assert 'channel create code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    $chList = Get-Json ("$base/vms/api/stVmChannel/list?searchStVmName=" + $qaName) $sess
    if ($chList.total -ge 1) {
        $chId = $chList.data[0].strategyId
        $chDetail = Get-Json "$base/vms/api/stVmChannel/$chId" $sess
        Assert 'channel detail has channels' ($chDetail.channels.Count -gt 0) 'empty channels'
        $r = Post-Form '/vms/api/stVmChannel/delete' @{ ids = $chId } $sess
        Assert 'channel delete code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    } else {
        Assert 'channel visible in list' $false 'not found'
    }

    # 6.6 Energy strategy with one screen-off item
    $r = Post-Form '/vms/api/stVmEnergySave/create' @{ vmModelId = $modelId2; strategyName = $qaName; strategyDesc = 'regression' } $sess
    Assert 'energy create type=success' ($r.type -eq 'success') ($r | ConvertTo-Json -Compress)
    $enList = Get-Json ("$base/vms/api/stVmEnergySave/list?searchStVmName=" + $qaName) $sess
    if ($enList.total -ge 1) {
        $enId = $enList.data[0].strategyId
        $r = Post-Form '/vms/api/stVmEnergySave/detail/create' @{ strategyId = $enId; energySaveType = '3'; startTime = '00:00'; endTime = '01:00'; remark = 'regression' } $sess
        Assert 'energy detail create type=success' ($r.type -eq 'success') ($r | ConvertTo-Json -Compress)
        $enDetail = Get-Json "$base/vms/api/stVmEnergySave/$enId" $sess
        Assert 'energy detail count=1' ($enDetail.details.Count -eq 1) ('count=' + $enDetail.details.Count)
        $r = Post-Form '/vms/api/stVmEnergySave/delete' @{ ids = $enId } $sess
        Assert 'energy delete code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    } else {
        Assert 'energy visible in list' $false 'not found'
    }
} else {
    Write-Host "[SKIP] no vm model for channel/energy cycles" -ForegroundColor Yellow
}

# 6.7 Apply promotion to a device, then cancel
if ($null -ne $vmId) {
    $r = Post-Form '/vms/api/stVmEvent/save' @{ strategyName = $qaName; strategyDesc = 'regression'; discountType = '2'; discountValue = '30' } $sess
    $veList2 = Get-Json ("$base/vms/api/stVmEvent/list?searchValue=" + $qaName) $sess
    if ($veList2.total -ge 1) {
        $veId2 = $veList2.data[0].strategyId
        $todayStr = Get-Date -Format 'yyyy-MM-dd'
        $endStr = (Get-Date).AddDays(1).ToString('yyyy-MM-dd')
        $r = Post-Form '/vms/api/mtVmEvent/apply' @{ eventType = '1'; strategyId = $veId2; vmIds = "$vmId"; beginDate = $todayStr; endDate = $endStr; startTime = '00:00'; endTime = '23:30' } $sess
        Assert 'promotion apply type=success' ($r.type -eq 'success') ($r | ConvertTo-Json -Compress)
        $apList = Get-Json "$base/vms/api/mtVmEvent/list?pageSize=50" $sess
        $applied = $apList.data | Where-Object { $_.eventName -eq $qaName -and $_.vmId -eq $vmId }
        if ($applied) {
            $apDetail = Get-Json ("$base/vms/api/mtVmEvent/" + $applied[0].eventId) $sess
            Assert 'applied detail events>=1' ($apDetail.events.Count -ge 1) 'no events'
            $r = Post-Form '/vms/api/mtVmEvent/delete' @{ ids = $applied[0].eventId } $sess
            Assert 'promotion cancel code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
        } else {
            Assert 'applied event visible' $false 'not found'
        }
        $r = Post-Form '/vms/api/stVmEvent/delete' @{ ids = $veId2 } $sess
        Assert 'vm-event cleanup code=0' ($r.code -eq 0) ($r | ConvertTo-Json -Compress)
    }
} else {
    Write-Host "[SKIP] no vm for promotion apply cycle" -ForegroundColor Yellow
}

# 6.8 Permission: anonymous write must be 401
$postCode = 0
try {
    $null = Invoke-WebRequest -Uri "$base/vms/api/stVmText/create" -Method POST -WebSession $anon -UseBasicParsing -ErrorAction Stop
} catch {
    if ($_.Exception.Response) { $postCode = [int]$_.Exception.Response.StatusCode }
}
Assert 'anon POST stVmText/create 401' ($postCode -eq 401) "got $postCode"

Write-Host ""
Write-Host "=== $script:pass passed, $script:fail failed ==="
exit $script:fail
