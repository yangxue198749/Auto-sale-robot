<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
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
		<script type="text/javascript" src="/mp-static/plus/js/jquery-weui.js"></script>
		<script type="text/javascript" src="/mp-static/plus/lib/fastclick.js"></script>
	    <%--<script type="text/javascript" src="/mp-static/js/SalesPipeline_detail.js"></script>--%>
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
	   		<input type="text" hidden="hidden" id="vmId" value="${vmId}"/>
	   		<div>
	        	<%--<jsp:include page="/mp/slOrder/getOrderDetailList" flush="false" />--%>
					<!--设备detailBox start-->
					<div class="detailBox">
						<aside>
							<p>设备编号</p>
							<strong>${vm.innerCode}</strong>
						</aside>
						<article>
							<section>
								<p><strong><span>具体地址</span></strong></p>
								<p class="moretext">${vm.node.nodeAddress }</p>
							</section>
						</article>
					</div>
					<!--设备detailBox end-->
					<!--tab date start-->
					<div class="listHeader date" id="topmenu">
						<section onclick="setTime(-1)" class="boxlf weui_cell_fl2">前一天</section>
						<span class="weui_cell_down2"><input class="dateinput" id="dateToday" type="button" value="${time}" readonly></span>
						<section onclick="setTime(1)" class="boxrt weui_cell_ft2">后一天</section>
					</div>
					<!--tab date end-->

					<!--总销售额 header-->
					<div class="detailBox head">
						<div class="title_news">总销售额(元)<b class="f24 fr payAmount"></b></div>
					</div>

					<!--列表详细-->
					<div class="detailWrap w160 m10">
						<header>
							<span>商品名称</span>
							<section>
								<ul>
									<li>售出时间</li>
									<li>售出金额(元)</li>
								</ul>
							</section>
						</header>
						<hgroup class="p0 slOrderDetailDiv">

						</hgroup>
					</div>
	        </div>
		   <div id="alreadyFinish" class="alreadyFinish">
			   <p> 已全部加载完毕 </p>
		   </div>
		   <div class="weui-infinite-scroll mb20">
			   <div class="infinite-preloader"></div><!-- 加载 -->
			   正在加载... <!-- 文案，可以自行修改 -->
		   </div>
	   </div>
	<!--watermark-->
	<div class="watermark"></div>
	<script>
		var hasNext = true;
		var pageNo = 1;
		var loading = false;  //状态标记
		$(function() {
			FastClick.attach(document.body);
			$(".weui-infinite-scroll").hide();//显示正在加载标签
			$("#alreadyFinish").hide();
			FastClick.attach(document.body);
			$(document.body).pullToRefresh().on("pull-to-refresh", function() {
				setTimeout(function() {
					window.location.reload();
					$(document.body).pullToRefreshDone(); // 重置下拉刷新
				}, 500);   //模拟延迟
			});
			infinite();
			getOrderDetailList();
		});
		function getOrderDetailList(){
			loading = true;
			$.ajax({
				url: "/mp/slOrder/getOrderDetailList",
				data:{"pageNo":pageNo,"vmId":$('#vmId').val(),"time":$("#dateToday").val()},
				type:"POST",
				success: function(data){
					loading = false;
					$(".slOrderDetailDiv").append(data.html);
					if(pageNo == 1 && null != data.payAmount){
						$('.payAmount').html(data.payAmount);
					}
					if(data.hasNextPage){
						$(".weui-infinite-scroll").hide();//隐藏正在加载标签
						pageNo++;
						$("#alreadyFinish").hide();
					}else{
						hasNext = false;
						$(document.body).destroyInfinite();//销毁加载事件
						$(".weui-infinite-scroll").hide();//隐藏正在加载标签
						$("#alreadyFinish").show();
					}
				}
			});
		}
		function infinite(){
			$(document.body).infinite().on("infinite", function() {
				if(loading) return;
				if(!hasNext)return;
				loading = true;
				$(".weui-infinite-scroll").show();//显示正在加载标签
				setTimeout(function() {
					loading = false;
					getOrderDetailList();
				},500);   //模拟延迟
			});
		}
		function addDate(date,days){
			var d=new Date(date);
			d.setDate(d.getDate()+days);
			var month=d.getMonth()+1;
			var day = d.getDate();
			if(month<10){
				month = "0"+month;
			}
			if(day<10){
				day = "0"+day;
			}
			var val = d.getFullYear()+"-"+month+"-"+day;
			return val;
		}
		function setTime(day){
			$("#dateToday").val(addDate($("#dateToday").val(),day));
			$(".slOrderDetailDiv").html("");
			pageNo=1;
			hasNext = true;
			loading = false;
			getOrderDetailList();
			infinite();

		}
	</script>
	</body>
</html>


