<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>仓库信息 | 贩售机管理系统</title>
	<%@ include file="/WEB-INF/view/include/head.jsp" %>
	<script type="text/javascript" src="/vmsjs/area.js"></script>
	<script type="text/javascript" src="/vmsjs/mt_warehouse.js"></script>
    <!--[if lt IE 9]>
	<script src="./plus/html5shiv/html5shiv.min.js"></script>
	<![endif]-->
  </head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=warehouse-uniteArea&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->        
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">仓库信息</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
            	<c:if test="${u:isPermitted('warehouse-uniteAreaPage')}">
                	<button type="button" class="btn btn-default btn-lg2" href="uniteAreaPage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#uniteArea">关联区域</button>
            	</c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        <form action="/warehouse/list" id="search-form" class="unKeyCode">
            <section class="diySltInput">
                <strong>搜索:</strong>
                <div class="form-group w90 pull-left">
                    <select class="form-control input-sm" name="type">
                        <option value="2">仓库名称</option>
                        <option value="1">仓库ID</option>
                    </select>
                </div>
                <div class="form-group w160 pull-left">
                    <input name="val" class="form-control input-sm" type="text" placeholder="请输入需要搜索的内容">
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
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh15">仓库ID</li>
                    <li class="wh20">仓库信息</li>
                    <li class="wh10">关联区域</li>
                    <li class="wh10">仓库类型</li>
                    <li class="wh10">仓管员信息</li>
                    <li class="wh10">经纬度</li>
                    <li class="wh10">创建时间</li>
                    <li class="wh10">修改时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/warehouse/list" flush="false" />
            </div>
        </div>
    </div>
    
    <!----
      **  模态框 **
    ---->
    <!-----关联区域 模态框 start----->
    <div class="modal fade" id="uniteArea" tabindex="-1" role="dialog" aria-labelledby="关联区域" aria-hidden="true" ></div>
    <!-----关联区域 模态框 end----->
    <!-----查看关联区域 模态框 start----->
    <div class="modal fade" id="lookUniteArea" tabindex="-1" role="dialog" aria-labelledby="查看关联区域" aria-hidden="true" ></div>
    <!-----查看关联区域 模态框 end----->

</div>
</body>
</html>
