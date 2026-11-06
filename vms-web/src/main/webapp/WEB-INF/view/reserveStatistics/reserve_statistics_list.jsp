<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>预定统计 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/reserveStatistics.js"></script>
</head>

<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=reserve-statistics-list&code=statistical-analysis"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">预定统计</strong></h2>
            <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form  method="POST" id="search-form">
                <section>
                    <strong>售货机编号:</strong>
                    <div class="form-group w120 pull-left">
                        <input class="form-control input-sm" name="innerCode" type="text" placeholder="请输入内容">
                    </div>
                </section>
                <section>
                    <strong>商品名称:</strong>
                    <div class="form-group w120 pull-left">
                        <input class="form-control input-sm" name="skuName" type="text" placeholder="不包含商品规格">
                    </div>
                </section>
                <section>
                    <strong>订单日期:</strong>
                    <div class="form-group w120 pull-left">
                        <input class="form-control input-sm" id="DateDefault" value="${date}" type="text" name="statisticsDate" placeholder="选择订单日期">
                    </div>
                </section>
                <div class="diyBtnGroup pull-left">
                    <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                    <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh20">日期</li>
                    <li class="wh15">售货机编号</li>
                    <li class="wh20">商品</li>
                    <li class="wh15">待配送订单</li>
                    <li class="wh15">已配送订单</li>
                    <li class="wh15">已取货订单</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <%--<jsp:include page="/reserveStatistics/listPage" flush="true"/>--%>
            </div>
        </div>
    </div>

</div>
</body>
</html>
<script type="text/javascript">
    $(function () {
        ReserveStistics.init();
    })
</script>
