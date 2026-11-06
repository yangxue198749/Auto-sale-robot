<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>支付管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/mtCustomer.js"></script>
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
		           <jsp:include page="/loadMenu?menuCode=customerAccount_home&code=system-manage"/>
		        </div>
		    </div>
		    <!------ 公用左侧导航 end ------>
		    <!------ 公用头部导航 start ------>
		    <div class="mp_topheader fixed">
		        <jsp:include page="/home?code=system-manage"/>
		        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
		    </div>
		    <!------ 公用头部导航 end ------>
		    <!------ 公用主体部分 start ------>
		    <div class="mp_main pt125">
		        <!--Main Min topheader-->
		        <div class="mp_topheader ver2 ver_fixed">
		            <h2><strong id="MainHeaderTitle">支付管理</strong></h2>
		            <div class="btn-group pull-right" data-toggle="buttons">
		            	<c:if test="${u:isPermitted('customerAccount-update')}">
		                	<button type="button" class="btn btn-default btn-lg2" href="updatePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#updateAccount">修改</button>
		            	</c:if>
		            </div>
		        </div>
		        <!--Main CommonHead-->
		
		        <!--Main CommonTable-->
		        <div class="main_commontable tablePlaceholder">
		            <!--theadscroll start-->
		            <div class="theadscroll" id="theadScroll">
		                <ul>
		                    <li class="wh3"><input type="checkbox" value=""></li>
		                    <li class="wh25">收款渠道</li>
		                    <li class="wh22">收款账号/服务器</li>
		                    <li class="wh20">真实姓名</li>
		                    <li class="wh15">创建信息</li>
		                    <li class="wh15">修改信息</li>
		                </ul>
		            </div>
		            <!--theadscroll end-->
		            <div id="pagediv">
		            	<jsp:include page="/customerAccount/list" flush="false" />
						<!-- <table class="">
						</table> -->
		            </div>
		            <!--table footer end-->
		        </div>
		    </div>
		    
		    <!--修改收款账号 -->
		    <div class="modal fade" id="updateAccount" tabindex="-1" role="dialog" aria-labelledby="修改收款账号" aria-hidden="true"></div>
		
		</div>
	</body>
</html>
