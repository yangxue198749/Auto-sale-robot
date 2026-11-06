<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>维护工单-详情</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="维护工单-详情">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">
    <!--Take the key start-->
<c:if test="${task.taskVmStatus==2||task.taskVmStatus==1}">
    <div class="detailBox head key">
        <%--<div class="title_news"><a href="javascript:;" class="touchbtn">生成开门密码</a><b class="f24 fr keycode" id="pwd"></b></div>--%>
        <div class="title_news"><button type="button" class="touchbtn">生成开门密码</button><b class="f24 fr keycode" id="pwd"></b></div>
        <input type="hidden" value="${task.taskVmId}" id="taskVmId">
    </div>
    </c:if>
    <!--Take the key end-->
    <!--任务说明 start-->
    <div class="weui_cells_title">任务说明</div>
    <div class="weui_cells weui_cells_access mt15">
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">售货机编号</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf_red text_right"><b>${task.mtVm.innerCode}</b></p>
            </div>
        </a>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">执行时间</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf3 text_right"><fmt:formatDate value="${task.taskTime}" type="date" dateStyle="default"/></p>
            </div>
        </a>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">所在点位</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf3 text_right">${task.mtVm.node.nodeName}</p>
            </div>
        </a>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">点位地址</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf3 text_right">${task.mtVm.node.nodeAddress}</p>
            </div>
        </a>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">具体位置</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf3 text_right">${task.mtVm.vmDesc}</p>
            </div>
        </a>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">任务说明</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf_red text_right" style="word-break:break-all; padding-left:10px;">${task.taskRemarks}</p>
            </div>
        </a>
    </div>
    <!--任务说明 end-->
    <!--button fixedbtm-->
    <c:if test="${task.taskVmStatus==2||task.taskVmStatus==1}">
        <a class="weui_btn weui_btn_primary fixedbtm" href="javascript:">完成任务</a>
    </c:if>
    <input type="hidden" value="${task.taskVmId}" id="taskId">
</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script type="text/javascript" src="${path}/mp-static/js/WorkOrder.js"></script>
<script>
    $(function() {
        FastClick.attach(document.body);
        sessionStorage.setItem("need-refresh", true);
        $(".fixedbtm").click(function(){
            var taskId = $("#taskId").val();
            $.confirm("确认维护完成?", function() {
                $.ajax({
                    type : 'post',
                    data:{"taskId":taskId},
                    url :'/mp/mpTask/repair/finish',
                    async : false,
                    dataType : 'json',
                    cache : false,
                    success : function(data) {
                        if(data.type=='error'){
                            $.toast("保存失败", "text");
                        }
                        else{
//                            window.location.href = data.url;
                            window.history.back();
                        }
                    }
                });
            }, function() {
            });
        })
    });
</script>
<!----------------Common js end------------------>

</body>
</html>
