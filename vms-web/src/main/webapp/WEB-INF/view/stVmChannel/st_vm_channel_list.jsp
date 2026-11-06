<%@ page language="java" contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>货道策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/st_vm_channel.js"></script>
    <script type="text/javascript" src="${path}/vmsjs/skuType.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=st-vm-channel&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->        
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">货道策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('st-vm-channel-add')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="${path }/stVmChannel/addStVmChannelDialog" data-target="#addStVmChannel">新增</button></c:if>
                <c:if test="${u:isPermitted('st-vm-channel-edit')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-must="true" href="${path }/stVmChannel/deleteStrategy" data-set="#pagediv table tbody input[type='checkbox']:checked">删除</button></c:if>
                <c:if test="${u:isPermitted('st-vm-channel-delete')}"><button type="button" class="btn btn-default btn-lg2" data-must="true" select-one="true" data-set="#pagediv table tbody input[type='checkbox']:checked" data-toggle="modal" href="${path }/stVmChannel/editStVmChannelDialog" data-target="#editStVmChannel">修改</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        <form action="${path }/stVmChannel/listPage" class="unKeyCode" id="search-form">
            <section>
                <strong>策略名称:</strong>
                <div class="form-group w120 pull-left"><input name="searchStVmName" class="form-control input-sm" type="text" placeholder="请输入策略名称"></div>
            </section>
            <section>
                <strong>售货机型号:</strong>
                <div class="form-group w120 pull-left">
                    <select class="form-control input-sm" name="searchVmModelId" >
                      <option value="">请选择</option>
                      <c:forEach items="${vmModelList }" var="vmModel">
                      	<option value="${vmModel.id }" >${vmModel.modelName }</option>
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
                    <li class="wh10"><input type="checkbox" value=""></li>
                    <li class="wh20">策略名称</li>
                    <li class="wh20">策略说明</li>
                    <li class="wh10" >售货机型号</li>
                    <li class="wh10">货道配置</li>
                    <%-- 5.0.0a 删除 查看列表页配置  <li class="wh10">列表页配置</li>--%>
                    <li class="wh15">创建记录</li>
                    <li class="wh15">修改记录</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
	           <jsp:include page="/stVmChannel/listPage" flush="true"/>
            </div>
        </div>
    </div>
    
    <!----
      **  模态框 **
    ---->
    <!-----查看货道配置 模态框 start----->
    <div class="modal fade" id="viewTheGoods" tabindex="-1" role="dialog" aria-labelledby="查看货道" aria-hidden="true">
        
    </div>
    <!-----查看货道配置 模态框 end----->  
    
    

    <!-----新增货道策略 模态框 start----->
    <div class="modal fade" id="addStVmChannel" tabindex="-1" role="dialog" aria-labelledby="新增货道策略" aria-hidden="true">
        
    </div>
    <div class="modal fade" id="editStVmChannel" tabindex="-1" role="dialog" aria-labelledby="新增货道策略" aria-hidden="true">
        
    </div>
    <!-----新增货道策略  模态框 end----->  
    <!-----货道设置 第二步 模态框 start----->
    <div class="modal fade" id="setTheGoods_02" tabindex="-1" role="dialog" aria-labelledby="货道设置" aria-hidden="true">
    
    	<div class="modal-dialog w1000">
	     <div class="modal-content">
	         <div class="modal-header">
	             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	             <h4 class="modal-title f16" id="">货道设置</h4>
	         </div>
	         <form id="addStVmChannelDetail-form" action="/stVmChannel/submitAddStVmChannelDetail" method="POST" class="unKeyDown">
	         <div class="modal-body">
	                 <!--货道设置信息 start-->
	                 <div class="modal_commonbody clearfix">
	                      <div class="btn-group" data-toggle="buttons">
	         					<button type="button" class="btn btn-default btn-lg2" data-toggle="modal"  href="/stVmChannel/selectSkuDialog?relateType=add" data-target="#SelectSku" data-must="true" li-set="#addChannelDetailDiv li.on input">关联商品</button>
	         					<button type="button" id="addCancelRelate" href="/stVmChannel/cancelRelate"  class="btn btn-default btn-lg2">取消关联</button>
	         					<button type="button" id="addMergeChannel" href="/stVmChannel/mergeChannel" class="btn btn-default btn-lg2">合并货道</button>
	         					<button type="button" id="addCancelMerge" href="/stVmChannel/cancelMerge" class="btn btn-default btn-lg2">解除合并</button>
	         					<%--
	         					5.0.0a 去掉列表页设置
	         					<button type="button" id="addSetSort" href="/stVmChannel/setSortDialog" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#setSort">列表页设置</button>--%>
	                      </div>
	                      <div id="addChannelDetailDiv" class="col_thegoods mt15 thegoods v3_2">
	                      ${html }
	<!--                           <ul> -->
	<!--                               <li class="wh10 on"> -->
	<!--                               	<hgroup> -->
	<!--                                	<input name="" value="" type="hidden" > -->
	                               
	<!--                                	<span class="icon_state sm">1-1</span> -->
	<!--                               	</hgroup> -->
	<!--                               </li> -->
	<!--                           </ul> -->
	                      </div>
	                 </div>
	         </div>
	         <!--按钮-->
	         <div class="modal-footer">
	             <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
                 <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
	         </div>
	         </form>
	     </div>
 	</div>
    </div>
    <!-----货道设置 第二步 模态框 end----->  

    <!-----货道设置 第二步 选择商品 模态框 start----->
    <div class="modal fade" id="SelectSku" role="dialog" aria-labelledby="选择商品" aria-hidden="true">
        
    </div>
    <!-----货道设置 第二步 选择商品 模态框 end----->  
    
    
    
    <div class="modal fade" id="setSort" tabindex="-1" role="dialog" aria-labelledby="列表页设置" aria-hidden="true">
        
    </div>
    
    <div class="modal fade" id="showSortDialog" tabindex="-1" role="dialog" aria-labelledby="列表页设置" aria-hidden="true">
        
    </div>
    <!-----修改货道满量 模态框 start----->
    <div class="modal fade" id="setFullSku" tabindex="-1" role="dialog" aria-labelledby="设置满量" aria-hidden="true">

    </div>
</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
    	StVmChannel.init();
    	StVmChannel.addMergeChannel();
    	StVmChannel.addCancelMerge();
    	StVmChannel.addCancelRelate();
        StVmChannel.editFullSku();
	   $(".thegoods").on("click","li", function(){
	   	$(this).toggleClass("on");
	   })
    })
</script>

 