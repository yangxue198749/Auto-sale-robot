<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>应用促销策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/mt_event.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=mtVmEvent&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">应用促销策略</strong></h2>
            <div class="btn-group pull-right mr20" data-toggle="buttons">
        <c:if test="${u:isPermitted('mtVmEvent-add-sku')}"><button type="button" class="btn btn-default btn-lg2"  href="${path}/mtVmEvent/add/sku" data-toggle="modal" validate-vmModel-promotion="true" data-target="#applyShopSales">应用商品策略</button></c:if>
        <c:if test="${u:isPermitted('mtVmEvent-add-vm')}"><button type="button" class="btn btn-default btn-lg2"  href="${path}/mtVmEvent/add/vm" data-toggle="modal" validate-vmModel-promotion="true" data-target="#applyVmboxSales">应用整机策略</button></c:if>
        <c:if test="${u:isPermitted('mtVmEvent-delete')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/mtVmEvent/delete">取消</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/mtVmEvent/table" method="POST" id ="search-form"  class="unKeyCode">
        <div class="main_commonhead hl30">
            <section>
                <strong>区域:</strong>
                <div class="form-group pull-left w100 mr5">
                    <select class="form-control input-sm pull-left" name="province" id="provinceSelect" onchange="refreshCitySelect()"></select>
                </div>
                <div class="form-group pull-left w100 mr5">
                    <select class="form-control input-sm pull-left" name="city" id="citySelect" onchange="refreshCountrySelect()"></select>
                </div>
                <div class="form-group pull-left w100">
                    <select class="form-control input-sm pull-left" name="area" id="countrySelect"></select>
                </div>
            </section>
            <section>
                <strong>场景:</strong>
                <div class="form-group pull-left w120">
                    <select class="form-control input-sm" name="businessDistrictId" >
                        <option value="">请选择</option>
                        <c:forEach items="${bdlist}" var="bd">
                            <option value="${bd.id}">${bd.businessDistrictName}</option>
                        </c:forEach>
                    </select>
                </div>
            </section>
            <section>
                <strong>点位:</strong>
                <div class="form-group w120 pull-left"><input class="form-control input-sm" name="searchNodeName" type="text" placeholder="请输入点位名称"></div>
            </section>
            <section>
                <strong>售货机编号:</strong>
                <div class="form-group w120 pull-left"><input class="form-control input-sm" name="searchInnerCode" type="text" placeholder="请输入售货机编号"></div>
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
                    <li class="wh7">售货机编号</li>
                    <li class="wh10">点位</li>
                    <li class="wh15">区域(省市区)</li>
                    <li class="wh10">具体位置</li>
                    <li class="wh7">策略类型</li>
                    <li class="wh10">策略名称</li>
                    <li class="wh10">生效日期</li>
                    <li class="wh8">生效时段</li>
                    <li class="wh8">详情</li>
                    <li class="wh12">创建信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            <jsp:include page="/mtVmEvent/table" flush="true"></jsp:include>
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----应用商品策略 模态框 start----->
    <div class="modal fade" id="applyShopSales" tabindex="-1" role="dialog" aria-labelledby="应用商品促销策略" aria-hidden="true"></div>
    <!-----选择商品策略 模态框 ----->
    <div class="modal fade" id="selectShop" tabindex="-1" role="dialog" aria-labelledby="选择商品策略" aria-hidden="true"></div>
    <!-----查看要应用的商品策略详情 模态框 ----->
    <div class="modal fade" id="checkTactics" tabindex="-1" role="dialog" aria-labelledby="查看策略详情" aria-hidden="true"></div>
    <!-----应用整机策略 模态框 ----->
    <div class="modal fade" id="applyVmboxSales" tabindex="-1" role="dialog" aria-labelledby="应用整机策略" aria-hidden="true"></div>
    <!-----应用整机 选择售货机 模态框 ----->
    <div class="modal fade" id="selectVmbox" tabindex="-1" role="dialog" aria-labelledby="选择售货机" aria-hidden="true"></div>
    <!-----应用商品 选择售货机 模态框 ----->
    <div class="modal fade" id="selectSkubox" tabindex="-1" role="dialog" aria-labelledby="选择售货机" aria-hidden="true"></div>
    <!-----选择整机策略 模态框 ----->
    <div class="modal fade" id="selectVmboxSales" tabindex="-1" role="dialog" aria-labelledby="选择整机策略" aria-hidden="true"></div>
    <!--查看已经应用的售货机促销mt_vm_detail/商品促销详请mt_sku_detail 模态框-->
    <div class="modal fade" id="checkStInfo" tabindex="-1" role="dialog" aria-labelledby="促销策略详情" aria-hidden="true"></div>
</div>
</body>
</html>
<script type="text/javascript" src="${path}/vmsjs/area.js"></script>
<script type="text/javascript">
    var area = '${u:getAreaJson()}';
    // 将字符串转成JSON对象
    var areaJson = eval("(" + area + ")");
    $(function(){
        renderProvince();
        var selectedProvinceId = $("#provinceSelect").val();
        renderCity(selectedProvinceId);
        var selectedCityeId = $("#citySelect").val();
        renderCountry(selectedCityeId);
        MtEvent.init();
    });

</script>