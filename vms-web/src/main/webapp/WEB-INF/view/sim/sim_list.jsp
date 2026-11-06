<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <title>SIM卡信息 | 贩售机管理系统</title>
  <%@ include file="/WEB-INF/view/include/head.jsp" %>
  <script type="text/javascript" src="${path}/vmsjs/sim.js"></script>
  <!--[if lt IE 9]>
  <script src="./plus/html5shiv/html5shiv.min.js"></script>
  <![endif]-->
  </head>
  <body>
    <div class="mp_container">
      <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=sim-list&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
      <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
          <h2>
            <strong id="MainHeaderTitle">SIM卡信息</strong>
          </h2>
          <div class="btn-group pull-right" data-toggle="buttons">
          	<c:if test="${u:isPermitted('sim-add')}">
            	<button type="button" class="btn btn-default btn-lg2" href="${path}/sim/addPage" data-toggle="modal" data-target="#addSIM">新增</button>
            </c:if>
            <c:if test="${u:isPermitted('sim-delete')}">
            	<button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" href="${path}/sim/deleteSim">删除</button>
            </c:if>
            <c:if test="${u:isPermitted('sim-edit')}">
            	<button type="button" class="btn btn-default btn-lg2" href="${path}/sim/editPage" data-toggle="modal" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true" data-target="#editSIM">修改</button>
          	</c:if>
          </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/sim/listPage" method="POST" id="search-form" class="unKeyCode">
          <div class="main_commonhead hl30">
            <section class="diySltInput">
              <strong>搜索:</strong>
              <div class="form-group w90 pull-left">
                <select class="form-control input-sm" name="selectType">
                  <option value="1">SIM卡号码</option>
                  <option value="2">SIM卡编号</option>
                </select>
              </div>
              <div class="form-group w160 pull-left">
                <input class="form-control input-sm" type="text" placeholder="请输入需要搜索的内容" name="selectText">
              </div>
            </section>
            <section>
              <strong>运营商:</strong>
              <div class="form-group w120 pull-left">
                <select class="form-control input-sm" name="serviceProvider">
                  <option value="">请选择</option>
                  <option value="1">移动</option>
                  <option value="2">联通</option>
                  <option value="3">电信</option>
                </select>
              </div>
            </section>
            <section>
              <strong>SIM卡状态:</strong>
              <div class="form-group w120 pull-left">
                <select class="form-control input-sm" name="simUse">
                  <option value="">请选择</option>
                  <option value="1">使用中</option>
                  <option value="2">未使用</option>
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
              <li class="wh3">
                <input type="checkbox" value="">
              </li>
              <li class="wh15">SIM卡号码</li>
              <li class="wh20">SIM卡编码</li>
              <li class="wh12">运营商</li>
              <li class="wh10">SIM卡状态</li>
              <li class="wh20">创建记录</li>
              <li class="wh20">修改记录</li>
            </ul>
          </div>
          <!--theadscroll end-->
          <div id="pagediv">
            <jsp:include page="/sim/listPage" flush="true"/>
          </div>
        </div>
      </div>
      <!-- 模态框 -->
      <!-- 新建SIM卡 模态框 start-->
      <div class="modal fade" id="addSIM" tabindex="-1" role="dialog" aria-labelledby="新建SIM卡信息" aria-hidden="true"></div>
      <!-- 新建SIM卡 模态框 end -->
      <!-- 编辑SIM卡 模态框 start-->
      <div class="modal fade" id="editSIM" tabindex="-1" role="dialog" aria-labelledby="编辑SIM卡信息" aria-hidden="true"></div>
      <!-- 编辑SIM卡 模态框 end -->
    </div>
  </body>
</html>
<script type="text/javascript">
    $(function () {
        Sim.init();
    })
</script>
