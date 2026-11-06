<%@ page language="java" contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>字典信息 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/sysType.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
   <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=sysType-home&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">字典信息</strong></h2>
        </div>
        <!--Main CommonHead-->
        <div class="row">
        <div class="col-md-6">
            <div class="main_commonhead hl30">
            	<form action="list" id="search-form" class="unKeyCode pull-left">
	                <section>
	                    <strong>字典类型名称:</strong>
	                    <div class="form-group pull-left">
	                        <input name="name" class="form-control input-sm pull-left w160" placeholder="请输入字典类型名称">
	                    </div>
	                </section>
                    <div class="diyBtnGroup pull-left">
                        <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                        <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                        <div class="clearfix"></div>
                    </div>
                    <div class="clearfix"></div>
            	</form>
                <div class="btn-group pull-right" data-toggle="buttons">
                	<c:if test="${u:isPermitted('sysType-create')}">
                    	<button type="button" class="btn btn-default btn-lg2 f12" href="createPage" data-toggle="modal" data-target="#addSysType">新增</button>
                    </c:if>
                    <c:if test="${u:isPermitted('sysType-update')}">
                    	<button type="button" class="btn btn-default btn-lg2 f12" href="updatePage" data-toggle="modal" data-set="#pagediv1 table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#updateSysType">修改</button>
                    </c:if>
                    <%-- <c:if test="${u:isPermitted('sysType-delete')}">
                    	<button id="deleteSysType" type="button" class="btn btn-default btn-lg2">删除</button>
                    </c:if> --%>
                </div>
                <div class="clearfix"></div>
            </div>

            <!--Main CommonTable-->
            <div class="main_commontable">
                <!--theadscroll start-->
                <div class="theadscroll">
                    <ul>
                        <li class="wh5"><input type="checkbox" value=""></li>
                        <li class="wh20">字典类型编码</li>
                        <li class="wh25">字典类型名称</li>
                        <li class="wh50">字典类型说明</li>
                    </ul>
                </div>
                <!--theadscroll end-->
                <div id="pagediv1">
	            	<jsp:include page="/sysType/list" flush="false" />
	            </div>
                
            </div>
        </div>
        <div class="col-md-6">
            <div class="main_commonhead hl30">
            	<form action="${path}/dictionary/list" id="dictionary_list_form" class="pull-left">
	                <section>
	                    <strong>字典内容:</strong>
	                    <div class="form-group pull-left">
	                        <input name="name" class="form-control input-sm pull-left w160" placeholder="请输入字典内容">
	                    </div>
	                </section>
	                <c:if test="${u:isPermitted('dictionary-list')}">
	                	<button id="dictionary_list_button" type="button" class="btn btn-default btn-lg2 f12">查询</button>
	                </c:if>
	                <button onclick='reset("dictionary_list_form")' type="button" class="btn btn-default btn-lg2 f12">重置</button>
            	</form>
                <div class="btn-group pull-right" data-toggle="buttons">
                	<c:if test="${u:isPermitted('dictionary-create')}">
                    	<button type="button" class="btn btn-default btn-lg2 f12" href="${path}/dictionary/createPage" data-toggle="modal" data-target="#addDictionary">新增</button>
                    </c:if>
                    <c:if test="${u:isPermitted('dictionary-update')}">
                    	<button type="button" class="btn btn-default btn-lg2 f12" href="${path}/dictionary/updatePage" data-toggle="modal" data-set="#pagediv2 table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#updateDictionary">修改</button>
                    </c:if>
                    <%-- <c:if test="${u:isPermitted('dictionary-delete')}">
                    	<button id="deleteDictionary" type="button" class="btn btn-default btn-lg2" >删除</button>
                	</c:if> --%>
                </div>
                <div class="clearfix"></div>
            </div>

            <!--Main CommonTable-->
            <div class="main_commontable">
                <!--theadscroll start-->
                <div class="theadscroll">
                    <ul>
                        <li class="wh5"><input type="checkbox" value=""></li>
                        <li class="wh15">字典数据编码</li>
                        <li class="wh30">字典数据名称</li>
                        <li class="wh10">状态</li>
                        <li class="wh40">字典数据说明</li>
                    </ul>
                </div>
                <!--theadscroll end-->
                <div id="pagediv2">
	            	<jsp:include page="/dictionary/list" flush="false" />
	            </div>
            </div>
        </div>
        </div>

    </div>
    <script>
        $(function(){
            $(".wordbook").hide();
        });
    </script>

    <!-----新增字典类型 模态框 ----->
    <div class="modal fade" id="addSysType" tabindex="-1" role="dialog" aria-labelledby="新建字典类型" aria-hidden="true"></div>
    
    <!-----修改字典类型 模态框 ----->
    <div class="modal fade" id="updateSysType" tabindex="-1" role="dialog" aria-labelledby="修改字典类型" aria-hidden="true"></div>

    <!-----新增字典 模态框 ----->
    <div class="modal fade" id="addDictionary" tabindex="-1" role="dialog" aria-labelledby="新建字典" aria-hidden="true"></div>
    
    <!-----修改字典 模态框 ----->
    <div class="modal fade" id="updateDictionary" tabindex="-1" role="dialog" aria-labelledby="修改字典" aria-hidden="true"></div>



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
