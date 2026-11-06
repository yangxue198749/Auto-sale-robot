<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
	<head>
	    <meta http-equiv="Content-Type">
	    <meta content="text/html; charset=utf-8">
	    <meta charset="utf-8">
	    <title>销售流水</title>
	    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	    <meta name="apple-mobile-web-app-capable" content="yes">
	    <meta name="apple-touch-fullscreen" content="yes" />
	    <meta name="apple-mobile-web-app-status-bar-style" content="default">
	    <meta name="apple-mobile-web-app-title" content="工单列表">
	    <meta name="HandheldFriendly" content="True">
	    <meta name="format-detection" content="telephone=no">
	    <meta name="format-detection" content="email=no">
	    <meta name="msapplication-tap-highlight" content="no">
	    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
	</head>

	<body>
		<div class="weui-pull-to-refresh-layer">
			<div class="pull-to-refresh-arrow"></div> <!-- 上下拉动的时候显示的箭头 -->
			<div class="pull-to-refresh-preloader"></div> <!-- 正在刷新的图标 -->
			<div class="down">下拉刷新</div><!-- 下拉过程显示的文案 -->
			<div class="up">释放刷新</div><!-- 下拉超过50px显示的文案 -->
			<div class="refresh">正在刷新...</div><!-- 正在刷新时显示的文案 -->
		</div>
		<!-- 5.0.0a 置顶搜索 -->
		<div class="searchFixed">
			<div class="topSearch">
				<form class="searchInput" id="searchForm" method="post">
					<input type="text" name="innerCode" id="innerCode" value="${innerCode}">
					<input type="submit" class="searchBtn" value="搜索"/>
				</form>
			</div>
		</div>
		<div class="mh_container">
			<!-- / 5.0.0a 置顶搜索 -->

	        <!--流水列表 header-->
	        <div class="detailBox head">
	             <div class="title_news">全部售货机<b class="f24 fr">${list.size()}</b></div>
	        </div>

	        <!--列表详细-->
	        <div class="weui_cells weui_cells_access mt15">
				<c:if test="${empty list &&!empty innerCode}">
                    <p class="searchEmpty">未找到相关内容，请确认搜索内容</p>
                </c:if>
	        	<c:forEach items="${list}" var="vm">
	        		<a class="weui_cell" href="getOrderDetailHome?vmId=${vm.id}">
		              <div class="weui_cell_bd weui_cell_primary">
		                  <p class="f14 cf3"><b>${vm.innerCode}</b></p>
		                  <p class="f12 cf6">${vm.node.nodeName }</p>
		              </div>
		              <div class="weui_cell_ft"></div>
		            </a>
	        	</c:forEach>
	        </div>

	   </div>


	<!--watermark-->
	<div class="watermark"></div>
	<!----------------Common js start---------------->
	<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
	<script>
		$(function() {
//			$('#searchForm .searchBtn').click(function(){
//				var innerCode = $('#innerCode').val();
//			});

		    FastClick.attach(document.body);
			$(document.body).pullToRefresh().on("pull-to-refresh", function() {
				setTimeout(function() {
					window.location.reload();
					$(document.body).pullToRefreshDone(); // 重置下拉刷新
				}, 500);   //模拟延迟
			});
		});
	</script>
	<!----------------Common js end------------------>

	<!---AutoMat chioceCode 交互js start--->
	<script type="text/javascript" src="/mp-static/js/AutoMat_chioceCode.js"></script>
	<!---AutoMat chioceCode 交互js end--->

	</body>
</html>
