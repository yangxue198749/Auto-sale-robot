<%@ page language="java" contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>合伙人信息 | 贩售机管理系统</title>
	<%@ include file="/WEB-INF/view/include/head.jsp" %>
	<script type="text/javascript" src="${path}/vmsjs/mt_partner.js"></script>
    <!--[if lt IE 9]>
	<script src="./plus/html5shiv/html5shiv.min.js"></script>
	<![endif]-->
	</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=partner-home&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">合伙人信息</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
            	<c:if test="${u:isPermitted('partner-createPage')}">
                	<button type="button" class="btn btn-default btn-lg2" href="createPage" data-toggle="modal" data-target="#addPartner">新增</button>
                </c:if>
                <c:if test="${u:isPermitted('partner-updatePage')}">
                	<button type="button" class="btn btn-default btn-lg2" href="updatePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#updatePartner">修改</button>
                </c:if>
                <c:if test="${u:isPermitted('partner-delete')}">
                	<button id="deleteButton" type="button" class="btn btn-default btn-lg2">删除</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        	<form action="list" id="search-form" class="unKeyCode">
	            <section class="diySltInput">
	                <strong>搜索:</strong>
	                <div class="form-group w100 pull-left">
	                    <select class="form-control input-sm" name="searchType">
	                        <option value="1">合伙人名称</option>
	                        <option value="2">负责人姓名</option>
	                    </select>
	                </div>
	                <div class="form-group w160 pull-left">
	                    <input name="searchName" class="form-control input-sm" type="text" placeholder="请输入内容">
	                </div>
	            </section>
	            <section>
	                <strong>合伙人类型:</strong>
	                <div class="form-group w120 pull-left">
	                    <select class="form-control input-sm" name="partnerType">
	                        <option value="">请选择</option>
	                        <c:forEach items="${partnerType.dictionary}" var="tSysDictionary">
                           		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            </c:forEach>
	                    </select>
	                </div>
	            </section>
	            <c:if test="${u:isPermitted('partner-list')}">
	            	<button id="searchButton" type="button" class="btn btn-default f12 btn-lg2">查询</button>
	            </c:if>
	            <button onclick="reset()" type="button" class="btn btn-default f12 btn-lg2">重置</button>
	    	</form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh10">合伙人名称</li>
                    <li class="wh10">合伙人类型</li>
                    <li class="wh15">公司地址</li>
                    <li class="wh10">负责人信息</li>
                    <li class="wh15">开户信息</li>
                    <li class="wh15">纳说登记号</li>
                    <li class="wh10">创建信息</li>
                    <li class="wh10">修改信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/partner/list" flush="false" />
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建客户信息 模态框 start----->
    <div class="modal fade" id="addPartner" tabindex="-1" role="dialog" aria-labelledby="新增客户" aria-hidden="true"></div>
    
    <!-----修改客户信息 模态框 start----->
    <div class="modal fade" id="updatePartner" tabindex="-1" role="dialog" aria-labelledby="修改客户" aria-hidden="true"></div>
    

    <!-----窗口 模态框----->
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
