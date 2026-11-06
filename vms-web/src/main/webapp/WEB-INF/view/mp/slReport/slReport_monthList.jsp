<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
	<head>
	    <meta http-equiv="Content-Type">
	    <meta content="text/html; charset=utf-8">
	    <meta charset="utf-8">
	    <title>月度销售报表</title>
	    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	    <meta name="apple-mobile-web-app-capable" content="yes">
	    <meta name="apple-touch-fullscreen" content="yes" />
	    <meta name="apple-mobile-web-app-status-bar-style" content="default">
	    <meta name="apple-mobile-web-app-title" content="月度销售报表">
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
	   <div class="mh_container pb60">
	        
	        <div class="detailWrap w160">
	            <header class="ul3">
	                 <ul>
	                     <li><span>月度销售统计</span></li>
	                     <li><span>销量(个)</span></li>
	                     <li><span>销售额(元)</span></li>
	                 </ul>
	            </header>
	            <hgroup class="p0">
	            	<c:forEach items="${list}" var="monthData">
		                 <div class="goodslist ul3">
		                     <ul>
		                         <li><span>${monthData.month}</span></li>
		                         <li><b>${monthData.salesVolume}</b></li>
		                         <li><b class="cf_red">${monthData.salesAmount}</b></li>
		                     </ul>
		                 </div>
	            	</c:forEach>
	            </hgroup>
	        </div>
	        
	   </div>
	
	
	<!--watermark-->
	<div class="watermark"></div>
	<!----------------Common js start---------------->
	<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
	<script>
	$(function() {
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
	
	
	</body>
</html>
