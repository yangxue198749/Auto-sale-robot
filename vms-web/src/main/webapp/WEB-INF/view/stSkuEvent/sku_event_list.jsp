<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>商品促销策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/dist/js/strategy.setSalePromotion.js"></script>
    <script type="text/javascript" src="${path}/vmsjs/st_sku_event.js"></script>
    <!--[if lt IE 9]>
    <script src="${path}/plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <!------ 公用左侧导航 start ------>
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=stSkuEvent&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">商品促销策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
        <c:if test="${u:isPermitted('stSkuEvent-add')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/stSkuEvent/add" data-toggle="modal" data-target="#addSalePromotion">新增</button></c:if>
        <c:if test="${u:isPermitted('stSkuEvent-update')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/stSkuEvent/delete">删除</button></c:if>
        <c:if test="${u:isPermitted('stSkuEvent-delete')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/stSkuEvent/update" data-toggle="modal" data-target="#addSalePromotion" data-must="true" data-set="#pagediv table tbody input:checkbox" select-one="true">修改</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/stSkuEvent/table" method="POST" id ="search-form"  class="unKeyCode">
            <div class="main_commonhead hl30">
                <section>
                    <strong>策略名称:</strong>
                    <div class="form-group w160 pull-left"><input name="searchValue" id="searchValue" class="form-control input-sm" type="text" placeholder="请输入策略名称"></div>
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
                    <li class="wh12">策略名称</li>
                    <li class="wh25">策略说明</li>
                    <li class="wh10">详情</li>
                    <li class="wh10">创建人</li>
                    <li class="wh15">创建时间</li>
                    <li class="wh10">修改人</li>
                    <li class="wh15">修改时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            <jsp:include page="/stSkuEvent/table" flush="true"></jsp:include>
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----查看商品促销策略详情 模态框 start----->
    <div class="modal fade" id="viewSalePromotion" tabindex="-1" role="dialog" aria-labelledby="查看商品促销策略" aria-hidden="true">

    </div>
    <!-----查看商品促销策略详情 模态框 end----->
    <!-----新建商品促销策略 模态框 start----->
    <div class="modal fade" id="addSalePromotion" tabindex="-1" role="dialog" aria-labelledby="新建商品促销策略" aria-hidden="true">

    </div>
    <!-----新建价格策略 模态框 end----->


</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
        StSkuEvent.init();
    })
</script>