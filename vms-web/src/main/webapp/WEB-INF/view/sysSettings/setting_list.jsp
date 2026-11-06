<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>系统配置信息 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/sysSetting.js"></script>
    <!--[if lt IE 9]>
    <script src="${path}/plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="mp_leftmenu">
            <div class="menu_wrap" id="LeftMenuGroup">
                <jsp:include page="/loadMenu?menuCode=sysSettings&code=system-manage"/>
            </div>
        </div>
        <div class="mp_topheader fixed">
            <jsp:include page="/home?code=system-manage"/>
            <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
        </div>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">配置信息</strong></h2>
        </div>
        <!--Main CommonHead-->
        <div class="col-md-6">
            <div class="main_commonhead hl30">
                <form class="pull-left unKeyCode" id="search-form"  action="${path}/settings/type/table" method="POST">
                    <section>
                        <strong>类型名称:</strong>
                        <div class="form-group pull-left w160">
                            <input class="form-control input-sm" placeholder="请输入类型描述或key值" name="searchValue">
                        </div>
                    </section>
                    <div class="diyBtnGroup pull-left">
                        <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                        <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                        <div class="clearfix"></div>
                    </div>
                    <div class="clearfix"></div>
                    <%--<div class="btn-group" data-toggle="buttons">
                        <button type="button" id="searchButton" class="btn btn-default btn-lg2">查询</button>
                        <button type="button" id="resetButton" class="btn btn-default btn-lg2">重置</button>
                    </div>--%>
                </form>
                <c:if test="${u:isPermitted('settings-type-add')}">
                <%--<button type="button" class="btn btn-success btn-lg2 pull-right" href="${path}/settings/type/add" data-toggle="modal" data-target="#addType">新增</button>--%>
                </c:if>
                <div class="clearfix"></div>
            </div>

            <!--Main CommonTable-->
            <div id="pagediv" class="main_commontable">
                    <jsp:include page="/settings/type/table" flush="true"/>
            </div>
        </div>
        <form action="/settings/setting/table" method="POST" id="search-setting-form">
            <input name="pid" type="hidden">
        </form>
        <div class="col-md-6">
            <div class="main_commonhead hl30">
                <div class="btn-group pull-right" style="height:32px;" data-toggle="buttons">
                <c:if test="${u:isPermitted('settings-setting-add')}">
                    <%--<button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="${path}/settings/setting/add" data-target="#addSetting">新增</button>--%>
                </c:if>
                <c:if test="${u:isPermitted('settings-setting-delete')}">
                    <%--<button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#settings-pagediv table tbody input[type=checkbox]:checked" href="${path}/settings/setting/delete">删除</button>--%>
                </c:if>
                <c:if test="${u:isPermitted('settings-setting-update')}">
                    <button type="button" class="btn btn-default btn-lg2 f12" data-toggle="modal" data-target="#addSetting" href="${path}/settings/setting/update" data-must="true" data-set="#settings-pagediv table tbody input:checkbox" select-one="true">修改</button>
                </c:if>
                </div>
                <div class="clearfix"></div>
            </div>

            <!--Main CommonTable-->
            <div class="main_commontable" id="settings-pagediv">
            </div>
        </div>

    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新增类型 模态框 start----->
    <div class="modal fade" id="addType" tabindex="-1" role="dialog" aria-labelledby="新建类型" aria-hidden="true">

    </div>

    <!-----新增配置 模态框 start----->
    <div class="modal fade" id="addSetting" tabindex="-1" role="dialog" aria-labelledby="新建配置" aria-hidden="true">

    </div>

</div>
</body>
</html>
<script type="text/javascript">
    $(function () {
        SysSetting.init();
    })
</script>
