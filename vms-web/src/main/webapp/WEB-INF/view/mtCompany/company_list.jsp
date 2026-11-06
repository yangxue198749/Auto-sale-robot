<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>公司管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/mtCompany.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
  </head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=company_home&code=system-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=system-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">公司管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
            	<c:if test="${u:isPermitted('company_createPage')}">
            		<c:if test="${isCreateCompany }">
                		<button type="button" class="btn btn-default btn-lg2" href="createPage" data-toggle="modal" data-target="#addCompany">新增</button>
                	</c:if>
                </c:if>
                <c:if test="${u:isPermitted('company_updatePage')}">
                	<button type="button" class="btn btn-default btn-lg2" href="updatePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#changeCompany">修改</button>
                </c:if>
                <c:if test="${u:isPermitted('company_upgrade')}">
	                <c:if test="${isUpgrade }" >
		                <button type="button" class="btn btn-default btn-lg2" href="upgradePage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#upgradeCompany" id="upgradeCompanyButton">升级</button>
					</c:if>
				</c:if>
				<c:if test="${u:isPermitted('company_addCapacityPage')}">
					<c:if test="${maxSum != null && maxSum > 0 }" >
						<button type="button" class="btn btn-default btn-lg2" href="addCapacityPage" data-toggle="modal" data-set="#pagediv table tbody input[type=checkbox]:checked" data-must="true" select-one="true" data-target="#addCapacity">新增机器</button>
	            	</c:if>
            	</c:if>
                <button id="exportOrUnStock" type="button" class="btn btn-default btn-lg2" >启用导出/取消</button>
            </div>

        </div>
        <!--Main CommonHead-->
        <form action="/company/list" method="POST" id="search-form" class="unKeyCode">
	        <div class="main_commonhead hl30">
	            <section class="diySltInput">
	                <strong>搜索:</strong>
	                <div class="form-group w90 pull-left">
	                    <select class="form-control input-sm" name="searchType">
	                        <option value="1">公司名称</option>
	                        <option value="2">所属客户</option>
	                        <option value="3">管理员姓名</option>
	                    </select>
	                </div>
	                <div class="form-group w160 pull-left">
	                    <input name="searchName" class="form-control input-sm" type="text" placeholder="请输入策略名称">
	                </div>
	            </section>
                <div class="diyBtnGroup pull-left">
                    <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                    <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>
	        </div>
        </form>

        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh20">公司名称</li>
                    <li class="wh10">所属客户</li>
                    <li class="wh7">售货机数量</li>
                    <li class="wh10">管理员姓名</li>
                    <li class="wh10">登录账号</li>
                    <li class="wh10">手机号码</li>
                    <li class="wh10">创建信息</li>
                    <li class="wh10">修改信息</li>
                    <li class="wh10">库存状态</li>
                </ul>
            </div>
            <!--theadscroll end-->
            
            <div id="pagediv">
            	<jsp:include page="/company/list" flush="false" />
            </div>
            
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建公司 模态框 start----->
    <div class="modal fade" id="addCompany" tabindex="-1" role="dialog" aria-labelledby="新建公司" aria-hidden="true">
    </div>
    <!-----新建公司 模态框 end----->

    <!-----升级公司 模态框 start----->
    <div class="modal fade" id="upgradeCompany" tabindex="-1" role="dialog" aria-labelledby="升级公司" aria-hidden="true"></div>
    <!-----升级公司 模态框 end----->
    
    <!-----增加售货机数量 模态框 start----->
    <div class="modal fade" id="addCapacity" tabindex="-1" role="dialog" aria-labelledby="增加售货机数量" aria-hidden="true"></div>
    <!-----增加售货机数量 模态框 end----->
    
    <!-----修改公司 模态框 start----->
	<div class="modal fade" id="changeCompany" tabindex="-1" role="dialog" aria-labelledby="修改公司" aria-hidden="true"></div>
	<!-----修改公司 模态框 end----->

</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
    	MtCompany.init();
    })
</script>
