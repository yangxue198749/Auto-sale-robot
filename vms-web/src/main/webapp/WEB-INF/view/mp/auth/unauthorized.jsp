<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>抱歉，您没有被授权浏览此页面，如有需要请联系管理员。</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="抱歉，您没有被授权浏览此页面，如有需要请联系管理员。">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">

    <div class="unview">
        <section class="txtgroup">
            <p>抱歉，您没有被授权浏览此页面，如有需要请联系管理员。</p>
        </section>
        <a class="weui_btn weui_btn_default2" href="javascript:" id="closeWindow">返回</a>
    </div>

</div>


<!--watermark-->
<div class="watermark"></div>

</body>
</html>
<script>
    $(function() {
        //关闭
        $(document).on("click","#closeWindow",function(){
            WeixinJSBridge.call('closeWindow');
        });
    });
</script>
