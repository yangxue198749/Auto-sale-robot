<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>售货机状态 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/vmStatus.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=vmStatus-list&code=statistical-analysis"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机状态</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('vm-alarm-subscription')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#alarmsubscription" href="${path}/vmStatus/getCompanyUserList">告警订阅</button></c:if>
                <c:if test="${u:isPermitted('vm-status-dispatch')}"><button type="button" class="btn btn-default btn-lg2 btn_tableUpdate" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/vmStatus/dispatch">派修</button></c:if>
                <c:if test="${u:isPermitted('vm-status-unDispatch')}"><button type="button" class="btn btn-default btn-lg2 btn_tableUpdate" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/vmStatus/unDispatch">取消派修</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form action="${path}/vmStatus/listPage" method="POST" id="search-form" class="unKeyCode">
        <div class="main_commonhead hl30">
            <section class="diySltInput">
                <strong>搜索:</strong>
                <div class="form-group w100 pull-left">
                    <select class="form-control input-sm select2me" name="selectType">
                        <option value="1">售货机编号</option>
                        <option value="2">点位</option>
                    </select>
                </div>
                <div class="form-group w160 pull-left">
                    <input class="form-control input-sm" type="text" placeholder="请输入搜索内容" name="selectValue">
                </div>
            </section>
            <section>
                <strong>VMC状态:</strong>
                <div class="form-group w120 pull-left">
                    <select class="form-control input-sm select2me" name="vmcStatus">
                        <option value="">请选择</option>
                        <option value="0">正常</option>
                        <option value="1">故障</option>
                    </select>
                </div>
            </section>
            <section>
                <strong>网络状态:</strong>
                <div class="form-group w120 pull-left">
                    <select class="form-control input-sm select2me" name="netStatus">
                        <option value="">请选择</option>
                        <option value="1">正常</option>
                        <option value="2">异常</option>
                    </select>
                </div>
            </section>
            <section>
                <strong>温度状态:</strong>
                <div class="form-group w120 pull-left">
                    <select class="form-control input-sm select2me" name="tempStatus">
                        <option value="">请选择</option>
                        <option value="1">正常</option>
                        <option value="2">异常</option>
                        <option value="3">未知</option>
                    </select>
                </div>
            </section>
            <section>
                <strong>工单状态:</strong>
                <div class="form-group w120 pull-left">
                    <select class="form-control input-sm select2me" name="taskStatus">
                        <option value="">请选择</option>
                        <option value="1">已派修</option>
                        <option value="0">未派修</option>
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
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh8">售货机编号</li>
                    <li class="wh8">点位</li>
                    <li class="wh8">软件版本</li>
                    <li class="wh5">门锁</li>
                    <li class="wh6">设置温度</li>
                    <li class="wh6">实际温度</li>
                    <li class="wh6">温度状态</li>
                    <li class="wh6">VMC状态</li>
                    <li class="wh10">VMC问题</li>
                    <li class="wh8">网络状态</li>
                    <li class="wh8">工单状态</li>
                    <li class="wh8">销售状态</li>
                    <li class="wh8">最后上报时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
          <div id="pagediv">
            <jsp:include page="/vmStatus/listPage" flush="true"/>
          </div>
        </div>
    </div>

    <!---窗口 模态框--->
    <!-----告警订阅 alarmsubscription 模态框 start----->
    <div class="modal fade" id="alarmsubscription" tabindex="-1" role="dialog" aria-labelledby="告警订阅" aria-hidden="true"></div>

</div>
</body>
</html>
<script type="text/javascript">
    $(function () {
        VmStatus.init();
    })

</script>
