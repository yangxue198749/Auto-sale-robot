<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>闪购键策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/shortcuts.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=st-shortcuts&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">闪购键策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
            	<c:if test="${u:isPermitted('st-shortcuts-add')}">
              		<button type="button" class="btn btn-default btn-lg2" href="${path}/stShortcuts/addPage1" data-toggle="modal" data-target="#add-shortcuts-div">新增</button>
              	</c:if>
              	<c:if test="${u:isPermitted('st-shortcuts-delete')}">
              		<button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" href="${path}/stShortcuts/deleteShortcuts">删除</button>
              	</c:if>
              	<c:if test="${u:isPermitted('st-shortcuts-edit')}">
              		<button type="button" class="btn btn-default btn-lg2" href="${path}/stShortcuts/editPage1" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#edit-shortcuts-div">修改</button>
            	</c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/stShortcuts/listPage" class="unKeyCode" method="POST" id="search-form">
          <div class="main_commonhead hl30">
              <section>
                  <strong>策略名称:</strong>
                  <div class="form-group w120 pull-left">
                    <input class="form-control input-sm" type="text" placeholder="请输入策略名称" name="strategyName">
                    </div>
              </section>
              <section>
                  <strong>售货机型号:</strong>
                  <div class="form-group w120 pull-left">
                      <select class="form-control input-sm" name="modelId">
                        <option value="">请选择</option>
  		                  <c:forEach items="${modelList}" var="model">
                          <option value="${model.id}">${model.modelName}</option>
                        </c:forEach>
                      </select>
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
                    <li class="wh10">策略名称</li>
                    <li class="wh15">策略说明</li>
                    <li class="wh10">售货机类型</li>
                    <li class="wh10">售货机型号</li>
                    <li class="wh10">闪购键数量</li>
                    <li class="wh12">闪购键配置</li>
                    <li class="wh15">创建记录</li>
                    <li class="wh15">修改记录</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
              <jsp:include page="/stShortcuts/listPage" flush="true"/>
            </div>
    </div>
    <!-- 模态框 -->
    <!-- 闪购键配置策略-->
    <div class="modal fade" id="view-shortcuts-div" tabindex="-1" role="dialog" aria-labelledby="闪购键配置策略" aria-hidden="true"> </div>
    <!-- 新建闪购键配置策略 -->
    <div class="modal fade" id="add-shortcuts-div" tabindex="-1" role="dialog" aria-labelledby="新建闪购键配置策略" aria-hidden="true"> </div>
    <!-- 编辑闪购键配置策略 -->
    <div class="modal fade" id="edit-shortcuts-div" tabindex="-1" role="dialog" aria-labelledby="编辑闪购键配置策略" aria-hidden="true"> </div>

    <div class="modal fade" id="select-sku-div" tabindex="-1" role="dialog" aria-labelledby="选择商品" aria-hidden="true" tp=""> </div>
    <div class="modal fade" id="selectLink" tabindex="-1" role="dialog" aria-labelledby="选择跳转页" aria-hidden="true"> </div>
</div>
</body>
</html>
<script type="text/javascript">
    $(function () {
        Shortcuts.init();
    })
</script>
