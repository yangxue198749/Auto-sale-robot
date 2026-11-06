<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>商品信息 | 贩售机管理系统</title>
	<%@ include file="/WEB-INF/view/include/head.jsp" %>
	<script type="text/javascript" src="/vmsjs/skuType.js"></script>
	<script type="text/javascript" src="/vmsjs/sku.js"></script>
	<!--[if lt IE 9]>
	<script src="./plus/html5shiv/html5shiv.min.js"></script>
	<![endif]-->
  </head>
<body>
	<div class="mp_container">
	    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=sku-home&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
	    <div class="mp_main pt125">
	        <!--Main Min topheader-->
	        <div class="mp_topheader ver2 ver_fixed">
	            <h2><strong id="MainHeaderTitle">商品信息</strong></h2>
	            <div class="btn-group pull-right" data-toggle="buttons">
	            	<c:if test="${u:isPermitted('sku-createPage')}">
	                	<button type="button" class="btn btn-default btn-lg2" href="createPage" data-toggle="modal" data-target="#addSku">新增</button>
	                </c:if>
	                <c:if test="${u:isPermitted('sku-changePage')}">
	                	<button type="button" class="btn btn-default btn-lg2" href="changePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#changeSku">编辑</button>
	                </c:if>
	                <c:if test="${u:isPermitted('sku-updatePage')}">
	                	<button type="button" class="btn btn-default btn-lg2" href="updatePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#updateSku">修改</button>
	            	</c:if>
	            </div>
	        </div>
	        <!--Main CommonHead-->
	        <div class="main_commonhead hl30">
	        	<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
	        	<form action="list" id="search-form" class="unKeyCode">
		            <section>
		                <strong>商品名称:</strong>
		                <div class="form-group w160 pull-left"><input name="skuName" class="form-control input-sm" type="text" placeholder="请输入商品名称"></div>
		            </section>
					<section>
						<strong>商品编码:</strong>
						<div class="form-group w160 pull-left"><input name="skuCode" class="form-control input-sm" type="text" placeholder="请输入商品编码"></div>
					</section>
		            <section>
		                <strong>类型组:</strong>
		                <div class="form-group w120 pull-left">
		                    <select name="skuTypePid" class="pull-left form-control input-sm" id="skuTypeGroup" onchange="refreshRenderSkuType()" >
		                    </select>
		                </div>
		            </section>
		            <section>
		                <strong>类型:</strong>
		                <div class="form-group w120 pull-left">
		                    <select name="skuTypeId" class="form-control input-sm" id="skuType" >
		                    </select>
		                </div>
		            </section>
					<div class="diyBtnGroup pull-left">
						<button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
						<button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
						<div class="clearfix"></div>
					</div>
					<div class="clearfix"></div>
	            </form>
	        </div>
	
	        <!--Main CommonTable-->
	        <div class="main_commontable tablePlaceholder">
	            <!--theadscroll start-->
	            <div class="theadscroll" id="theadScroll">
	                <ul>
	                    <li class="wh3"><input type="checkbox" value=""></li>
	                    <li class="wh8">商品图片</li>
	                    <li class="wh7">商品名称</li>
	                    <li class="wh5">商品规格</li>
	                    <li class="wh5">商品编码</li>
	                    <li class="wh10">建议零售价(分)</li>
	                    <li class="wh5">商品大类</li>
	                    <li class="wh5">商品小类</li>
	                    <li class="wh8">尺寸(毫米)</li>
	                    <li class="wh10">商品描述</li>
	                    <li class="wh7">销售区域</li>
	                    <li class="wh7">其他信息</li>
	                    <li class="wh10">创建信息</li>
	                    <li class="wh10">修改信息</li>
	                </ul>
	            </div>
	            <!--theadscroll end-->
	            <div id="pagediv">
	            	<jsp:include page="/sku/list" flush="false" />
	            </div>
	        </div>
	    </div>
	
	    <!----
	      **  模态框 **
	    ---->
	    <!-----编辑商品 模态框 start----->
	    <div class="modal fade" id="changeSku" tabindex="-1" role="dialog" aria-labelledby="编辑商品" aria-hidden="true" ></div>
	
	    <!--其他信息 -->
	    <div class="modal fade" id="checkInfo" tabindex="-1" role="dialog" aria-labelledby="查看其他信息" aria-hidden="true" ></div>
	
	    <!--新建商品 -->
	    <div class="modal fade" id="addSku" tabindex="-1" role="dialog" aria-labelledby="新建商品" aria-hidden="true" ></div>
		
		<!--修改商品 -->
		<div class="modal fade" id="updateSku" tabindex="-1" role="dialog" aria-labelledby="修改商品" aria-hidden="true" ></div>
	
	
	    <!-----参考窗口 模态框----->
	    <div class="modal fade" id="viewModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        <div class="modal-dialog">
	            <div class="modal-content">
	                <div class="modal-header">
	                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                    <h4 class="modal-title" id="viewModalLabel">配送工单模态框标题</h4>
	                </div>
	                <div class="modal-body">
	                    在这里添加一些文本
	                </div>
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                    <button type="button" class="btn btn-success">确定</button>
	                </div>
	            </div>
	        </div>
	    </div>
	
	</div>
	</body>
</html>
<script>
	var sku = '${u:getSkuJson()}';
	// 将字符串转成JSON对象
	var skuJson = eval("(" + sku + ")");
	
	renderSkuTypeGroup();
	renderSkuType("");
	// 页面加载完成后执行的方法
	/* $(function(){
	    renderTypeGroup();
	
		var groupId = $("#typeGroupAdd").val();
	
	    renderType(groupId);
	    
	}); */

</script>
