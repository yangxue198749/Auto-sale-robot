<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>投放工单 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/deploy_task.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=deploy-task&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">投放工单</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('deploy-task-add')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/deployTask/addTaskDialog" data-target="#addWorkOrder">新增</button></c:if>
                <c:if test="${u:isPermitted('deploy-task-publish')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/deployTask/publishTaskDialog" data-target="#setLaunchWorkOrder" data-must="true"  data-set="#pagediv table tbody input[type='checkbox']:checked">发布</button></c:if>
                <c:if test="${u:isPermitted('deploy-task-edit')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/deployTask/editTaskDialog" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-must="true" data-target="#changeWorkOrder">修改</button></c:if>
                <c:if test="${u:isPermitted('deploy-task-cancel')}"><button type="button" class="btn btn-default btn-lg2 btn_tableCancel" data-must="true" href="${path }/deployTask/cancelTask" data-set="#pagediv table tbody input[type='checkbox']:checked">取消</button></c:if>
            </div>
        </div> <!--Main CommonHead-->
        <form id="search-form" action="${path}/deployTask/listPage" method="POST">
	        <div class="main_commonhead hl30">
	            <section class="diySltInput">
	                <strong>搜索:</strong>
	                <div class="form-group w100 pull-left">
	                    <select class="form-control input-sm" name="searchField">
	                      <option value="1">售货机编号</option>
	                      <option value="2">点位名称</option>
	                      <option value="3">负责人</option>
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
	                	<input class="form-control input-sm" id="DateDefault" type="text" name="startTime" placeholder="选择执行时间">
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
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh20">点位</li>
                    <li class="wh10">状态</li>
                    <li class="wh10">负责人员</li>
                    <li class="wh15">执行时间</li>
                    <li class="wh15">创建记录</li>
                    <li class="wh15">完成时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/deployTask/listPage" flush="true"/>
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
     <!-------------发布任务 setLaunchWorkOrder start------------->
    <div class="modal fade" id="setLaunchWorkOrder" tabindex="-1" role="dialog" aria-labelledby="发布任务" aria-hidden="true">
       
    </div>
    <!-------------发布任务 setLaunchWorkOrder end------------->
    <div class="modal fade" id="choiceLocation" tabindex="-1" role="dialog" aria-labelledby="选择点位" aria-hidden="true">
        
    </div>
    
</div>
</body>
</html>
<script type="text/javascript">
$(function(){
	DeployTask.init();
})
</script>
