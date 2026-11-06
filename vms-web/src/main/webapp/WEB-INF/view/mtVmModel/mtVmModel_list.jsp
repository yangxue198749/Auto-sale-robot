<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>售货机型号 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/mt_VmModel.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
  </head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=vmModel-home&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机型号</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
            	<c:if test="${u:isPermitted('vmModel-createPage')}">
                	<button type="button" class="btn btn-default btn-lg2" href="createPage" data-toggle="modal" data-target="#addModel">新增</button>
                </c:if>
                	
                <c:if test="${u:isPermitted('vmModel-delete')}">
                	<button id="deleteVmModel" type="button" class="btn btn-default btn-lg2" >删除</button>
                </c:if>
                
                <c:if test="${u:isPermitted('vmModel-updatePage')}">
                	<button type="button" class="btn btn-default btn-lg2" href="updatePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#editModel">修改型号</button>
                </c:if>
                
                <c:if test="${u:isPermitted('vmModel-update-channel')}">
                	<button id="updateModelTable" type="button" class="btn btn-default btn-lg2" >修改货道</button>
                </c:if>
                
                <button type="button" class="btn btn-default btn-lg2" href="/vmModelChannel/lookPage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#lookModelTable">查看货道</button>
                <c:if test="${u:isPermitted('vm-model-change-locked')}">
                    <button id="lockedOrUnModel" type="button" class="btn btn-default btn-lg2" >禁用/启用</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        	<form action="/vmModel/list" id="search-form" class="unKeyCode">
	            <section class="diySltInput">
	                <strong>搜索字段:</strong>
	                <div class="form-group w100 pull-left">
	                    <select class="form-control input-sm">
	                        <option value="1">售货机型号</option>
	                    </select>
	                </div>
	                <div class="form-group w160 pull-left">
                        <input class="form-control input-sm" type="text" placeholder="请输入搜索内容" name="name" >
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
                    <li class="wh7">售货机型号</li>
                    <li class="wh7">机型分类</li>
                    <li class="wh15">型号描述</li>
                    <li class="wh8">货道类型</li>
                    <li class="wh7">组合模式</li>
                    <li class="wh8">展示位数量</li>
                    <li class="wh8">货道层数</li>
                    <li class="wh8">单层货道数</li>
                    <li class="wh8">更多信息</li>
                    <li class="wh8">创建信息</li>
                    <li class="wh8">修改信息</li>
                    <li class="wh5">状态</li>
                </ul>
            </div>
            <!--theadscroll end-->
            
            <div id="pagediv">
            	<jsp:include page="/vmModel/list" flush="false" />
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建型号 模态框 start----->
    <div class="modal fade" id="addModel" tabindex="-1" role="dialog" aria-labelledby="新建型号" aria-hidden="true"></div>

    <!--新建型号table  -->
    <div class="modal fade" id="addModelTable" tabindex="-1" role="dialog" aria-labelledby="新建型号" aria-hidden="true"></div>
	
    <!-----设置货道属性行 模态框----->
    <div class="modal fade" id="modelTableRow"  role="dialog" aria-labelledby="设置货道属性" aria-hidden="true"></div>

    <!-----设置货道属性列 模态框----->
    <div class="modal fade" id="modelTable"  role="dialog" aria-labelledby="设置货道属性" aria-hidden="true"></div>
    
    <!-----修改售货机型号----->
    <div class="modal fade" id="editModel"  role="dialog" aria-labelledby="修改型号" aria-hidden="true"></div>
    
    <!-----修改售货机型号----->
    <div class="modal fade" id="editModelTable"  role="dialog" aria-labelledby="修改型号配置" aria-hidden="true"></div>

    <!--查看货道   -->
    <div class="modal fade" id="lookModelTable" tabindex="-1" role="dialog" aria-labelledby="查看货道" aria-hidden="true"></div>


    <!-----查看其他信息 模态框----->
    <div class="modal fade" id="lookOtherInfo" tabindex="-1" role="dialog" aria-labelledby="其他信息" aria-hidden="true"></div>



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
