<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>客户信息 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/mtCustomer.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
  </head>
<body>
	<div class="mp_container">
		<div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=customer-home&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
		<div class="mp_main pt125">
			<!--Main Min topheader-->
			<div class="mp_topheader ver2 ver_fixed">
				<h2><strong id="MainHeaderTitle">客户信息</strong></h2>
				<div class="btn-group pull-right" data-toggle="buttons">
					<c:if test="${u:isPermitted('customer-createPage')}">
						<button id="add" type="button" class="btn btn-default btn-lg2" href="/customer/createPage" data-toggle="modal" data-target="#addCustomer" >新增</button>
					</c:if>
					<c:if test="${u:isPermitted('customer-delete')}">
						<button id="deleteButton" type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true">删除</button>
					</c:if>
					<c:if test="${u:isPermitted('customer-updatePage')}">
						<button type="button" class="btn btn-default btn-lg2" href="/customer/updatePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#updateCustomer">修改</button>
					</c:if>
				</div>
			</div>
			<!--Main CommonHead-->
			<form name="" action="/customer/list" class="unKeyCode" id="search-form">
				<div class="main_commonhead hl30">
					<section class="diySltInput">
						<strong>搜索:</strong>
						<div class="form-group w80 pull-left">
							<select class="form-control input-sm" name="searchType">
								<option value="1">客户名称</option>
								<option value="2">负责人姓名</option>
							</select>
						</div>
						<div class="form-group w160 pull-left">
							<input class="form-control input-sm" type="text" placeholder="请输入内容" name="searchName">
						</div>
					</section>
					<section> <strong>客户模式:</strong>
					<div class="form-group w120 pull-left">
						<select class="form-control input-sm" name="customerMode">
							<option value="">请选择</option>
							<c:forEach items="${customerMode.dictionary}" var="tSysDictionary">
                        		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            </c:forEach>
						</select>
					</div>
					</section>
					<section> <strong>客户类型:</strong>
					<div class="form-group w120 pull-left">
						<select class="form-control input-sm" name="customerType">
	                        <option value="">请选择</option>
                            <c:forEach items="${customerType.dictionary}" var="tSysDictionary">
                        		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            </c:forEach>
						</select>
					</div>
					</section>
					<c:if test="${u:isPermitted('customer-list')}">
						<button id="searchButton" type="button" class="btn btn-default f12 btn-lg2">查询</button>
					</c:if>
					<button onclick="reset()" type="button" class="btn btn-default f12 btn-lg2">重置</button>
				</div>
			</form>

			<!--Main CommonTable-->
			<div class="main_commontable">
				<!--theadscroll start-->
				<div class="theadscroll" id="theadScroll">
					<ul>
						<li class="wh3"><input type="checkbox" value=""></li>
						<li class="wh10">客户名称</li>
						<li class="wh6">客户模式</li>
						<li class="wh6">客户类型</li>
						<li class="wh15">公司地址</li>
						<li class="wh12">负责人信息</li>
						<li class="wh12">开户行</li>
						<li class="wh12">纳说登记号</li>
						<li class="wh12">创建信息</li>
						<li class="wh12">修改信息</li>
					</ul>
				</div>
				<!--theadscroll end-->
				<div id="pagediv">
	            	<jsp:include page="/customer/list" flush="false" />
	            </div>
			</div>
		</div>

		<!-----新建客户 模态框 start----->
		<div class="modal fade" id="addCustomer" tabindex="-1" role="dialog" aria-labelledby="新增客户" aria-hidden="true"></div>
		<!-----新建客户 模态框 end----->
	
		<!-----修改客户 模态框 start----->
		<div class="modal fade" id="updateCustomer" tabindex="-1" role="dialog" aria-labelledby="修改客户" aria-hidden="true" ></div>
		<!-----修改客户 模态框 end----->



	</div>
</body>
</html>

