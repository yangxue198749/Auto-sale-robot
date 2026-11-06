<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>微信公众号支付参数如何填写？ | 贩售机管理系统</title>
    <!--依赖jQuery bootstrap-->
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
</head>

<body>

<div class="docHtmlGrid">
    <div class="headGroup">
        <h2>微信公众号支付参数如何填写？</h2>
    </div>
    <div class="midGroup">
        <p>若您已经注册了微信服务号并申请到了微信公众号支付，则可直接按照下列提示将相关参数配置到系统中。</p>
        <p>填写微信公众号支付参数的流程</p>
        <h4>一、确保是在微信公众平台申请的微信支付（点击登陆）</h4>
        <h4>二、微信公众号支付参数获取</h4>
        <div class="itemBox">
            <p><b>1、微信公众号 AppID 和应用密钥（AppSecret）</b></p>
            <p>登录微信公众平台（点击登陆），点击左侧底部的「开发 - 基本配置」，可查看到开发者ID（AppID） 和开发者密码（AppSecret）。</p>
            <div class="tips">注：应用密钥必须是点击<em>「完整显示」</em>后再复制，如未成为开发者需在此页面先成为开发者。</div>
            <p></p>

            <p><b>2、商户 ID（微信支付商户号）</b></p>
            <p>微信公众号支付申请完成之后，微信商户平台会给你的邮箱发通知邮件。如不知邮件是发到哪个邮箱的，请在微信公众平台的微信支付申请的页面，点击查看资料，「常用邮箱」就是微信商户平台发通知邮件的邮箱。</p>
            <p></p>

            <p><b>3、商户支付密钥（API 密钥）</b></p>
            <p>登陆微信商户平台（点击登陆）（账号密码在微信商户平台发来的邮件里，详见上一步）。</p>
            <p>点击「账户中心 - 账户设置 - API 安全」（第一次登陆会提示你安装操作证书，请先安装操作证书）。</p>
            <p>点击「设置密钥」（随机密码生成器），设置32位密钥。</p>
            <p></p>

            <p><b>4、微信客户端证书和微信客户端证书密钥</b></p>
            <p>在微信商户平台点击左侧的「账户设置 - API 安全」，点击「点击下载证书」。</p>
            <div class="tips">
                注：任何新增账号（包括管理员账户）都无法看到「下载证书」按钮，只有主体账户可以看到「下载证书」按钮。<br>
                证书下载后，打开压缩包会看到<em>「apiclient_cert.p12」</em>，将此文件上传到配置参数处。
            </div>
        </div>
    </div>
</div>

</body>
</html>
