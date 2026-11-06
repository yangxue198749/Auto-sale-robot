<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>投放工单-详情</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="投放工单-详情">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">
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
                <p class="f12 cf9">售货机型号</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf_red text_right"><b>${task.mtVm.modelName}</b></p>
            </div>
        </a>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">执行时间</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf3 text_right"><fmt:formatDate value="${task.taskTime}" type="date"
                                                              dateStyle="default"/></p>
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
                <p class="f12 cf9">建议时间</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf3 text_right">${task.mtVm.node.putStartTime} 至 ${task.mtVm.node.putEndTime}</p>
            </div>
        </a>
        <%--<a class="weui_cell" href="javascript:;">--%>
            <%--<div class="weui_cell_hd">--%>
                <%--<p class="f12 cf9">投放方式</p>--%>
            <%--</div>--%>
            <%--<div class="weui_cell_bd weui_cell_primary">--%>
                <%--<p class="f14 cf3 text_right">${task.mtVm.node.putMethod}</p>--%>
            <%--</div>--%>
        <%--</a>--%>
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd">
                <p class="f12 cf9">硬件编号</p>
            </div>
            <div class="weui_cell_bd weui_cell_primary bind_scan">
                <input class="weui_input f14 readonly" type="text" placeholder="使用扫一扫，添加硬件编号" readonly
                       id="HardwareCode">
                <aside class="touchscan" id="scanQRCodeBtn"></aside>
            </div>
        </a>
        <div class="weui_cell mb20">
            <p class="f12">请扫描贩售机门体内侧的条形码，会自动填入硬件编号。如果门内没有条形码，<span class="cf_blue" id="enterCode">请点此输入</span></p>
        </div>
        <!--button fixedbtm-->
        <c:if test="${(task.taskVmStatus==2||task.taskVmStatus==1) && canFinish eq 'true'}">
            <a class="weui_btn weui_btn_primary fixedbtm" href="javascript:">完成任务</a>
        </c:if>
        <c:if test="${vmType==3}">
            <a class="weui_btn weui_btn_primary finishBtn" href="javascript:">完成任务</a>
        </c:if>
    </div>
    <!--任务说明 end-->
    <%-- 20180326 by tonney
    <!--button fixedbtm-->
    <c:if test="${(task.taskVmStatus==2||task.taskVmStatus==1) && canFinish eq 'true'}">
        <a class="weui_btn weui_btn_primary fixedbtm" href="javascript:">完成任务</a>
    </c:if>
    <c:if test="${vmType==3}">
        <a class="weui_btn weui_btn_primary finishBtn" href="javascript:">完成任务</a>
    </c:if>--%>
    <input type="hidden" value="${task.taskVmId}" id="taskId">
</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
<script>
    $(function () {
        FastClick.attach(document.body);
        sessionStorage.setItem("need-refresh", true);

        //扫码
        //执行扫描动作scanQRCode
        $('#scanQRCodeBtn').click(function () {
            wx.scanQRCode({
                needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                scanType: ["qrCode", "barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                success: function (res) {
                    var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                    // console.log(result);
//                    $("#HardwareCode").val(result);
                    if (result.indexOf(",") >= 0) {
                        var tempArray = result.split(',');
                        var tempNum = tempArray[1];
                        $("#HardwareCode").val(tempNum);
                    } else {
                        $("#HardwareCode").val(result);
                    }
                }
            });
        });
        //输入条形码
        $('#enterCode').click(function () {
            $.prompt({
                title: '请输入硬件编号',
                text: '例：TFA-10010001',
                //input: '输入框默认值',
                empty: false, // 是否允许为空
                onOK: function (input) {
                    //点击确认
                    $("#HardwareCode").val(input);
                },
                onCancel: function () {
                    //点击取消
                }
            });
        });

        $.ajax({
            type: "post",
            url: "/mp/activate/config",
            data: {"url": location.href.split('#')[0]},
            dataType: "json",
            success: function (data) {
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: data.appId, // 必填，公众号的唯一标识
                    timestamp: data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: data.nonceStr, // 必填，生成签名的随机串
                    signature: data.signature,// 必填，签名，见附录1
                    jsApiList: ["getLocation", "scanQRCode"] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
            }
        });

        $(".fixedbtm").click(function () {
            var taskId = $("#taskId").val();
            $.confirm("确认投放完成?", function () {
                wx.getLocation({
                    type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                    success: function (res) {
                        var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                        var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                        wx.getLocation({
                            type: 'gcj02', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                            success: function (res_mars) {
                                var latitude_mars = res_mars.latitude; // 纬度，浮点数，范围为90 ~ -90
                                var longitude_mars = res_mars.longitude; // 经度，浮点数，范围为180 ~ -180。
                                $.ajax({
                                    type: 'post',
                                    data: {
                                        "taskId": taskId,
                                        "latitude": latitude,
                                        "longitude": longitude,
                                        "latitude_mars": latitude_mars,
                                        "longitude_mars": longitude_mars
                                    },
                                    url: '/mp/mpTask/deploy/finish',
                                    async: false,
                                    dataType: 'json',
                                    cache: false,
                                    success: function (data) {
                                        if (data.type == 'error') {
                                            $.toast("保存失败", "text");
                                        }
                                        else {
                                            window.history.back();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }, function () {
            });
        })
    });

    //完成
    $(".finishBtn").click(function () {
        var taskId = $("#taskId").val();
        var hardwareCode = $('#HardwareCode').val();
        if (hardwareCode == '') {
            $.toast("请输入硬件编号", "text");
            return false;
        } else {
            $.confirm("确认投放完成?", function () {
                $.ajax({
                    type: 'post',
                    data: {"taskId": taskId, "hardwareCode": hardwareCode},
                    url: '/mp/mpTask/deploy/finishExtention',
                    async: false,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        if (data.type == 'error') {
                            $.toast(data.message, "text");
                        }
                        else {
                            window.history.back();
                        }
                    }
                });
            }, function () {
            });
        }
    })


</script>
<!----------------Common js end------------------>

</body>
</html>
