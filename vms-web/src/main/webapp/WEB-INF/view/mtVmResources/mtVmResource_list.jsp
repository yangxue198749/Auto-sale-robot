<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>闪购跳转页策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/mtVmResources.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=st-mtvm-resources-list&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">闪购跳转页策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('st-mtvm-resources-add')}">
                    <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addMtVmResourcesDiv" href="/mtVmResources/addPage">新增</button>
                </c:if>
                <c:if test="${u:isPermitted('st-mtvm-resources-delete')}">
                    <button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" href="/mtVmResources/deleteMtVmResources">删除</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form class="unKeyCode" action="/mtVmResources/listPage" method="POST" id = "search-form">
                <section>
                    <strong>跳转页名称:</strong>
                    <div class="form-group w160 pull-left">
                        <input class="form-control input-sm" name="name" type="text" placeholder="请输入皮肤名称">
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
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh30">跳转页名称</li>
                    <li class="wh55">跳转页描述</li>
                    <li class="wh10">创建记录</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/mtVmResources/listPage" flush="true"/>
            </div>

        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建跳转页 模态框 start----->
    <div class="modal fade" id="addMtVmResourcesDiv" tabindex="-1" role="dialog" aria-labelledby="新增跳转页" aria-hidden="true" data-backdrop="static" data-keyboard="false">

    </div>
    <!-----新建跳转页 模态框 end----->
</div>
<script>
    $(function(){
        MtVmResources.init();
    })
</script>
</body>
</html>

