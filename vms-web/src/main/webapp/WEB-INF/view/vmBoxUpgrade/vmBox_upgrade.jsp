<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>vmBox升级 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/vmBoxUpgrade.js"></script>
    <script type="text/javascript" src="/vmsjs/area.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=vmBoxUpgrade-list&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">升级计划</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('vmBoxUpgrade-add')}">
                    <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#updateModal" href="/vmBoxUpgrade/createDialog">新增</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form id="search-form" action="/vmBoxUpgrade/listPage" method="post" class="unKeyCode">
                <section>
                    <strong>版本号:</strong>
                    <div class="form-group w160 pull-left">
                        <div class="form-group w160 pull-left"><input class="form-control input-sm" name="versionNo" type="text" placeholder="请输入版本号"></div>
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
                    <li class="wh20">升级类型</li>
                    <li class="wh20">版本号</li>
                    <li class="wh20">升级区域</li>
                    <li class="wh15">升级型号</li>
                    <li class="wh10">升级结果</li>
                    <li class="wh15">创建信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
                <jsp:include page="/vmBoxUpgrade/listPage" flush="true"/>
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----升级结果 viewUpdateDetail 模态框 start----->
    <div class="modal fade" id="viewUpdateDetail"  tabindex="-1" role="dialog" aria-labelledby="升级结果" aria-hidden="true">

    </div>
    <!-----升级结果 viewUpdateDetail 模态框 end----->

    <!-----升级 updateModal 模态框 start----->
    <div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="升级" aria-hidden="true">

    </div>
    <!-----升级 updateModal 模态框 end----->

    <!-----关联程序 withProgram 模态框 start----->
    <div class="modal fade" id="withProgram" tabindex="-1" role="dialog" aria-labelledby="关联程序" aria-hidden="true">

    </div>
    <!-----关联程序 withProgram 模态框 end----->

    <!-----选择型号 chooseMat 模态框 start----->
    <div class="modal fade" id="chooseMat" tabindex="-1" role="dialog" aria-labelledby="选择型号" aria-hidden="true">

    </div>
    <!-----选择型号 chooseMat 模态框 end----->
    <script>
        $(function(){

            VmBoxUpgrade.init();
            //动态创建时，时间控件 需要加载timeWidgetInit();
            timeWidgetInit();
            //点击上传文件后改变按钮的文字

        });

        //时间控件  模态框为动态创建
        function timeWidgetInit(){
            $('.timewidget').datetimepicker({
                language:  'zh-CN',
                format: 'hh:ii',
                weekStart: 1,
                todayBtn:  1,
                autoclose: 1,
                todayHighlight: 1,
                startView: 1,
                minuteStep: 1,
                minView: 0,
                maxView: 1,
                forceParse: 0
            })
        }
    </script>

</div>
</body>
</html>

