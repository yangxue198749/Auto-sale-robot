<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>应用促销策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/uniqueCode.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=uniqueCode&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机MAC地址</strong></h2>
            <div class="btn-group pull-right mr20" data-toggle="buttons">
                <c:if test="${u:isPermitted('uniqueCode-unbind')}">
                    <button type="button" class="btn btn-default btn-lg2 btn_unbind" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/uniqueCode/unbind">解除绑定</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/uniqueCode/table" method="POST" id ="search-form"  class="unKeyCode">
            <div class="main_commonhead hl30">
                <section class="diySltInput">
                    <strong>搜索:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="searchType">
                            <option value="1">售货机编号</option>
                            <option value="2">点位</option>
                            <option value="3">硬件编号</option>
                            <option value="4">所属公司</option>
                        </select>
                    </div>
                    <div class="form-group w160 pull-left">
                        <input class="form-control input-sm" name="searchValue" type="text" placeholder="输入内容">
                    </div>
                </section>
                <div class="diyBtnGroup pull-left">
                    <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                    <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>
            </div>
        </form>
        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh17">售货机编号</li>
                    <li class="wh20">区域</li>
                    <li class="wh30">点位</li>
                    <li class="wh30">MAC地址</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/uniqueCode/table" flush="true"></jsp:include>
            </div>
        </div>
    </div>

</div>
</body>
</html>

<script type="text/javascript">
    $(function(){
        uniqueCode.init();
    });

</script>