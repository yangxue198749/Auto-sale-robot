<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>投放工单</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="投放工单">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">
    <!--tabHeader start-->
    <div class="tabHeader SendTabHeader double">
        <ul>
            <li class="on"><span>待完成<i class="icon_flag min postion">${fn:length(initialTaskList)}</i></span></li>
            <li><span>已处理<i class="icon_flag gray min postion">${fn:length(closeTaskList)}</i></span></li>
        </ul>
    </div>
    <!--tabHeader end-->
    <!--SendTabBody start-->
    <div class="SendTabBody">

        <!--待完成 start-->
        <div class="status_wrap pt45 pb60">
            <!--list start-->
            <div class="weui_cells mt15 listcolor">
                <c:forEach items="${initialTaskList}" var="task">
                <div class="weui_cell">
                    <a class="weui_cell_bd weui_cell_primary" href="${path}/mp/mpTask/deploy/detail?taskId=${task.taskVmId}">
                        <p class="f14 cf3">${task.mtVm.innerCode}</p>
                        <p class="f12 cf9">${task.mtVm.node.nodeName}</p>
                    </a>
                    <div class="weui_cell_ft2"></div>
                </div>
                </c:forEach>

            </div>
            <!--list end-->
        </div>
        <!--待完成 end-->

        <!--已处理 start-->
        <div class="status_wrap pt45" style="display:none">
            <!--list start-->
            <div class="weui_cells weui_cells_access mt15 listcolor">
                <c:forEach items="${closeTaskList}" var="task">
                <section class="weui_cell">
                    <a class="weui_cell_bd weui_cell_primary" href="${path}/mp/mpTask/deploy/detail?taskId=${task.taskVmId}">
                        <p class="f14 cf3">${task.mtVm.innerCode}</p>
                        <p class="f12 cf9">${task.mtVm.node.nodeName}</p>
                    </a>
                    <c:choose>
                        <c:when test="${task.taskVmStatus eq 7}">
                            <div class="weui_cell_ft2"><span class="cf_red f12">已忽略</span></div>
                        </c:when>
                        <c:otherwise>
                            <div class="weui_cell_ft2"><span class="cf9 f12">已完成</span></div>
                        </c:otherwise>
                    </c:choose>
                </section>
                </c:forEach>
            </div>
            <!--list end-->
        </div>
        <!--已处理 end-->
    </div>
    <!--SendTabBody end-->

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        var rnumber = Math.floor(Math.random()*1000);
        history.replaceState({mod: rnumber}, 'Title', '?mod='+rnumber);

//        pushHistory();
//        window.addEventListener("popstate", function(e) {
//            window.location.href = "/mp/mpTask/list";
//        }, false);
//        function pushHistory() {
//            var state = {
//                title: "title",
//                url: "#"
//            };
//            window.history.pushState(state, "title", "#");
//        }
        FastClick.attach(document.body);
    });
</script>
<!----------------Common js end------------------>

<!--- WorkOrder 交互js start--->
<script type="text/javascript" src="${path}/mp-static/js/WorkOrder.js"></script>
<!--- WorkOrder 交互js end --->

</body>
</html>
