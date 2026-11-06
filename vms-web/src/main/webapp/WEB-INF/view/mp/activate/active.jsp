<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>激活售货机</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="激活售货机">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
</head>
<body>
<div class="mh_container">
    <!--售货机编号  完整结果
    <div class="weui_cells_title">售货机编号</div>
    <div class="weui_cells weui_cells_access">
        <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_hd"></div>
          <div class="weui_cell_bd weui_cell_primary">
            <p><b class="f16">02500001</b></p>
            <p class="f12 cf9">金鹰国际购物中心（新街口）</p>
          </div>
          <div class="weui_cell_ft"></div>
        </a>
    </div>
    -->
    <!--售货机编号-->
    <div class="weui_cells_title">售货机编号</div>
    <div class="weui_cells weui_cells_access">
        <a class="weui_cell" href="javascript:;" id="chioceCode">
            <div class="weui_cell_hd"></div>
            <div class="weui_cell_bd weui_cell_primary codeplace">
                <p class="cf9 f14">请选择售货机编号</p>
            </div>
            <div class="weui_cell_ft"></div>
        </a>
    </div>
    <!--硬件编号-->
    <div class="weui_cells_title">硬件编号</div>
    <div class="weui_cells weui_cells_access">
        <a class="weui_cell" href="javascript:;">
            <div class="weui_cell_hd"></div>
            <div class="weui_cell_bd weui_cell_primary bind_scan">
                <input class="weui_input f14 readonly" type="text" placeholder="使用扫一扫，添加硬件编号" readonly <%--placeholder="请输入硬件编号"--%> id="HardwareCode">
                <input class="weui_input f14" type="hidden" id="uniqueCode" value="${uniqueCode}">
                <aside class="touchscan" id="scanQRCodeBtn"></aside>
            </div>
        </a>
        <div class="weui_cell">
            <p class="f12">请扫描贩售机门体内侧的条形码，会自动填入硬件编号。如果门内没有条形码，<span class="cf_blue" id="enterCode">请点此输入</span></p>
        </div>
    </div>
    <!--button-->
    <div class="weui_btn_area">
        <a class="weui_btn weui_btn_primary" href="javascript:" id="activAutoMat_btn">激活售货机</a>
    </div>

    <!--选择编号 AutoMatlist start-->
    <div class="AutoMatlist hide" id="AutoMatlist">
        <div class="weui_cells_title">选择编号</div>
        <div class="weui_cells weui_cells_radio">
            <c:forEach items="${tasks}" var="task">
                <label class="weui_cell weui_check_label">
                    <div class="weui_cell_bd weui_cell_primary">
                        <p><b class="f16 child_code">${task.mtVm.innerCode}</b></p>
                        <p class="f12 cf9">点位：<span class="cf6 child_location">${task.mtVm.node.nodeName}</span></p>
                        <p class="f12 cf9">位置：<span class="cf6">${task.mtVm.vmDesc}</span></p>
                        <input type="hidden" value="${task.mtVm.id}" class="vmId">
                        <input type="hidden" value="${task.taskVmId}" class="taskId">
                    </div>
                    <div class="weui_cell_ft">
                        <input type="radio" class="weui_check" name="AutoMatCode">
                        <span class="weui_icon_checked"></span>
                    </div>
                </label>
            </c:forEach>

        </div>
        <!--button-->
        <div class="weui_btn_area">
            <a class="weui_btn weui_btn_primary AutoMatlist_Submit" href="javascript:">确定</a>
        </div>
    </div>
    <!--选择编号 AutoMatlist end-->


</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        FastClick.attach(document.body);
        //配置JS-SDK
        $.ajax({
            type:"post",
            url:"/mp/activate/config",
            data:{"url":location.href.split('#')[0]},
            dataType:"json",
            success:function(data){
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: data.appId, // 必填，公众号的唯一标识
                    timestamp: data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: data.nonceStr, // 必填，生成签名的随机串
                    signature: data.signature,// 必填，签名，见附录1
                    jsApiList: [//API配置列表
                        "scanQRCode"
                        /*"onMenuShareTimeline",
                         "onMenuShareAppMessage",
                         "onMenuShareQQ",
                         "onMenuShareWeibo",
                         "hideOptionMenu",
                         "showOptionMenu",
                         "closeWindow",
                         "hideMenuItems",
                         "showMenuItems",
                         "hideAllNonBaseMenuItem",
                         "showAllNonBaseMenuItem"*/] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
            }
        });

        //执行扫描动作scanQRCode
        $('#scanQRCodeBtn').click(function(){
            wx.scanQRCode({
                needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                success: function (res) {
                    var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                   // console.log(result);
//                    $("#HardwareCode").val(result);
                    if(result.indexOf(",")>=0){
                        var tempArray = result.split(',');
                        var tempNum = tempArray[1];
                        $("#HardwareCode").val(tempNum);
                    }else{
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
                onOK: function (input){
                    //点击确认
                    $("#HardwareCode").val(input);
                },
                onCancel: function () {
                    //点击取消
                }
            });
        });

    });
</script>
<!----------------Common js end------------------>

<!---AutoMat chioceCode 交互js start--->
<script type="text/javascript" src="${path}/mp-static/js/AutoMat_chioceCode.js"></script>
<!---AutoMat chioceCode 交互js end--->

</body>
</html>
