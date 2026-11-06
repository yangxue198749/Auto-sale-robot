<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>用户管理 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/user.js"></script>
    <!--[if lt IE 9]>
      <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
    <div class="mp_container">
        <div class="mp_leftmenu">
            <div class="menu_wrap" id="LeftMenuGroup">
               <jsp:include page="/loadMenu?menuCode=user-list&code=system-manage"/>
            </div>
        </div>
        <div class="mp_topheader fixed">
            <jsp:include page="/home?code=system-manage"/>
            <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
        </div>
        <div class="mp_main pt125">
          <!--Main Min topheader-->
          <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">用户管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
<c:if test="${u:isPermitted('user-add')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/user/editPage" data-toggle="modal" data-target="#edit-user-div">新增</button></c:if>
  <c:if test="${u:isPermitted('user-delete')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" href="${path}/user/deleteUser">删除</button></c:if>
    <c:if test="${u:isPermitted('user-edit')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/user/editPage" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#edit-user-div">修改</button></c:if>
            </div>
            <div class="btn-group pull-right mr20" data-toggle="buttons">
      <c:if test="${u:isPermitted('user-resePw')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/user/resetPw" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#prompt-div">重置密码</button></c:if>
              <%--<button type="button" class="btn btn-default btn-lg2">魔核数据</button>
              <button type="button" class="btn btn-default btn-lg2">魔核配送</button>
              <button type="button" class="btn btn-default btn-lg2">魔核维护</button>--%>
            </div>
          </div>
          <!--Main CommonHead-->
          <form action="${path}/user/listPage" method="POST" id="search-form" class="unKeyCode">
            <div class="main_commonhead hl30">
              <section class="diySltInput">
                <strong>搜索:</strong>
                <div class="form-group w90 pull-left">
                  <select class="form-control input-sm" name="selectType">
                    <option value="1">用户姓名</option>
                    <option value="2">所属公司</option>
                  </select>
                </div>
                <div class="form-group w160 pull-left">
                  <input class="form-control input-sm" type="text" placeholder="请输入搜索内容" name="selectText">
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
                <li class="wh5"><input type="checkbox" value=""></li>
                <li class="wh10">登录账号</li>
                <li class="wh10">用户姓名</li>
                <li class="wh10">手机号码</li>
                <li class="wh15">所属公司</li>
                <li class="wh20">所属角色</li>
                <%-- v3.2 移除魔核权限 <li class="wh10">魔核权限</li>--%>
                <li class="wh10">最后登录时间</li>
                <li class="wh10">创建信息</li>
                <li class="wh10">修改信息</li>
              </ul>
            </div>
            <!--theadscroll end-->
          <div id="pagediv">
            <jsp:include page="/user/listPage" flush="true"/>
          </div>
        </div>
      </div>

      <div id="role-ids-div" value=""> </div>

      <!-- 模态框 -->
      <!-- 编辑用户 -->
      <div class="modal fade" id="edit-user-div" tabindex="-1" role="dialog" aria-labelledby="编辑用户" aria-hidden="true"> </div>
      <!-- 选择角色 -->
      <div class="modal fade" id="edit-role-div" tabindex="-1" role="dialog" aria-labelledby="修改角色" aria-hidden="true" value=""> </div>
      <!-- 提示框 -->
      <div class="modal fade" id="prompt-div" tabindex="-1" role="dialog" aria-labelledby="提示" aria-hidden="true"> </div>
    </div>
  </body>
</html>
<script type="text/javascript">
    $(function () {
        User.init();
    })
</script>
