<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>接口管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/open_api.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=openApi-list&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">接口管理</strong></h2>
            <div class="btn-group pull-right mr10" data-toggle="buttons">
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/openApi/addOpenApiDialog"  data-target="#addOpenInterface">新增</button>
            </div>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">

            <div class="commonTable">
                <table>
                    <thead>
                    <tr>
                        <td>公司名称</td>
                        <td>开放接口</td>
                        <td>customer_id</td>
                        <td>key</td>
                        <td width="140">操作</td>
                    </tr>
                    </thead>
                </table>
                <div id="pagediv">
                    <jsp:include page="/openApi/listPage" flush="true" />
                </div>
            </div>
        </div>
    </div>

    <!--- 窗口 模态框 --->
    <!------------- 新增接口 addOpenInterface ------------->
    <div class="modal fade" id="addOpenInterface" tabindex="-1" role="dialog" aria-labelledby="新增接口" aria-hidden="true"></div>
    <!------------- / 新增接口 addOpenInterface ------------->
    <div class="modal fade" id="choiceCompany" tabindex="-1" role="dialog" aria-labelledby="选择公司" aria-hidden="true"></div>
    <!------------- 修改接口 editOpenInterface ------------->
    <div class="modal fade" id="editOpenInterface" tabindex="-1" role="dialog" aria-labelledby="修改接口" aria-hidden="true"></div>
    <!------------- / 修改接口 editOpenInterface ------------->
    <!----- 删除接口 deleteOpenInterface ----->
    <div class="modal fade" id="deleteOpenInterface" tabindex="-1" role="dialog" aria-labelledby="删除接口" aria-hidden="true"></div>
    <!----- / 删除接口 deleteOpenInterface ----->
</div>
</body>
</html>
