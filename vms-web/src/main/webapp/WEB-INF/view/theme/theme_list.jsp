<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <title>皮肤策略 | 贩售机管理系统</title>
  <%@ include file="/WEB-INF/view/include/head.jsp" %>
  <script type="text/javascript" src="${path}/vmsjs/theme.js"></script>
  <!--[if lt IE 9]>
  <script src="./plus/html5shiv/html5shiv.min.js"></script>
  <![endif]-->
  </head>
  <body>
    <div class="mp_container">
      <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
          <jsp:include page="/loadMenu?menuCode=st-theme-list&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
      <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
      <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
          <h2>
            <strong id="MainHeaderTitle">皮肤信息</strong>
          </h2>
          <div class="btn-group pull-right" data-toggle="buttons">
          	<c:if test="${u:isPermitted('st-vm-theme-add')}">
            	<button type="button" class="btn btn-default btn-lg2" href="${path}/theme/addPage" data-toggle="modal" data-target="#addTheme">新增</button>
            </c:if>
            <c:if test="${u:isPermitted('st-vm-theme-delete')}">
            	<button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" href="${path}/theme/deleteTheme">删除</button>
            </c:if>
          </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/theme/listPage" method="POST" id="search-form" class="unKeyCode">
          <div class="main_commonhead hl30">
            <section>
              <strong>皮肤名称:</strong>
              <div class="form-group w160 pull-left">
                <input class="form-control input-sm" type="text" placeholder="请输入皮肤名称" name="themeName">
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
              <li class="wh3">
                <input type="checkbox" value="">
              </li>
              <li class="wh20">皮肤名称</li>
              <li class="wh57">皮肤描述</li>
              <li class="wh10">效果图</li>
              <li class="wh10">创建记录</li>
            </ul>
          </div>
          <!--theadscroll end-->
          <div id="pagediv">
            <jsp:include page="/theme/listPage" flush="true"/>
          </div>
        </div>
      </div>
      <!-- 模态框 -->
      <!-- 新建皮肤 模态框 start-->
      <div class="modal fade" id="addTheme" tabindex="-1" role="dialog" aria-labelledby="新建皮肤" aria-hidden="true"></div>
      <!-- 新建皮肤 模态框 end -->

      <!-- 查看 模态框 start-->
      <div class="modal fade" id="viewTheme" tabindex="-1" role="dialog" aria-labelledby="查看" aria-hidden="true"></div>
      <!-- 查看 模态框 end -->
    </div>
  </body>
</html>
<script type="text/javascript">
    $(function () {
      Theme.init();
    })
</script>
