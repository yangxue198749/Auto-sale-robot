<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>绑定账号</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="绑定账号">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">

    <div class="bind_account" id="BindAccount">
        <section class="accountbox">
            <div class="bindwrap">
                <section>
                    <input class="weui_input f14 userAccount" type="text" name="username" placeholder="请输入魔核账号">
                </section>
                <section>
                    <input class="weui_input f14 userPassword" type="password" name="password" placeholder="请输入密码">
                </section>
            </div>
            <a class="weui_btn weui_btn_primary submitbtn" href="javascript:">绑定微信账号</a>
        </section>
    </div>

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<!----------------Common js end------------------>

<!---AutoMat chioceCode 交互js start--->
<script type="text/javascript" src="${path}/mp-static/js/BindAccount.js"></script>
<!---AutoMat chioceCode 交互js end--->

</body>
</html>
