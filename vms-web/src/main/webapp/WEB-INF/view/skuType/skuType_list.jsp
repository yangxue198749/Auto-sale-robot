<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>商品类型 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/skuType.js"></script>
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
           <jsp:include page="/loadMenu?menuCode=skuType-home&code=data-manage"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
	    <!------ 公用头部导航 end ------>
	    <!------ 公用主体部分 start ------>
	    <div class="mp_main pt125">
	        <!--Main Min topheader-->
	        <div class="mp_topheader ver2 ver_fixed">
	            <h2><strong id="MainHeaderTitle">商品类型</strong></h2>
	
	        </div>
	        <!--Main CommonHead-->
			<div style="margin-left: -10px; margin-right: -10px;">
	        <div class="col-md-6" style="margin-right: -10px;">
	            <div class="main_commonhead hl30">
	                <div class="btn-group" data-toggle="buttons">
	                	<c:if test="${u:isPermitted('skuType-create')}">
	                    	<button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addTypeGroup">新增</button>
	                    </c:if>
	                    <c:if test="${u:isPermitted('skuType-update')}">
	                    	<button id="updateTypeGroupButton" type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-set="#pagediv1 table tbody input[type=checkbox]:checked" select-one="true" data-must="true" data-target="#updateTypeGroup">修改</button>
	                    </c:if>
	                    <c:if test="${u:isPermitted('skuType-delete')}">
	                    	<button id="deleteTypeGroupButton" type="button" class="btn btn-default btn-lg2" data-toggle="modal" >删除</button>
	                	</c:if>
	                </div>
	            </div>
	
	            <!--Main CommonTable-->
	            <div class="main_commontable">
	                <!--theadscroll start-->
	                <div class="theadscroll">
	                    <ul>
	                        <li class="wh10"><input type="checkbox" value=""></li>
	                        <li class="wh30">类型组名称</li>
	                        <li class="wh30">创建信息</li>
	                        <li class="wh30">修改信息</li>
	                    </ul>
	                </div>
	                <!--theadscroll end-->
	                <div id="pagediv1">
		            	<jsp:include page="/skuType/list" flush="false" />
		            </div>
	            </div>
	        </div>
	        
	        <div class="col-md-6" style="margin-left: -10px;">
	            <div class="main_commonhead hl30">
	                <div class="btn-group" data-toggle="buttons">
	                	<c:if test="${u:isPermitted('skuType-create')}">
	                    	<button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addType">新增</button>
	                    </c:if>
	                    <c:if test="${u:isPermitted('skuType-update')}">
	                    	<button id="updateTypeButton" type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-set="#pagediv2 table tbody input[type=checkbox]:checked" select-one="true" data-must="true" data-target="#updateType">修改</button>
	                    </c:if>
	                    <c:if test="${u:isPermitted('skuType-delete')}">
	                    	<button id="deleteTypeButton" type="button" class="btn btn-default btn-lg2" data-toggle="modal" >删除</button>
	                	</c:if>
	                </div>
	            </div>
	
	            <!--Main CommonTable-->
	            <div class="main_commontable">
	                <!--theadscroll start-->
	                <div class="theadscroll">
	                    <ul>
	                        <li class="wh10"><input type="checkbox" value=""></li>
	                        <li class="wh30">类型名称</li>
	                        <li class="wh30">创建时间</li>
	                        <li class="wh30">修改时间</li>
	                    </ul>
	                </div>
	                <!--theadscroll end-->
	                <div id="pagediv2">
		            	<%-- <jsp:include page="/skuType/list" flush="false" /> --%>
		            </div>
	            </div>
	        </div>
			</div>
	
	    </div>
	
	    <!----
	      **  模态框 **
	    ---->
	    <!-----新增类型组 模态框 start----->
	    <div class="modal fade" id="addTypeGroup" tabindex="-1" role="dialog" aria-labelledby="新建类型组" aria-hidden="true">
	        <div class="modal-dialog w400">
	            <div class="modal-content">
	                <div class="modal-header">
	                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                    <h4 class="modal-title f16">新建类型组</h4>
	                </div>
	                <div class="modal-body">
	                    <!--基本信息 start-->
	                    <form action="" id="addTypeGroup_form">
	                        <div class="modal_commonbody mt10 clearfix">
	                            <section class="col-md-12">
	                                <span class="w100 pull-left text-right mr10">类型组:</span>
	                                <div class="form-group pull-left">
	                                    <input id="addTypeGroupName" name="skuTypeName" type="text" maxlength="10" class="form-control input-sm" placeholder="请输入类型组">
	                                </div>
	                            </section>
	                        </div>
	                        <!--基本信息 end-->
	                    </form>
	                </div>
	                <!--按钮-->
	                <div class="modal-footer">
	                    <button id="addTypeGroup_form_button" type="button" class="btn btn-success btn-lg2 confirmbtn">提交</button>
	                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
	                </div>
	
	            </div>
	        </div>
	    </div>
	
	    <!-----新增类型 模态框 start----->
	    <div class="modal fade" id="addType" tabindex="-1" role="dialog" aria-labelledby="新建类型" aria-hidden="true">
	        <div class="modal-dialog w400">
	            <div class="modal-content">
	                <div class="modal-header">
	                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                    <h4 class="modal-title f16">新建类型</h4>
	                </div>
	                <div class="modal-body">
	                    <!--基本信息 start-->
	                    <form action="" id="addType_form">
	                        <div class="modal_commonbody mt10 clearfix">
	                            <section class="col-md-12">
	                                <span class="w100 pull-left text-right mr10">类型:</span>
	                                <div class="form-group pull-left">
	                                    <input id="addTypeName" name="skuTypeName" type="text" maxlength="10" class="form-control input-sm" placeholder="请输入类型">
	                                </div>
	                            </section>
	                        </div>
	                        <!--基本信息 end-->
	                    </form>
	                </div>
	                <!--按钮-->
	                <div class="modal-footer">
	                    <button id="addType_form_button" type="button" class="btn btn-success btn-lg2 confirmbtn">提交</button>
	                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
	                </div>
	
	            </div>
	        </div>
	    </div>
	
	
		<!-----修改商品分类组  模态框----->
	    <div class="modal fade" id="updateTypeGroup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        <div class="modal-dialog">
	            <div class="modal-content">
	                <div class="modal-header">
	                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                    <h4 class="modal-title" id="viewModalLabel">修改类型组</h4>
	                </div>
	                <div class="modal-body">
	                   	<form action="" id="updateTypeGroup_form">
	                        <div class="modal_commonbody mt10 clearfix">
	                            <section class="col-md-12">
	                                <span class="w100 pull-left text-right mr10">类型:</span>
	                                <div class="form-group pull-left">
	                                    <input id="updateTypeGroup_name" type="text" maxlength="10" class="form-control input-sm" placeholder="请输入类型">
	                                </div>
	                            </section>
	                        </div>
	                        <!--基本信息 end-->
	                    </form>
	                </div>
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                    <button id="updateTypeGroup_form_button" type="button" class="btn btn-success">确定</button>
	                </div>
	            </div>
	        </div>
	    </div>
	
	
	    <!-----修改商品分类  模态框----->
	    <div class="modal fade" id="updateType" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        <div class="modal-dialog">
	            <div class="modal-content">
	                <div class="modal-header">
	                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                    <h4 class="modal-title" id="viewModalLabel">修改类型</h4>
	                </div>
	                <div class="modal-body">
	                   	<form action="" id="updateType_form">
	                        <div class="modal_commonbody mt10 clearfix">
	                            <section class="col-md-12">
	                                <span class="w100 pull-left text-right mr10">类型:</span>
	                                <div class="form-group pull-left">
	                                    <input id="updateType_name" type="text" maxlength="10" class="form-control input-sm" placeholder="请输入类型">
	                                </div>
	                            </section>
	                        </div>
	                        <!--基本信息 end-->
	                    </form>
	                </div>
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                    <button id="updateType_form_button" type="button" class="btn btn-success">确定</button>
	                </div>
	            </div>
	        </div>
	    </div>
	
	</div>
	</body>
</html>
