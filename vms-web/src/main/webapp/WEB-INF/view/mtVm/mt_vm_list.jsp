<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>售货机管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/mtVm.js"></script>
    <script type="text/javascript" src="${path}/vmsjs/area.js"></script>
    <script type="text/javascript" src="${path}/vmsjs/mt_vm_energy_save.js"></script>
    <script type="text/javascript" src="${path}/vmsjs/mt_vm_theme.js"></script>
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
                <li><a class="chrome" target="_blank" href="http://www.google.cn/chrome/browser/desktop/index.html">谷歌浏览器(推荐)</a></li>
                <li><a class="flash-360" target="_blank" href="http://se.360.cn/">360安全浏览器</a></li>
            </ul>
        </div>
    </div>
</div>
<![endif]-->
<div class="mp_container">
    <!------ 公用左侧导航 start ------>
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=vm-manage&code=operate-manage"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!------ 公用头部导航 end ------>
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <!--Main Min topheader-->        
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('vm-add')}"><button type="button" class="btn btn-default btn-lg2" href="${path }/mtVm/addMtVmDialog" data-toggle="modal" data-target="#addAutoMat">新增</button></c:if>
                <c:if test="${u:isPermitted('vm-extend')}"><button type="button" class="btn btn-default btn-lg2" href="${path }/mtVm/extendMtVmDialog" data-toggle="modal" data-target="#addAutoMat" data-must="true" select-one="true" data-set="#pagediv table tbody input[type='checkbox']:checked" validate-model-extend="true">拓展</button></c:if>
                <c:if test="${u:isPermitted('vm-sale-console')}"><button type="button" id="changeSaleBtn" href="${path }/mtVm/changeSale" class="btn btn-default btn-lg2">销售控制</button></c:if>
                <c:if test="${u:isPermitted('vm-edit')}"><button type="button" class="btn btn-default btn-lg2" href="${path }/mtVm/editMtVmDialog" data-toggle="modal" data-target="#editMtVm" data-must="true" select-one="true" data-set="#pagediv table tbody input[type='checkbox']:checked" validate-model-edit="true">修改</button></c:if>
                <c:if test="${u:isPermitted('export-report-vm')}"><button type="button" id="exportVmBtn" class="btn btn-success btn-lg2">导出数据</button></c:if>
            </div>
            <div class="btn-group pull-right mr20" style="margin-top: 2px;" data-toggle="buttons">
                <c:if test="${u:isPermitted('vm-set-channel')}"><button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmChannel/list" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" data-target="#CtrlTheGoods" validate-vmModel="true" validate-task="true">货道设置</button></c:if>
                <c:if test="${u:isPermitted('vm-set-shelf')}"><button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmShelf/list" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" data-target="#CtrlTheGoods" select-one="true"  validate-model-shelf="true">列表页设置</button></c:if>
                <c:if test="${u:isPermitted('vm-set-shortcuts')}"> <button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmShortcuts/list" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" data-target="#CtrlTheFlashBuy" validate-vmModel="true" id="shortcutsBtn" validate-vmModel-shortcuts="true">闪购键设置</button></c:if>
                <c:if test="${u:isPermitted('vm-set-text')}"><button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmText/list" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" data-target="#CtrlTheLight" validate-vmModel-screenHave="true">走马灯设置</button></c:if>
                <c:if test="${u:isPermitted('vm-set-energy')}"><button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmEnergySave/list" data-set="#pagediv table tbody input[type='checkbox']:checked" data-must="true" validate-vmModel="true" data-target="#CtrlTheEnergy" id="energyBtn">节能设置</button></c:if>
                <c:if test="${u:isPermitted('vm-set-price')}"><button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmPrice/list" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" data-target="#CtrlThePrice" validate-vmModel-price="true">价格设置</button></c:if>
                <c:if test="${u:isPermitted('vm-set-theme')}"><button type="button" class="btn btn-default f12 btn-lg2" data-toggle="modal" href="${path}/mtVmTheme/list" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" data-target="#CtrlTheTheme" validate-vmModel-screenHave="true">皮肤设置</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        <form action="${path }/mtVm/listPage" id="search-form" method="POST" class="unKeyCode">
            <section class="diySltInput">
                <strong>搜索:</strong>
                <div class="form-group w100 pull-left">
                    <select class="form-control input-sm" name="searchFiled">
                        <option value="1">售货机编号</option>
                        <option value="2">点位</option>
                        <option value="3">硬件编号</option>
                        <option value="4">所属公司</option>
                    </select>
                </div>
                <div class="form-group w100 pull-left">
                	<input class="form-control input-sm" name="searchFiledValue" type="text" placeholder="请输入关键字">
                </div>
            </section>
            <%--<section>
                <strong>搜索字段:</strong>
                <div class="form-group w70 pull-left">
                    <select class="form-control input-sm" name="searchFiled">
                        <option value="1">售货机编号</option>
                        <option value="2">点位</option>
                        <option value="3">硬件编号</option>
                        <option value="4">所属公司</option>
                    </select>
                </div>
            </section>
            <section>
                <strong>搜索内容:</strong>
                <div class="form-group w100 pull-left">
                    <input class="form-control input-sm" name="searchFiledValue" type="text" placeholder="请输入关键字">
                </div>
            </section>--%>
            <section>
                <strong>售货机型号:</strong>
                <div class="form-group w70 pull-left">
                    <select class="form-control input-sm" name="searchVmModelId">
                      <option value="">请选择</option>
                      <c:forEach items="${vmModelList }" var="vmModel">
                     	 <option value="${vmModel.id }">${vmModel.modelName}</option>
                      </c:forEach>
                    </select>
                </div>
            </section>
            <section>
                <strong>售货机状态:</strong>
                <div class="form-group w70 pull-left">
                    <select class="form-control input-sm" name="searchVmStatus">
                      <option value="">请选择</option>
                      <option value="0">未投放</option>
                      <option value="1">投放中</option>
                      <option value="2">在售</option>
                      <option value="3">停售</option>
                      <option value="4">撤回中</option>
                    </select>
                </div>
            </section>
            <section>
                <strong>区域:</strong>
                <div class="form-group pull-left">
                   <select class="form-control input-sm w70 pull-left mr5" name="province" id="provinceSelect" onchange="refreshCitySelect()"></select>
                   <select class="form-control input-sm w70 pull-left mr5" name="city" id="citySelect" onchange="refreshCountrySelect()"></select>
                   <select class="form-control input-sm w70 pull-left" name="area" id="countrySelect"></select>
                </div>
            </section>
            <section>
                <strong>场景:</strong>
                <div class="form-group w70 pull-left">
                    <select class="form-control input-sm" name="seardchMtBusinessDistrictId">
                      <option value="">请选择</option>
                      <c:forEach items="${mtBusinessDistrictList }" var="mtBusinessDistrict">
                      	<option value="${mtBusinessDistrict.id }">${mtBusinessDistrict.businessDistrictName }</option>
                      </c:forEach>
                    </select>
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
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5"><input type="checkbox" data-set="#pagediv table tbody input[type='checkbox']" onclick="Metronic.changeBox(this)"></li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh10">区域</li>
                    <li class="wh15">点位</li>
                    <li class="wh10">售货机型号</li>
                    <li class="wh10">状态</li>
                    <li class="wh20">查看</li>
                    <li class="wh10">创建记录</li>
                    <li class="wh10">修改记录</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/mtVm/listPage" flush="true"/>
            </div>
        </div>
    </div>
    
    <!-- 模态窗开始 -->
    
    <!-----新建售货机  #addAutoMat 模态框 start----->
    <div class="modal fade" id="addAutoMat" tabindex="-1" role="dialog" aria-labelledby="新建售货机" aria-hidden="true">
    </div>
    <!-----新建售货机  #addAutoMat 模态框 end----->
    
    <!-----修改售货机  #addAutoMat 模态框 start----->
    <div class="modal fade" id="editMtVm" tabindex="-1" role="dialog" aria-labelledby="修改售货机" aria-hidden="true">
    </div>
    <!-----修改售货机  #addAutoMat 模态框 end----->
    
    <!-----新建售货机 选择点位 #Child_choiceLocation 模态框 start----->
    <div class="modal fade" id="Child_choiceLocation" tabindex="-1" role="dialog" aria-labelledby="选择点位" aria-hidden="true">
    </div>
    <!-----新建售货机 选择点位 #Child_choiceLocation 模态框 end----->
    
    <!-----新建售货机 复制设备策略 #Child_copyMatStrategy 模态框 start----->
	<div class="modal fade" id="Child_copyMatStrategy" tabindex="-1" role="dialog" aria-labelledby="选择要复制的售货机策略" aria-hidden="true">
	</div>
	<!-----新建售货机 复制设备策略 #Child_copyMatStrategy 模态框 end----->
	
	<!-------------------------------------走马灯设置：#CtrlTheLight start------------------------------------------------>
	<div class="modal fade" id="CtrlTheLight" tabindex="-1" role="dialog" aria-labelledby="走马灯设置" aria-hidden="true">
	</div>
	<!-------------------------------------走马灯设置：#CtrlTheLight end------------------------------------------------>
	
	
	<!-----查看走马灯 #viewTheLight 模态框 start------->
	<div class="modal fade" id="viewTheLight" tabindex="-1" role="dialog" aria-labelledby="查看走马灯" aria-hidden="true">
	</div>
	<!-----查看走马灯 #viewTheLight 模态框 end------->
	
	<!-------------------------------------闪购键设置：#CtrlTheFlashBuy start------------------------------------------------>
    <div class="modal fade" id="CtrlTheFlashBuy" tabindex="-1" role="dialog" aria-labelledby="闪购键设置" aria-hidden="true">
    </div>
    <!-------------------------------------闪购键设置：#CtrlTheFlashBuy end------------------------------------------------>
    
    
    <!-----选择商品 #Child_choiceTheGoods 模态框 start----->
    <div class="modal fade" id="Child_choiceTheGoods" tabindex="-1" role="dialog" aria-labelledby="选择商品" aria-hidden="true">
    </div>
    <!-----选择商品 #Child_choiceTheGoods 模态框 end----->
    
    <!-----查看闪购键 #viewTheFlashBuy 模态框 start------->
    <div class="modal fade" id="viewTheFlashBuy" tabindex="-1" role="dialog" aria-labelledby="查看闪购键" aria-hidden="true">
    </div>
    <!-----查看闪购键 #viewTheFlashBuy 模态框 end------->
    
    <!-------------------------------------节能设置：#CtrlTheEnergy start------------------------------------------------>
    <div class="modal fade" id="CtrlTheEnergy" tabindex="-1" role="dialog" aria-labelledby="节能设置" aria-hidden="true">
	</div>
	<!-------------------------------------节能设置：#CtrlTheEnergy end------------------------------------------------>
	
	<!-----新建节能策略 #Child_addTheEnergy 模态框 start----->
    <div class="modal fade" id="Child_addTheEnergy" tabindex="-1" role="dialog" aria-labelledby="新建节能策略" aria-hidden="true">
    </div>
    <!-----新建节能策略 #Child_addTheEnergy 模态框 end----->
    
	<!-----修改节能策略 #Child_editTheEnergy" 模态框 start----->
    <div class="modal fade" id="Child_editTheEnergy"" tabindex="-1" role="dialog" aria-labelledby="新建节能策略" aria-hidden="true">
    </div>
    <!-----修改节能策略 #Child_addTheEnergy 模态框 end----->
    
    <!-----查看节能策略 #viewTheEnergy 模态框 start------->
    <div class="modal fade" id="viewTheEnergy" tabindex="-1" role="dialog" aria-labelledby="查看节能策略" aria-hidden="true">
    </div>
    <!-----查看节能策略 #viewTheEnergy 模态框 end------->
    
    
    <!-------------------------------------价格设置：#CtrlThePrice start------------------------------------------------>
    <div class="modal fade" id="CtrlThePrice" tabindex="-1" role="dialog" aria-labelledby="选择价格策略" aria-hidden="true">
    </div>
    <!-------------------------------------价格设置：#CtrlThePrice end------------------------------------------------>
    
    <!-----查看价格 #viewThePrice 模态框 start------->
    <div class="modal fade" id="viewThePrice" tabindex="-1" role="dialog" aria-labelledby="查看价格" aria-hidden="true">
    </div>
    <!-----查看价格 #viewThePrice 模态框 end------->

    <!-------------------------------------皮肤设置：#CtrlTheTheme start------------------------------------------------>
    <div class="modal fade" id="CtrlTheTheme" tabindex="-1" role="dialog" aria-labelledby="选择皮肤策略" aria-hidden="true">
    </div>
    <!-------------------------------------皮肤设置：#CtrlTheTheme end------------------------------------------------>
    
    <!-------------------------------------货道设置：#CtrlTheGoods start------------------------------------------------>
    <div class="modal fade" id="CtrlTheGoods" tabindex="-1" role="dialog" aria-labelledby="货道设置" aria-hidden="true">
    </div>
    <!-------------------------------------货道设置：#CtrlTheGoods end------------------------------------------------>

    <!-----选择商品 #Child_choiceTheGoods 模态框 end----->
    
    <!-----列表页设置 #Child_setTheList 模态框 start----->
    <div class="modal fade" id="Child_setTheList" tabindex="-1" role="dialog" aria-labelledby="列表页设置" aria-hidden="true">
    </div>
    <!-----列表页设置 #Child_setTheList 模态框 end----->
    
    <!-----查看货道 #viewTheGoods 模态框 start----->
    <div class="modal fade" id="viewTheGoods" tabindex="-1" role="dialog" aria-labelledby="查看货道" aria-hidden="true">
    </div>
    <!-----查看货道 #viewTheGoods 模态框 end----->
    
    <!-----查看列表页 #viewTheList 模态框 start----->
    <div class="modal fade" id="viewTheList" tabindex="-1" role="dialog" aria-labelledby="查看列表页" aria-hidden="true">
    </div>
    <!-----查看列表页 #viewTheList 模态框 end----->
    
    <!-----查看策略 #viewTheStrategy 模态框 start------->
    <div class="modal fade" id="viewTheStrategy" tabindex="-1" role="dialog" aria-labelledby="查看策略" aria-hidden="true">
    </div>
    <!-----查看策略 #viewTheStrategy 模态框 end------->
    <!-----新建售货机 选择型号 模态框 start----->
    <div class="modal fade" id="selectVmModel" tabindex="-1" role="dialog" aria-labelledby="选择型号" aria-hidden="true">
    </div>
    <!-----新建售货机 选择型号 模态框 end----->
    <!-- 模态窗结束-->
</div>
</body>
</html>
<script type="text/javascript">
var area = '${u:getAreaJson()}';
// 将字符串转成JSON对象
var areaJson = eval("(" + area + ")");
$(function(){
	MtVm.init();
    // 页面加载完成后执行的方法
    renderProvince();

    var selectedProvinceId = $("#provinceSelect").val();

    renderCity(selectedProvinceId);

    var selectedCityeId = $("#citySelect").val();

    renderCountry(selectedCityeId);


})
</script>