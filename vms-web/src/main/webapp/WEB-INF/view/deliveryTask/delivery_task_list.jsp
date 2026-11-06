<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>配送工单 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/delivery_task.js?v=20180116"></script>
    <script type="text/javascript" src="${path}/plus/datepicker/js/function.datepicker.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <!------  mp_leftmenu ------>
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=delivery-task&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!------ / mp_topheader ------>
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">配送工单</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('delivery-task-add')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/deliveryTask/addTaskDialog" data-target="#addWorkOrder">新增</button></c:if>
                <c:if test="${u:isPermitted('delivery-task-edit')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/deliveryTask/editTaskDialog" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-must="true" data-target="#changeWorkOrder">修改</button></c:if>
                <c:if test="${u:isPermitted('delivery-task-cancel')}"><button type="button" class="btn btn-default btn-lg2 btn_tableCancel" data-must="true" href="${path }/deliveryTask/cancelTask" data-set="#pagediv table tbody input[type='checkbox']:checked"">取消</button></c:if>
                <c:if test="${u:isPermitted('export-delivery')}"><button type="button" id="exportDeliveryBtn" class="btn btn-success btn-lg2">导出数据</button></c:if>
            </div>
        </div>
        <form id="search-form" action="${path}/deliveryTask/listPage" method="POST">
	        <div class="main_commonhead hl30">
	            <section class="diySltInput">
	                <strong>搜索:</strong>
	                <div class="form-group w80 pull-left">
	                    <select class="form-control input-sm" name="searchField">
	                      <option value="1">售货机编号</option>
	                      <option value="2">点位名称</option>
	                      <option value="3">配送员</option>
	                    </select>
	                </div>
	                <div class="form-group w120 pull-left">
	              	  <input class="form-control input-sm" name="searchFieldValue" type="text" placeholder="请输入关键字">
	                </div>
	            </section>
	            <section>
	                <strong>任务状态:</strong>
	                <div class="form-group w120 pull-left">
	                    <select class="form-control input-sm" name="searchTaskVmStatus">
	                      <option value="">请选择</option>
	                      <option value="1">未开始</option>
	                      <option value="2">进行中</option>
	                      <option value="4">已完成</option>
	                      <option value="7">已忽略</option>
	                    </select>
	                </div>
	            </section>
	            <section>
	                <strong>执行时间:</strong>
	                <div class="form-group w120 pull-left">
	                	<input class="form-control input-sm" id="DateStartOne" type="text" name="startTime" placeholder="开始日期">
	                </div>
                    <strong class="ml5">-</strong>
                    <div class="form-group w120 pull-left">
                        <input class="form-control input-sm" id="DateEndOne" type="text" name="endTime" placeholder="结束日期">
                    </div>
                    <div class="clearfix"></div>
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
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh17">点位</li>
                    <li class="wh10">状态</li>
                    <li class="wh10">补货模式</li>
                    <li class="wh10">配送员</li>
                    <li class="wh10">执行时间</li>
                    <li class="wh10">创建记录</li>
                    <li class="wh10">完成时间</li>
                    <li class="wh10">详情</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/deliveryTask/listPage" flush="true"/>
            </div>
        </div>
    </div>
    
    <!---窗口 模态框--->
    <!-------------新增配送工单 addWorkOrder start------------->
    <div class="modal fade" id="addWorkOrder" tabindex="-1" role="dialog" aria-labelledby="新增任务" aria-hidden="true">
        
    </div>
    <!-------------新增配送工单 addWorkOrder end------------->
    <!-------------选择售货机 choiceAutoMat start------------>
    <div class="modal fade" id="selectMtVm" tabindex="-1" role="dialog" aria-labelledby="选择设备" aria-hidden="true">
        
    </div>
    <!-------------选择售货机 choiceAutoMat end--------------->
    <!-------------修改任务 changeWorkOrder start------------->
    <div class="modal fade" id="changeWorkOrder" tabindex="-1" role="dialog" aria-labelledby="修改任务" aria-hidden="true">
        
    </div>
    <!-------------修改任务 changeWorkOrder end------------->

    <!-------------查看详情 changeWorkOrder start------------->
    <div class="modal fade" id="showTaskDialog" tabindex="-1" role="dialog" aria-labelledby="详情" aria-hidden="true">
        
    </div>
    <!-------------查看详情 changeWorkOrder end------------->
    <!-------------手动调量 adjust start------------->
    <div class="modal fade" id="adJustModel" tabindex="-1" role="dialog" aria-labelledby="手动调量" aria-hidden="true">

    </div>
    <!-------------手动调量 adjust end------------->
    
    
</div>
</body>
</html>
<script type="text/javascript">
$(function(){
	DeliveryTask.init();
})
</script>
