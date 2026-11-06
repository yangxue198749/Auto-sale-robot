<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>工单查询 | 贩售机管理系统</title>
	<%@ include file="/WEB-INF/view/include/head.jsp" %>
	<script type="text/javascript" src="${path}/vmsjs/search_task.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=search-task&code=operate-manage"/>
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
            <h2><strong id="MainHeaderTitle">工单查询</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <button type="button" onclick="exportWorkOrder()" class="btn btn-success btn-lg2">导出数据</button>
            </div>
        </div>
        <!--Main CommonHead-->
        <form id="search-form" action="/searchTask/listPage" method="POST">
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
	                <strong>执行时间:</strong>
	                <div class="form-group w120 pull-left">
	                	<input class="form-control input-sm" id="DateDefault" type="text" name="startTime" placeholder="选择执行时间">
	                </div>
	            </section>
	            <section>
	                <strong>任务状态:</strong>
	                <div class="form-group w120 pull-left">
	                    <select class="form-control input-sm" name="searchTaskVmStatus">
	                        <option  value="">请选择</option>
		                      <option  value="1">未开始</option>
		                      <option  value="2">进行中</option>
		                      <option  value="4">已完成</option>
		                      <option  value="7">已忽略</option>
	                    </select>
	                </div>
	            </section>
	            <section>
	                <strong>任务类型:</strong>
	                <div class="form-group w120 pull-left">
	                    <select class="form-control input-sm" name="searchTaskType">
	                        <option value="">请选择</option>
	                        <option value="3">补货工单</option>
	                        <option value="5">维修工单</option>
	                        <option value="7">投放工单</option>
	                        <option value="8">撤机工单</option>
	                    </select>
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
                    <li class="wh3"><input type="checkbox"></li>
                    <li class="wh5">任务ID</li>
                    <li class="wh10">任务类型</li>
                    <li class="wh8">售货机编号</li>
                    <li class="wh10">点位</li>
                    <li class="wh12">具体位置</li>
                    <li class="wh8">任务状态</li>
                    <li class="wh6">负责人</li>
                    <li class="wh6">任务详情</li>
                    <li class="wh8">执行时间</li>
                    <li class="wh12">创建信息</li>
                    <li class="wh12">完成时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/searchTask/listPage" flush="true"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
$(function(){
	SearchTask.init();
})
</script>
