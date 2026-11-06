<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>升级包管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/upgradePackage.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=upgradePackage-list&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">升级包管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('upgradePackage-addUpgradePackage')}">
                    <button type="button" class="btn btn-default btn-lg2" href="/upgradePackage/addPage" data-toggle="modal" data-target="#addUpgradePackage">新增</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="/upgradePackage/listPage" method="POST" id="search-form" class="unKeyCode">
            <div class="main_commonhead hl30">
                <form class="unKeyCode">
    				<section>
    					<strong>程序类型:</strong>
    					<div class="form-group w120 pull-left">
    						<select class="form-control input-sm" name="type">
    							<option value="1">vmBox</option>
    							<option value="2">通信板</option>
    							<option value="3">出货板</option>
    						</select>
    					</div>
    				</section>
    				<section>
    					<strong>版本号:</strong>
    					<div class="form-group w120 pull-left">
                             <input class="form-control input-sm" type="text" name="version" placeholder="请输入搜索内容">
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
        </form>

        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh20">程序类型</li>
                    <li class="wh15">版本号</li>
                    <li class="wh10">查看</li>
                    <li class="wh10">状态</li>
                    <li class="wh15">创建信息</li>
                    <li class="wh15">修改信息</li>
                    <li class="wh15">操作</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/upgradePackage/listPage" flush="true"/>
            </div>
        </div>
    </div>

    <!-- 模态框 -->
    <!-- 新增 addProgram 模态框 start -->
    <div class="modal fade" id="addUpgradePackage" tabindex="-1" role="dialog" aria-labelledby="新增" aria-hidden="true">
    </div>

    <!-- 上传 uploadModal 模态框 end -->
    <script type="text/javascript">
        $(function () {
            UpgradePackage.init();
        });

	</script>
</div>
</body>
</html>
