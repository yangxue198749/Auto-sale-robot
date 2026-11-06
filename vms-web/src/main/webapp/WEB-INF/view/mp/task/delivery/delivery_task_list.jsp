<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>配送工单</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="配送工单">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Pragma" content="no-cache">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">
    <!--tabHeader start-->
    <div class="tabHeader SendTabHeader">
        <ul>
            <li class="on"><span>进行中<i class="icon_flag min postion">${fn:length(ongoingTaskList)}</i></span></li>
            <li><span>待完成<i class="icon_flag min postion">${fn:length(initialTaskList)}</i></span></li>
            <li><span>已处理<i class="icon_flag gray min postion">${fn:length(closeTaskList)}</i></span></li>
        </ul>
    </div>
    <!--tabHeader end-->
    <!--SendTabBody start-->
    <div class="SendTabBody">
        <!--进行中 start-->
        <div class="status_wrap pt45">

            <!--本次提货量入口-->
            <%--<div class="weui_cells weui_cells_access">--%>
                <%--<a class="weui_cell" href="${path}/mp/mpTask/delivery/order/list">--%>
                    <%--<div class="weui_cell_hd"></div>--%>
                    <%--<div class="weui_cell_bd weui_cell_primary">--%>
                        <%--<p class="f14 cf6">本次提货量(件)</p>--%>
                    <%--</div>--%>
                    <%--<div class="weui_cell_ft2"><span class="cf_green">${count}</span></div>--%>
                <%--</a>--%>
            <%--</div>--%>

            <!--list start-->
            <div class="weui_cells weui_cells_access mt15 listcolor">
                <c:forEach items="${ongoingTaskList}" var="task">
                    <section class="weui_cell">
                        <div class="weui_cell_hd removeSection">
                            <i class="weui_icon_cancel2 "></i>
                            <input type="hidden" value="${task.taskVmId}" name="">
                        </div>
                        <a class="weui_cell_bd weui_cell_primary"
                           href="${path}/mp/mpTask/delivery/detail?taskId=${task.taskVmId}">
                            <p class="f14 cf3">${task.mtVm.innerCode}</p>
                            <p class="f12 cf9">${task.mtVm.node.nodeName}</p>
                        </a>
                        <div class="weui_cell_ft2"></div>
                    </section>
                </c:forEach>

            </div>
            <!--list end-->
        </div>
        <!--进行中 end-->

        <!--待完成 start-->
        <div class="status_wrap pt45 pb60" style="display:none;">

            <!--提货总量入口-->
            <%--<div class="weui_cells weui_cells_access">
                <a class="weui_cell" href="javascript:;">
                    <div class="weui_cell_hd"></div>
                    <div class="weui_cell_bd weui_cell_primary">
                        <p class="f14 cf6">提货总量(件)</p>
                    </div>
                    <div class="weui_cell_hd"><span class="cf_green">3565</span></div>
                </a>
            </div>--%>

            <!--list start-->
            <div class="weui_cells mt15 listcolor" id="initial_list">
                <c:forEach items="${initialTaskList}" var="task">
                    <div class="weui_cell">
                        <div class="weui_cell_hd checkbox_wp">
                            <i class="weui_icon_check"></i><!--添加.checked后，为选中后效果-->
                            <input type="hidden" value="${task.taskVmId}" name="">
                        </div>
                        <a class="weui_cell_bd weui_cell_primary"
                           href="${path}/mp/mpTask/delivery/detail?taskId=${task.taskVmId}">
                            <p class="f14 cf3">${task.mtVm.innerCode}</p>
                            <p class="f12 cf9">${task.mtVm.node.nodeName}</p>
                        </a>
                        <div class="weui_cell_ft2"></div>
                    </div>
                </c:forEach>
            </div>
            <!--list end-->
            <a class="weui_btn weui_btn_primary fixedbtm" id="joinOrderBtn" href="javascript:">加入本次任务</a>
        </div>
        <!--待完成 end-->

        <!--已处理 start-->
        <div class="status_wrap pt45" style="display:none">
            <!--list start-->
            <div class="weui_cells weui_cells_access mt15 listcolor">
                <c:forEach items="${closeTaskList}" var="task">
                    <section class="weui_cell">
                        <a class="weui_cell_bd weui_cell_primary"
                           href="${path}/mp/mpTask/delivery/detail?taskId=${task.taskVmId}">
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
    $(function () {
        var rnumber = Math.floor(Math.random() * 100000);
        history.replaceState({mod: rnumber}, 'Title', '?mod=' + rnumber);
//        pushHistory();
        window.addEventListener("popstate", function(e) {
                var needRefresh = sessionStorage.getItem("need-refresh");
                if(needRefresh){
                    sessionStorage.removeItem("need-refresh");
                    location.reload();
                }
//                window.location.reload();
        }, false);
//        function pushHistory() {
//            var state = {
//                title: "title",
//                url: "#"
//            };
//            window.history.pushState(state, "title", "/mp/mpTask/list");
//        }
        $(document).on("click", ".removeSection", function () {
            var taskId = $(this).find("input[type='hidden']").val();
            $.ajax({
                type: 'post',
                data: {"taskId": taskId},
                url: '/mp/mpTask/delivery/exit',
                async: false,
                dataType: 'json',
                cache: false,
                success: function (data) {
                    if (data.type == 'error') {
                        $.toast("退出失败", "text");
                    }
                    else {
                        window.location.href = data.url;
                    }
                }
            });
        });
        $("#joinOrderBtn").click(function () {
            var checkInput = $("#initial_list .weui_icon_check.checked");
            var taskIds = new Array();
            //加入任务
            checkInput.each(function () {
                taskIds.push($(this).siblings("input[type='hidden']").val());
            });
            if (taskIds.length > 0) {
                $.ajax({
                    type: 'post',
                    data: {"taskIds": taskIds.join(",")},
                    url: '/mp/mpTask/delivery/join',
                    async: false,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        if (data.type == 'error') {
                            $.toast("加入失败", "text");
                        }
                        else {
                            window.location.href = data.url;
                        }
                    }
                });
            }
            else {
                $.toast("请选择一个任务", "text");
            }
        })
    });
</script>
<!----------------Common js end------------------>

<!--- WorkOrder 交互js start--->
<script type="text/javascript" src="${path}/mp-static/js/WorkOrder.js"></script>
<!--- WorkOrder 交互js end --->

</body>
</html>
