<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
	<head>
	    <meta http-equiv="Content-Type">
	    <meta content="text/html; charset=utf-8">
	    <meta charset="utf-8">
	    <title>今日销售报表</title>
	    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	    <meta name="apple-mobile-web-app-capable" content="yes">
	    <meta name="apple-touch-fullscreen" content="yes" />
	    <meta name="apple-mobile-web-app-status-bar-style" content="default">
	    <meta name="apple-mobile-web-app-title" content="今日销售报表">
	    <meta name="HandheldFriendly" content="True">
	    <meta name="format-detection" content="telephone=no">
	    <meta name="format-detection" content="email=no">
	    <meta name="msapplication-tap-highlight" content="no">
	    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
	    <script type="text/javascript" src="/mp-static/js/SalesReport.js?v=20171021"></script>
    	<script type="text/javascript" src="/mp-static/js/echarts.common.min.js"></script>
	</head>
  
	<body>
		<div class="weui-pull-to-refresh-layer">
			<div class="pull-to-refresh-arrow"></div> <!-- 上下拉动的时候显示的箭头 -->
			<div class="pull-to-refresh-preloader"></div> <!-- 正在刷新的图标 -->
			<div class="down">下拉刷新</div><!-- 下拉过程显示的文案 -->
			<div class="up">释放刷新</div><!-- 下拉超过50px显示的文案 -->
			<div class="refresh">正在刷新...</div><!-- 正在刷新时显示的文案 -->
		</div>
	   <div class="mh_container">
	       <!--售货机数量 start-->
	       <div class="weui_cells weui_cells_access mt0 bg_f2">
	            <div class="weui_cell">
	              <div class="weui_cell_bd weui_cell_primary">
	                  <p class="f14 cf6" id="vmCount">Loading...</p>
	              </div>
	              <div class="weui_cell_ft2"><input class="dateinput" id="dateToday" type="text" value="2016-12-12"></div>
	            </div>
	        </div>
	        <!--售货机数量 end-->
	        <!--今日销售额 start-->
	        <div class="detailBox ver2" id="todayBox">
	             <aside class="w55">
	                 <section>
	                     <strong class="f28" id="dayAmount1"><div class="loader">Loading...</div></strong>
	                     <p>今日销售额(元)</p>
	                 </section>
	             </aside>
	             <aside class="w45">
	                 <strong id="dayVolume1"><div class="loader">Loading...</div></strong>
	                 <p>今日销量(件)</p>
	             </aside>
	        </div>
	        <!--今日销售额 end-->
	        
	        <!--昨日销售额 start-->
	        <div class="detailBox ver2 blue" id="yesterdayBox">
	             <aside class="w55">
	                 <section>
	                     <strong class="f28 input_saleMoney" id="dayAmount2"><div class="loader">Loading...</div></strong>
	                     <p class="text_saleMoney">昨日销售额(元)</p>
	                 </section>
	             </aside>
	             <aside class="w45">
	                 <strong class="f28 input_saleSize" id="dayVolume2"><div class="loader">Loading...</div></strong>
	                 <p class="text_saleSize">昨日销量(件)</p>
	             </aside>
	        </div>
	        <!--昨日销售额 end-->
	        
	        <!--月累计销售额 start-->
	        <div onClick="monthReport()" class="detailBox ver2 orange" id="monthBox">
	             <aside class="w55">
	                 <section>
	                     <strong class="f24" id="totalAmount"><div class="loader">Loading...</div></strong>
	                     <p>月累计销售额(元)</p>
	                 </section>
	             </aside>
	             <aside class="w45">
	                 <strong class="f24" id="totalVolume"><div class="loader">Loading...</div></strong>
	                 <p>月累计销量(件)</p>
	             </aside>
	        </div>
	        <!--月累计销售额 end-->
	        
	        <!--它日销售额 start-->
	        <div class="detailBox ver2 blue" id="otherdayBox" hidden="hidden">
	             <aside class="w55">
	                 <section>
	                     <strong class="f28 input_saleMoney" id="dayAmount3"><div class="loader">Loading...</div></strong>
	                     <p class="text_saleMoney">销售额(元)</p>
	                 </section>
	             </aside>
	             <aside class="w45">
	                 <strong class="f28 input_saleSize" id="dayVolume3"><div class="loader">Loading...</div></strong>
	                 <p class="text_saleSize">销量(件)</p>
	             </aside>
	        </div>
	        <!--它日销售额 end-->
	        
	        <!--曲线box start-->
	        <div class="boxplaceholder" id="dayOfMonthChart"></div>
	        <!--曲线box end-->
	        
	        <!--列表详细-->
		   <div  id="monthSkuSaleListBox">
				<div class="weui_cells weui_cells_access mt0 bg_f2 btb">
				   <a class="weui_cell" href="javascript:;">
					   <div class="weui_cell_bd weui_cell_primary">
						   <p class="f14 cf6">当月商品销售报表</p>
					   </div>
				   </a>
				</div>
				<div class="detailWrap w160 m10">
					<header>
						<span>商品名称</span>
						<section>
							 <ul>
								 <li>销售额(元)</li>
								 <li>销售量(件)</li>
							 </ul>
						</section>
					</header>
					<hgroup class="p0" id="skuData"><div style="text-align: center; height: 100px; padding-top: 30px;"><div class="loader plus">Loading...</div></div></hgroup>
				</div>
		   </div>
	        
	   </div>
	
	
		<!--watermark-->
		<div class="watermark"></div>
		<!----------------Common js start---------------->
		<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
		<script>
		$(function() {
			otherdayReport(todayDate());


		    FastClick.attach(document.body);
			$(document.body).pullToRefresh().on("pull-to-refresh", function() {
				setTimeout(function() {
					otherdayReport(todayDate());
					$("#dateToday").val(todayDate());
					$(document.body).pullToRefreshDone(); // 重置下拉刷新
				}, 500);   //模拟延迟
			});
		});
		</script>
		<!----------------Common js end------------------>
	</body>
</html>
