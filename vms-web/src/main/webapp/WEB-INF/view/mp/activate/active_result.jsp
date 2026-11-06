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

    <div class="article_box">
        <article class="weui_article">
            <section>
                <h3>请观察售货机右下角是否出现刚选择的售货机编号：</h3>
                <section class="cf_red f14">
                    <p>1，如果出现编号，则激活成功；</p>
                    <p>2，如果未出现编号，请尝试重启售货机重新激活；</p>
                    <p>3，如果仍未出现编号，请联系运维主管汇报情况。</p>
                </section>
            </section>
        </article>
        <input type="hidden" value="${vmId}" id="vmId">
        <!--button-->
        <div class="weui_btn_area">
            <a class="weui_btn weui_btn_default2" href="javascript:" id="closeWindow">返回</a>
        </div>
    </div>

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>

    function up_mars_position() {
        var vmId = $("#vmId").val();
        wx.getLocation({
            type: 'gcj02', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function (res) {
                var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                var speed = res.speed; // 速度，以米/每秒计
                var accuracy = res.accuracy; // 位置精度
                $.ajax({
                    url: "/mp/activate/mars",
                    data:{"vmId":vmId,"latitude":latitude,"longitude":longitude},
                    type: "POST",
                    success: function (data) {
                    }
                });
            }
        });
    }

    function up_earth_position() {
        var vmId = $("#vmId").val();
        wx.getLocation({
            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function (res) {
                var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                var speed = res.speed; // 速度，以米/每秒计
                var accuracy = res.accuracy; // 位置精度
                $.ajax({
                    url: "/mp/activate/earth",
                    data:{"vmId":vmId,"latitude":latitude,"longitude":longitude},
                    type: "POST",
                    success: function (data) {
                        up_mars_position();
                    }
                });
            }
        });
    }





    $(function() {
        FastClick.attach(document.body);
        //关闭
        $(document).on("click","#closeWindow",function(){
            WeixinJSBridge.call('closeWindow');
        });

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
                    jsApiList: ["getLocation"] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
            }
        });
        wx.ready(function(){
            // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
            up_earth_position()
        });


    });
</script>
<!----------------Common js end------------------>

</body>
</html>
