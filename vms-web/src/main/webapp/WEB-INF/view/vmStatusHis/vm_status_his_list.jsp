<%@ page language="java" contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>售货机历史状态 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/vmStatusHis.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
	<style>
		.datetimepicker th.switch {text-indent:0;}
		.datetimepicker table tr td span.active:active, .datetimepicker table tr td span.active:hover:active, .datetimepicker table tr td span.active.disabled:active, .datetimepicker table tr td span.active.disabled:hover:active, .datetimepicker table tr td span.active.active, .datetimepicker table tr td span.active:hover.active, .datetimepicker table tr td span.active.disabled.active, .datetimepicker table tr td span.active.disabled:hover.active, .datetimepicker table tr td.active:active, .datetimepicker table tr td.active:hover:active, .datetimepicker table tr td.active.disabled:active, .datetimepicker table tr td.active.disabled:hover:active, .datetimepicker table tr td.active.active, .datetimepicker table tr td.active:hover.active, .datetimepicker table tr td.active.disabled.active, .datetimepicker table tr td.active.disabled:hover.active {
background: #04ac71 !important;}
	</style>
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=vmStatusHis-list&code=statistical-analysis"/>
        </div>
    </div>
   	<div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机历史状态</strong></h2>
        </div>
        <!--Main CommonHead-->
       <form action="/vmStatusHis/listPage"  method="POST" id="search-form" class="unKeyCode">
	        <div class="main_commonhead hl30">
	            <section class="diySltInput">
	                <strong>搜索:</strong>
	                <div class="form-group w100 pull-left">
	                    <select class="form-control input-sm select2me" name="selectType">
	                        <option value="1">售货机编号</option>
	                        <option value="2">点位</option>
	                    </select>
	                </div>
	                <div class="form-group w160 pull-left">
	                    <input class="form-control input-sm" name="selectValue" type="text" placeholder="请输入搜索内容">
	                </div>
	            </section>
	            <section>
	                <strong>VMC状态:</strong>
	                <div class="form-group w120 pull-left">
	                    <select class="form-control input-sm select2me" name="vmStatus" >
	                        <option value="">请选择</option>
	                        <option value="0">正常</option>
	                        <option value="1">故障</option>
	                    </select>
	                </div>
	            </section>
	            <section>
	                <strong>日期:</strong>
	                <div class="form-group w130 pull-left">
	                    <input class="form-control input-sm" type="text" name="startTime" value="${startTime }" id="begin_time"  placeholder="开始日期" name="startDate">
	                </div>
	                <strong class="ml5">-</strong>
	                <div class="form-group w130 pull-left">
	                    <input class="form-control input-sm" type="text"  name="endTime"  value="${endTime }" id="end_time" placeholder="结束日期" name="endDate">
	                </div>
	            </section>
				<div class="diyBtnGroup pull-left">
					<button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
					<button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
					<div class="clearfix"></div>
				</div>
				<div class="clearfix"></div>
				<%--<button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
				<button type="button"  onclick="reset('search-form')" class="btn btn-default f12 btn-lg2">重置</button>--%>
	            <%--<div class="btn-group" data-toggle="buttons">
	                <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
	            <button type="button"  onclick="reset('search-form')"   class="btn btn-default f12 btn-lg2">重置</button>
	            </div>--%>
	        </div>
		</form>
        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh20">最后上报时间</li>
                    <li class="wh15">点位</li>
                    <li class="wh10">门锁</li>
                    <li class="wh10">温度(°)</li>
                    <li class="wh10">VMC状态</li>
                    <li class="wh20">VMC问题</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
           		<jsp:include page="/vmStatusHis/listPage?startTime=${startTime}&endTime=${endTime}" flush="true"/>
           	</div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    $(function () {
        VmStatusHis.init();
    })
</script>
</html>
