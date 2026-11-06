<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>工作台 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<!--[if lt IE 9]>
<div class="goodbye_group">
    <div class="gbwrap">
        <div class="gbbox">
            <h2>为了提供更好的服务，请升级您的浏览器</h2>
            <p>尊敬的用户，您现在使用的浏览器版本过低，请升级后使用VMS相关服务。</p>
            <h4>您可以选择以下浏览器：</h4>
            <ul>
                <li><a class="chrome" target="_blank" href="http://www.google.cn/chrome/browser/desktop/index.html">谷歌浏览器(推荐)</a></li>
                <li><a class="flash-360" target="_blank" href="http://se.360.cn/">360安全浏览器</a></li>
            </ul>
        </div>
    </div>
</div>
<![endif]-->
<div class="mp_container">
    <!------ 公用左侧导航 start ------>
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=personal-workbench&code=operate-manage"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!------ 公用头部导航 end ------>
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">工作台</strong></h2>
        </div>

        <!--Main MyDock start-->
        <div class="mydock_group">

<c:if test="${u:isPermitted('delivery-task-count-today')}">
            <section class="col-md-3 mb20 plr10" id="deliveryCount"></section>
</c:if>
<c:if test="${u:isPermitted('repair-task-count-today')}">
            <section class="col-md-3 mb20 plr10" id="repairCount"></section>
</c:if>
<c:if test="${u:isPermitted('deploy-task-count-today')}">
            <section class="col-md-3 mb20 plr10" id="deployCount"></section>
</c:if>
<c:if test="${u:isPermitted('withdraw-task-count-today')}">
            <section class="col-md-3 mb20 plr10" id="withdrawCount"></section>
</c:if>
<c:if test="${u:isPermitted('add-node-count-today')}">
            <section class="col-md-3 mb20 plr10" id="newNodeCount"></section>
</c:if>
<c:if test="${u:isPermitted('add-vm-count-today')}">
            <section class="col-md-3 mb20 plr10" id="newVmCount"></section>
</c:if>
<c:if test="${u:isPermitted('event-vm-count-today')}">
            <section class="col-md-3 mb20 plr10" id="eventVmCount"></section>
</c:if>
<c:if test="${u:isPermitted('out-stock-vm-count-today')}">
            <section class="col-md-3 mb20 plr10" id="oosVmCount"></section>
</c:if>
<c:if test="${u:isPermitted('order-count-today')}">
            <section class="col-md-3 mb20 plr10" id="salesVolume"></section>
</c:if>
<c:if test="${u:isPermitted('sales-volume-today')}">
            <section class="col-md-3 mb20 plr10" id="salesAmount"></section>
</c:if>

        </div>
        <!--Main MyDock end-->

    </div>
    <!------ 公用主体部分 end ------>


</div>
</body>
</html>

<script>
    $(function () {
        $(".mb20").each(function(){
            var id = $(this).attr('id');
            $(this).load("/workbench/"+id);
            });
    })
</script>

