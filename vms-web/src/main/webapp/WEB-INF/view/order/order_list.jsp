<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>销售订单 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/order.js?v=20171227"></script>
    <script type="text/javascript" src="${path}/plus/datepicker/js/function.datepicker.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=order-list&code=statistical-analysis"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">销售订单</strong></h2>
            <c:if test="${u:isPermitted('order-export')}">
                <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
            </c:if>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/order/listPage" method="POST" id="search-form">
            <div class="main_commonhead hl30">
                <section class="diySltInput">
                    <strong>搜索:</strong>
                    <div class="form-group w80 pull-left">
                        <select class="form-control input-sm" name="searchType">
                            <option value="1">售货机编号</option>
                            <option value="2">订单编号</option>
                            <option value="3">点位名称</option>
                            <option value="4">外部订单号</option>
                            <option value="5">流水号</option>
                        </select>
                    </div>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" placeholder="搜索内容" name="searchValue">
                    </div>
                </section>
                <section>
                    <strong>商品名称:</strong>
                    <div class="form-group w110 pull-left">
                        <input class="form-control input-sm" type="text" placeholder="不包含商品规格" name="skuName">
                    </div>
                </section>
                <section>
                    <strong>订单状态:</strong>
                    <div class="form-group w80 pull-left">
                        <select class="form-control input-sm" name="status">
                            <option value="">请选择</option>
                            <option value="1">已完成</option>
                            <option value="2">待配送</option>
                            <option value="3">已取消</option>
                            <option value="4">配送中</option>
                            <option value="5">已送达</option>
                            <option value="6">未送达</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>业务类型:</strong>
                    <div class="form-group w80 pull-left">
                        <select class="form-control input-sm" name="orderType">
                            <option value="">请选择</option>
                            <option value="1">即售订单</option>
                            <option value="2">预定订单</option>
                           <%-- 5.0.0a 删掉预售 <option value="3">预售订单</option>--%>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>支付方式:</strong>
                    <div class="form-group w80 pull-left">
                        <select class="form-control input-sm" name="payType">
                            <option value="">请选择</option>
                            <c:forEach items="${payTypeList}" var="payType">
                                <option value="${payType.typeCode}">${payType.typeName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>支付日期:</strong>
                    <div class="form-group w90 pull-left">
                        <input class="form-control input-sm" type="text" id="DateStartOne" value="${date}"
                               placeholder="开始日期" name="startDate">
                    </div>
                    <strong class="ml5 pull-left mr5">-</strong>
                    <div class="form-group w90 pull-left">
                        <input class="form-control input-sm" type="text" id="DateEndOne" value="${date}"
                               placeholder="结束日期" name="endDate">
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
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5">点位名称</li>
                    <li class="wh5">售货机编号</li>
                    <li class="wh5">订单编号</li>
                    <li class="wh10">流水号</li>
                    <li class="wh10">外部订单号</li>
                    <li class="wh5">业务类型</li>
                    <li class="wh5">商品ID</li>
                    <li class="wh10">商品名称</li>
                    <li class="wh5">支付方式</li>
                    <li class="wh5">商品金额</li>
                    <li class="wh5">支付金额</li>
                    <li class="wh5">卡包支付金额</li>
                    <li class="wh5">下单时间</li>
                    <li class="wh5">取货有效期</li>
                    <li class="wh5">订单状态</li>
                    <li class="wh5">出货状态</li>
                    <li class="wh5">退款信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/order/listPage" flush="true"/>
            </div>
        </div>
    </div>
</div>

</body>
</html>
<script type="text/javascript">
    $(function () {
        Order.init();
    })
</script>
