<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>B客户转账 | 贩售机管理系统</title>
    <!--依赖jQuery bootstrap-->
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--sim_js-->
    <script type="text/javascript" src="${path}/vmsjs/giro.js"></script>
    <script type="text/javascript" src="${path}/plus/datepicker/js/function.datepicker.js"></script>
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
    <!-- 公用左侧导航 start -->
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=giro-list&code=finance-manage"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=finance-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!-- 公用头部导航 end -->
    <!-- 公用主体部分 start -->
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">B客户转账</strong></h2>
            <button type="button" class="btn btn-default btn-lg2 pull-right mr10" data-toggle="modal" data-target="#transferAccount">转账</button>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh15">客户名称</li>
                    <li class="wh15">客户账号</li>
                    <li class="wh10">客户账号名称</li>
                    <li class="wh10">月份</li>
                    <li class="wh10">应转金额(元)</li>
                    <li class="wh10">销售件数</li>
                    <li class="wh10">详细</li>
                    <li class="wh10">状态</li>
                    <li class="wh10">转账人</li>
                </ul>
            </div>
              <!--theadscroll end-->
              <div id="pagediv">
                <jsp:include page="/giro/listPage" flush="true"/>
              </div>
        </div>
    </div>

</div>
</body>
</html>
