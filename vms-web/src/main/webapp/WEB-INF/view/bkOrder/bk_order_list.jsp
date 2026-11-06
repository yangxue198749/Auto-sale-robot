<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>预定订单 | 贩售机管理系统</title>
    <!--依赖jQuery bootstrap-->
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--sim_js-->
    <script type="text/javascript" src="${path}/vmsjs/bk_order.js?v=2017102501"></script>
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
                <li><a class="chrome" target="_blank" href="http://www.google.cn/chrome/browser/desktop/index.html">谷歌浏览器(推荐)</a>
                </li>
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
            <jsp:include page="/loadMenu?menuCode=bk-order-list&code=statistical-analysis"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!-- 公用头部导航 end -->
    <!-- 公用主体部分 start -->
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">预定订单</strong></h2>
            <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form action="${path}/bk/order/page" method="POST" id="search-form">
                <section>
                    <strong>搜索类型:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="searchType">
                            <option value="1">售货机编号</option>
                            <option value="2">订单号</option>
                            <option value="3">点位名称</option>
                            <option value="4">手机号码</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>搜索内容:</strong>
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
                    <strong>出货状态:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="vendStatus">
                            <option value="">请选择</option>
                            <option value="1">未取货</option>
                            <option value="2">出货成功</option>
                            <option value="3">出货失败</option>
                            <option value="4">出货未知</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>订单状态:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="orderStatus">
                            <option value="">请选择</option>
                            <option value="1">待配送</option>
                            <option value="2">已取消</option>
                            <option value="3">配送中</option>
                            <option value="4">已送达</option>
                            <option value="5">未送达</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>订单日期:</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" value="${date}" id="DateStartOne" placeholder="开始日期" name="beginDate">
                    </div>
                    <strong class="ml5">-</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" value="${date}" id="DateEndOne" placeholder="结束日期" name="endDate">
                    </div>
                    <div class="clearfix"></div>
                </section>
                <button type="button" class="btn btn-default f12 btn-lg2" id="searchButton">查询</button>
                <button type="reset" class="btn btn-default f12 btn-lg2" id="resetButton">重置</button>
                <div class="clearfix"></div>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh7">点位名称</li>
                    <li class="wh7">售货机编号</li>
                    <li class="wh8">商品名称</li>
                    <li class="wh8">支付金额(分)</li>
                    <li class="wh7">预定日期</li>
                    <li class="wh8">出货时间</li>
                    <li class="wh5">出货状态</li>
                    <li class="wh8"><span class="cf_red">失败原因</span></li>
                    <li class="wh5">订单状态</li>
                    <li class="wh8">商品退款</li>
                    <li class="wh7">押金退款</li>
                    <li class="wh5">货道编号</li>
                    <li class="wh10">订单号</li>
                    <li class="wh7">用户手机号</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/bk/order/page" flush="true"/>
            </div>
        </div>
    </div>
</div>

</body>
</html>
<script type="text/javascript">
    $(function () {
        BkOrder.init();
    })
</script>
