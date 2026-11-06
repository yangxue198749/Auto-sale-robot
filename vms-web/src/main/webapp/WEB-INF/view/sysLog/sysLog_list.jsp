<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>系统日志 | 贩售机管理系统</title>
	<%@ include file="/WEB-INF/view/include/head.jsp" %>
	<script type="text/javascript" src="/vmsjs/sysLog.js"></script>
    <!--[if lt IE 9]>
	<script src="./plus/html5shiv/html5shiv.min.js"></script>
	<![endif]-->
  </head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=sysLog-home&code=system-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=system-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">系统日志</strong></h2>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
        	<form name="" action="/sysLog/list" class="unKeyCode" id="search-form" class="unKeyCode">
	            <section class="diySltInput">
	                <strong>搜索:</strong>
	                <div class="form-group w90 pull-left">
	                    <select class="form-control input-sm">
	                        <option value="1">操作用户</option>
	                    </select>
	                </div>
	                <div class="form-group w160 pull-left">
	                    <input name="userName" class="form-control input-sm" type="text" placeholder="请输入搜索内容">
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
                    <li class="wh5">序号</li>
                    <li class="wh15">操作用户</li>
                    <li class="wh10">所属板块</li>
                    <li class="wh10">操作</li>
                    <li class="wh45">详情</li>
                    <li class="wh15">时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/sysLog/list" flush="false" />
            </div>
            <!--table footer end-->
        </div>
    </div>
</div>
</body>
</html>
