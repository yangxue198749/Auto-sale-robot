<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>售货机促销策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/st_vm_event.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=stVmEvent&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机促销策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
<c:if test="${u:isPermitted('stVmEvent-add')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="${path}/stVmEvent/add" data-toggle="modal" data-target="#addAutoMatSale">新增</button></c:if>
    <c:if test="${u:isPermitted('stVmEvent-update')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path}/stVmEvent/delete">删除</button></c:if>
        <c:if test="${u:isPermitted('stVmEvent-delete')}"><button type="button" class="btn btn-default btn-lg2" href="${path}/stVmEvent/update" data-toggle="modal" data-target="#addAutoMatSale" data-must="true" data-set="#pagediv table tbody input:checkbox" select-one="true">修改</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form action="${path}/stVmEvent/table" method="POST" id ="search-form"  class="unKeyCode">
                <section>
                    <strong>策略名称:</strong>
                    <div class="form-group w160 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入策略名称" id="searchValue" name="searchValue"></div>
                </section>
                <section>
                    <strong>折扣类型:</strong>
                    <div class="form-group w120 pull-left">
                        <select class="form-control input-sm" name="selectValue">
                            <option value="">请选择</option>
                            <option value="1">百分比</option>
                            <option value="2">绝对值</option>
                        </select>
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
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh17">策略名称</li>
                    <li class="wh20">策略说明</li>
                    <li class="wh15">折扣类型</li>
                    <li class="wh15">折后价</li>
                    <%--<li class="wh10">应用机器数量</li>--%>
                    <li class="wh15">创建信息</li>
                    <li class="wh15">修改信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            <jsp:include page="/stVmEvent/table" flush="true"></jsp:include>
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新增售货机促销策略 模态框 start----->
    <div class="modal fade" id="addAutoMatSale" tabindex="-1" role="dialog" aria-labelledby="新增售货机促销策略" aria-hidden="true">

    </div>
    <!-----新增售货机促销策略 模态框 end----->



</div>
</body>
</html>
<script type="text/javascript">
$(function(){
StVmEvent.init();
})
</script>