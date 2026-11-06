<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>点位信息 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=oSu9HuVKHVoCmT3jB1dRsQ8p2NdQLT1q"></script>
    <script type="text/javascript" src="${path}/vmsjs/mt_node.js"></script>
    <!--[if lt IE 9]>
    <script src="${path}/plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="mp_leftmenu">
	        <div class="menu_wrap" id="LeftMenuGroup">
	           <jsp:include page="/loadMenu?menuCode=mtNode&code=operate-manage"/>
	        </div>
    	</div>
	    <div class="mp_topheader fixed">
	        <jsp:include page="/home?code=operate-manage"/>
	        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
	    </div>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">点位信息</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
<c:if test="${u:isPermitted('mtNode-add')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/mtNode/add"  data-toggle="modal" data-target="#addNode">新增</button></c:if>
    <c:if test="${u:isPermitted('mtNode-delete')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/mtNode/delete">删除</button></c:if>
        <c:if test="${u:isPermitted('mtNode-update')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/mtNode/update" data-toggle="modal" data-target="#addNode" data-must="true" data-set="#pagediv table tbody input:checkbox" select-one="true">修改</button></c:if>
            </div>
            <div class="btn-group pull-right mr20" data-toggle="buttons">
            <c:if test="${u:isPermitted('mtNode-deliveryList')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#setDelivery" href="${path}/mtNode/deliveryList" data-toggle="modal" data-target="#setDelivery" data-must="true" data-set="#pagediv table tbody input:checkbox">配置配送员</button></c:if>
                <c:if test="${u:isPermitted('mtNode-repairList')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#setRepair" href="${path}/mtNode/repairList" data-toggle="modal" data-target="#setRepair" data-must="true" data-set="#pagediv table tbody input:checkbox">配置维修员</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form  action="${path}/mtNode/table" id="search-form" name="search-form" class="unKeyCode" >
                <section class="diySltInput">
                    <strong>搜索:</strong>
                    <div class="form-group w80 pull-left">
                        <select class="form-control input-sm" name="searchType">
                            <option value="1">点位名称</option>
                            <option value="3">维修员</option>
                            <option value="2">配送员</option>
                            <option value="4">物业名称</option>
                            <option value="5">客户名</option>
                        </select>
                    </div>
                    <div class="form-group w120 pull-left">
                        <input class="form-control input-sm" name="searchValue" type="text" placeholder="请输入搜索内容">
                    </div>
                </section>
                <section>
                    <strong>区域:</strong>
                    <div class="form-group pull-left ">
                        <select class="form-control input-sm w100 pull-left mr5" name="province" id="provinceSelect" onchange="refreshCitySelect()"></select>
                        <select class="form-control input-sm w100 pull-left mr5" name="city" id="citySelect" onchange="refreshCountrySelect()"></select>
                        <select class="form-control input-sm w100 pull-left" name="area" id="countrySelect"></select>
                    </div>
                </section>
                <section>
                    <strong>场景:</strong>
                    <select class="form-control w100 input-sm pull-left" name="businessDistrictId">
                        <option value="">选择场景</option>
                        <c:forEach items="${bdlist}" var="bd">
                            <option value="${bd.id}">${bd.businessDistrictName}</option>
                        </c:forEach>
                    </select>
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
		        <li class="wh3"><input type="checkbox" value=""></li>
		        <li class="wh10">点位名称</li>
		        <li class="wh5">售货机数量</li>
		        <li class="wh15">区域</li>
		        <li class="wh5">场景</li>
		        <li class="wh5">维修员</li>
		        <li class="wh5">配送员</li>
		        <li class="wh15">详细地址</li>
		        <li class="wh5">合伙人</li>
		        <li class="wh5">负责补货方</li>
		        <li class="wh5">详情信息</li>
		        <li class="wh11">创建信息</li>
		        <li class="wh11">修改信息</li>
		    </ul>
		</div>
		<!--theadscroll end-->
		<div  id="pagediv">
            <jsp:include page="/mtNode/table" flush="true"></jsp:include>
		</div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建点位 模态框 start----->
    <div class="modal fade" id="addNode" role="dialog" aria-labelledby="新增点位" aria-hidden="true">

    </div>
    <!-----配置配送员 模态框 end----->

    <div class="modal fade" id="setDelivery" tabindex="-1" role="dialog" aria-labelledby="配置配送员" aria-hidden="true">

    </div>
    <!-----配置维修员 模态框 end----->

    <div class="modal fade" id="setRepair" tabindex="-1" role="dialog" aria-labelledby="配置维修员" aria-hidden="true">

    </div>


    <!--详情信息 -->
    <div class="modal fade" id="detailInfo" tabindex="-1" role="dialog" aria-labelledby="详细信息" aria-hidden="true">

    </div>


</div>
</body>
</html>
<script type="text/javascript" src="${path}/vmsjs/area.js"></script>

<script type="text/javascript">
    var area = '${u:getAreaJson()}';
    // 将字符串转成JSON对象
    var areaJson = eval("(" + area + ")");
    // 页面加载完成后执行的方法
    $(function(){
        renderProvince();
        var selectedProvinceId = $("#provinceSelect").val();
        renderCity(selectedProvinceId);
        var selectedCityeId = $("#citySelect").val();
        renderCountry(selectedCityeId);
        MtNode.init();
    });

</script>