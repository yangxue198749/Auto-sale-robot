<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>支付宝手机网站的支付参数如何填写？ | 贩售机管理系统</title>
    <!--依赖jQuery bootstrap-->
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
</head>

<body>

<div class="docHtmlGrid">
    <div class="headGroup">
        <h2>支付宝手机网站的支付参数如何填写？</h2>
    </div>
    <div class="midGroup">
        <p>若您已经自行注册了支付宝企业账号并申请到了「手机网站支付」，则可直接按照下列提示将相关参数配置到系统中。</p>
        <div class="itemBox">
            <p><b>1. 获取 PID 与 MD5 密钥</b></p>
            <p>登陆支付宝开放平台，点击登陆，点击右上角头像，进入「账户信息」。</p>
            <p>进入左侧的「mapi 网关产品密钥」，复制上方的 PID ，分别填写至「合作者身份（PID）」。</p>
            <p></p>

            <p><b>2. 获取支付宝公钥及AppID</b></p>
            <p>说明：当密钥中已有公钥时，请先确认是否已在使用中，不要随意覆盖公钥，以免影响使用中的支付渠道；</p>
            <p>进入左侧的「开放平台密钥」，找到添加了「已生效」功能的应用，将 AppID 填写至「支付宝 AppID」。</p>
            <p></p>
            <div class="tips">
                注：若存在多个应用，请务必确认哪个应用添加了<em>「已生效」</em>的功能，并填写该应用的AppID，将应用私钥和支付宝公钥上传至系统中。
            </div>
        </div>
    </div>
</div>

</body>
</html>
