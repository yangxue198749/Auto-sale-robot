<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>角色管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/role.js"></script>
    <link href="${path}/plus/jquery-ztree/3.5.12/css/zTreeStyle/zTreeStyle.min.css" rel="stylesheet" type="text/css"/>
    <script src="${path}/plus/jquery-ztree/3.5.12/js/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <!--[if lt IE 9]>
      <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
  <div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=role-list&code=system-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=system-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">角色管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
              <button type="button" class="btn btn-default btn-lg2" href="${path}/role/addPage" data-toggle="modal" data-target="#add-role-div">新增</button>
              <button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" href="${path}/role/deleteRole">删除</button>
              <button type="button" class="btn btn-default btn-lg2" href="${path}/role/editPage" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#edit-role-div">修改</button>
              <button type="button" class="btn btn-default btn-lg2" href="${path}/role/editElementPage" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#edit-element-div">设置权限</button>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/role/listPage" method="POST" id="search-form" class="unKeyCode">
          <div class="main_commonhead hl30">
              <section class="diySltInput">
                  <strong>搜索:</strong>
                  <div class="form-group w90 pull-left">
                      <select class="form-control input-sm" name="selectType">
                          <option value="1">角色名称</option>
                      </select>
                  </div>
                  <div class="form-group w160 pull-left">
                      <input class="form-control input-sm" type="text" placeholder="请输入搜索内容" name="selectValue">
                  </div>
              </section>
              <div class="diyBtnGroup pull-left">
                  <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                  <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                  <div class="clearfix"></div>
              </div>
              <div class="clearfix"></div>
              <%--<button type="button" class="btn btn-default f12 btn-lg2 mr10" href="${path}/role/editElementPage" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#edit-element-div">设置权限</button>--%>
          </div>
        </form>
        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh22">角色名称</li>
                    <li class="wh20">角色类型</li>
                    <li class="wh25">角色描述</li>
                    <li class="wh15">创建信息</li>
                    <li class="wh15">修改信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
          <div id="pagediv">
            <jsp:include page="/role/listPage" flush="true"/>
          </div>
        </div>
    </div>

    <!-- 模态框 -->
    <!-- 新建角色 模态框 -->
    <div class="modal fade" id="add-role-div" tabindex="-1" role="dialog" aria-labelledby="新建角色" aria-hidden="true"> </div>

    <!-- 新建角色 模态框 -->
    <div class="modal fade" id="edit-role-div" tabindex="-1" role="dialog" aria-labelledby="编辑角色" aria-hidden="true"> </div>

    <!-- 设置权限 模态框 -->
    <div class="modal fade" id="edit-element-div" tabindex="-1" role="dialog" aria-labelledby="设置权限" aria-hidden="true"> </div>

    <!-- 提示权限分配成功 模态框 -->
    <div class="modal fade" id="saveInfo" tabindex="-1" role="dialog" aria-labelledby="提示" aria-hidden="true"> </div>
  </body>
</html>
<script type="text/javascript">
    $(function () {
        Role.init();
    })
</script>
