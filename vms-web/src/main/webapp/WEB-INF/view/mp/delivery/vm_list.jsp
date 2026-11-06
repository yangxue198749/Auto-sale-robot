<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>立即补货</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="工单列表">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<!-- 5.0.0a 置顶搜索 -->
<div class="searchFixed">
    <div class="topSearch">
        <div class="searchInput" id="searchForm">
            <input type="text" name="innerCode" id="innerCode" value="">
            <div class="searchBtn">搜索</div>
        </div>
    </div>
</div>
<div class="mh_container">
<!-- / 5.0.0a 置顶搜索 -->

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    function getOutErrorList() {
        $.ajax({
            url: "/mp/delivery/page",
            type: "POST",
            success: function (data) {
                $(".mh_container").html(data.html);
            }
        });
    }
    function searchVmList(innerCode) {
        $.ajax({
            url: "/mp/delivery/page",
            data: {innerCode: innerCode},
            type: "POST",
            success: function (data) {
                $(".mh_container").html(data.html);
                if(data.html==''){
                    $(".mh_container").html("<p class=\"searchEmpty\">未找到相关内容，请确认搜索内容</p>");
                }
            }
        });
    }
    function checkVm(vmId) {
        var taskId;
        $.ajax({
            url: "/mp/delivery/check",
            data: {vmId: vmId},
            type: "POST",
            success: function (data) {
                if (data.type == 'error') {
                    $.toast(data.message, "text");
                }
                if (data.type == 'success') {
                    taskId = data.taskId;
                    window.location.href = "/mp/mpTask/delivery/detail?taskId=" + taskId;
                }
                if (data.type == 'confirm') {
                    taskId = data.taskId;
                    $.confirm({title: '请确认',text: data.message,
                        onOK: function () {
                            createTask(vmId,taskId);
                        },
                        onCancel: function () {
                        }
                    });
                }
            }
        });
    }

    function createTask(vmId,taskId){
        $.ajax({
            url: "/mp/delivery/create",
            data: {vmId: vmId,taskId:taskId},
            type: "POST",
            success: function (data) {
                if (data.type == 'error') {
                    $.toast(data.message, "text");
                }
                if (data.type == 'success') {
                    taskId = data.taskId;
                    window.location.href = "/mp/mpTask/delivery/detail?taskId=" + taskId;
                }
            }
        });
    }

    $(function () {
        //搜索
        $('#searchForm .searchBtn').click(function(){
            var innerCode = $('#innerCode').val();
            searchVmList(innerCode);
        });

        $('#searchForm').ons
            /*$('#innerCode').change(function(){
                if($(this).val().length < 2){
                    $.toast("最少输入2个字符", "text");
                    return false;
                }
                $('#searchForm').submit();
            });
            $('#searchForm').submit(function(){
                var innerCode = $('#innerCode').val();
                //alert(innerCode);
                searchVmList(innerCode);
                return false;
            });*/


        FastClick.attach(document.body);
        getOutErrorList();
        $(".mh_container").on('click', '.weui_cell_primary', function () {
            var vmId = $(this).attr("vmId");
            checkVm(vmId);
            //$.toast("该售货机货品已满，无需补货", "text");
            //$.alert("自定义的消息内容", "标题", function() {
            //点击确认后的回调函数
            //});
//            $.alert("该售货机货品已满，无需补货");
            //$.alert("自定义的消息内容", "自定义的标题");
        })
    });
</script>
<!----------------Common js end------------------>

</body>
</html>
