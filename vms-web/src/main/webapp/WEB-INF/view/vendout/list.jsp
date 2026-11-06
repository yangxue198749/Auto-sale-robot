<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>出货流水 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/vendout.js?v=20171229"></script>
    <!--[if lt IE 9]>
    <script src="${path}/plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
    <script type="text/javascript" src="${path}/plus/datepicker/js/function.datepicker.js"></script>
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=vendout-list&code=statistical-analysis"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">出货流水</strong></h2>
            <c:if test="${u:isPermitted('vendout-export')}"><button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出数据</button></c:if>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form id="search-form" action="${path}/vendout/table" method="POST">
                <section class="diySltInput">
                    <strong>搜索:</strong>
                    <div class="form-group w80 pull-left">
                        <select class="form-control input-sm" name="searchType">
                            <option value="1">售货机编号</option>
                            <option value="2">流水号</option>
                            <option value="3">点位名称</option>
                        </select>
                    </div>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" name="searchValue" type="text" placeholder="请输入内容">
                    </div>
                </section>
                <section>
                    <strong>商品名称:</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" name="skuName" type="text" placeholder="不包含商品规格">
                    </div>
                </section>
                <section>
                    <strong>支付方式:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="payType">
                            <option value="">请选择</option>
                            <c:forEach items="${payTypeList}" var="payType">
                                <option value="${payType.typeCode}">${payType.typeName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>出货状态:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="outStatus">
                            <option value="">请选择</option>
                            <option value="1">正常</option>
                            <option value="2">失败</option>
                            <option value="3">未知</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>订单日期:</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" id="DateStartOne" placeholder="开始日期" name="beginTime" value="${date}">
                    </div>
                    <strong class="ml5">-</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" id="DateEndOne" placeholder="结束日期" name="endTime" value="${date}">
                    </div>
                    <div class="clearfix"></div>
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
                    <li class="wh10">点位名称</li>
                    <li class="wh5">售货机编号</li>
                    <li class="wh10">流水号</li>
                    <li class="wh5">货道编号</li>
                    <li class="wh10">商品名称</li>
                    <li class="wh5">商品ID</li>
                    <li class="wh5">支付方式</li>
                    <li class="wh5">商品金额</li>
                    <li class="wh5">支付金额</li>
                    <li class="wh5">卡包支付金额</li>
                    <li class="wh5">扣款时间</li>
                    <li class="wh5">出货时间</li>
                    <li class="wh5">出货状态</li>
                    <li class="wh5">失败原因</li>
                    <li class="wh5">退款状态</li>
                    <li class="wh5">备注</li>
                    <li class="wh5">操作</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/vendout/table" flush="true"/>
            </div>
            <!--table footer start-->
            <!--table footer end-->
        </div>
    </div>
    <script>

    </script>


    <!-------------添加备注 addRemarkModel start------------->
    <div class="modal fade" id="addRemarkModel" tabindex="-1" role="dialog" aria-labelledby="添加备注" aria-hidden="true">
    </div>
    <!-------------添加备注 addRemarkModel end------------->

</div>
</body>
</html>

<script type="text/javascript">
    Vendout.init();
</script>
