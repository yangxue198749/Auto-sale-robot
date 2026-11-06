<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>收款管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--[if lt IE 9]>
    <script src="${path}/plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="mp_leftmenu">
            <div class="menu_wrap" id="LeftMenuGroup">
                <jsp:include page="/loadMenu?menuCode=beneficiary-manage-list&code=finance-manage"/>
            </div>
        </div>
        <div class="mp_topheader fixed">
            <jsp:include page="/home?code=finance-manage"/>
            <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
        </div>
    </div>
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">收款管理</strong></h2>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">

            <div class="commonTable" id="pagediv">
                    <jsp:include page="/beneficiaryManage/listPage" flush="true"/>
            </div>

        </div>
    </div>

</div>
</body>
</html>
