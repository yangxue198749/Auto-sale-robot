<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>商品效期监控 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/skuValidity.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=sku-validity-list&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">商品效期监控</strong></h2>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form method="post" action="${path}/skuValidity/table" name="form" id="search-form" class="unKeyCode">
                <section class="diySltInput">
                    <strong>搜索:</strong>
                    <div class="form-group w90 pull-left">
                        <select class="form-control input-sm" name="searchType">
                            <option value="1">商品名称</option>
                            <option value="2">售货机编号</option>
                            <option value="3">点位</option>
                        </select>
                    </div>
                    <div class="form-group w160 pull-left">
                        <input class="form-control input-sm" type="text" name="searchValue" placeholder="请输入内容">
                    </div>
                </section>
                <section>
                    <strong>商品编码:</strong>
                    <div class="form-group w160 pull-left"><input name="skuCode" class="form-control input-sm" type="text" placeholder="请输入商品编码"></div>
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
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh10">商品ID</li>
                    <li class="wh15">商品名称</li>
                    <li class="wh5">商品规格</li>
                    <li class="wh5">剩余天数</li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh5">货道编号</li>
                    <li class="wh5">货道余量</li>
                    <li class="wh10">生产日期</li>
                    <li class="wh10">有效期至</li>
                    <li class="wh10">生产批号</li>
                    <li class="wh10">点位</li>
                    <li class="wh5">配送员</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/skuValidity/table" flush="true"></jsp:include>
            </div>
            <!--table footer start-->
            <!--table footer end-->
        </div>
    </div>

</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
        skuValidity.init();
    });

</script>
