<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>下载报表 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=export-report-list&code=statistical-analysis"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">报表下载</strong></h2>
        </div>
        <!--Main CommonHead-->
        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh70">表名</li>
                    <li class="wh20">生成时间</li>
                    <li class="wh10">下载</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/export/report/listPage" flush="true"/>
            </div>
        </div>
    </div>
</div>

</body>
</html>
<script type="text/javascript">
</script>
