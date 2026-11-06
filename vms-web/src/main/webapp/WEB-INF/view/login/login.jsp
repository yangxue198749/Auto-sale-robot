<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>vms售货机管理系统</title>
    <link rel="stylesheet" href="/dist/css/login.css">
    <link rel="stylesheet" href="/dist/css/vms.mp-3.01.css">
    <script type="text/javascript" src="/plus/jQuery/jquery-2.2.3.min.js"></script>
	<script type="text/javascript" src="/plus/layer/layer.js"></script>
    <script type="text/javascript" src="/dist/js/login.js"></script>
	<link rel="shortcut icon" type="image/x-icon" href="/dist/images/favicon.ico" />
</head>
<body>
<!--[if lt IE 9]>
<div class="goodbye_group">
    <div class="gbwrap">
        <div class="gbbox">
            <h2>为了提供更好的服务，请升级您的浏览器</h2>
            <p>尊敬的用户，您现在使用的浏览器版本过低，请升级后使用VMS相关服务。</p>
            <h4>您可以选择以下浏览器：</h4>
            <ul>
                <li><a class="chrome" target="_blank" href="http://www.google.cn/chrome/browser/desktop/index.html">谷歌浏览器(推荐)</a></li>
                <li><a class="flash-360" target="_blank" href="http://se.360.cn/">360安全浏览器</a></li>
            </ul>
        </div>
    </div>
</div>
<![endif]-->
<div class="vms_loginwp">
    <div class="topgroup">
        <div class="navwrap">
            <div class="logo"><a href="#"><img src="dist/images/tf_logo_new.png" alt=""></a>触见未来</div>
            <div class="rtwrap"><span class="bl0"><a href="http://www.touchfound.com/" target="_blank">触创官网</a></span><span><a href="#">返回首页</a></span></div>
        </div>
    </div>
    <div class="midgroup">
        <div class="lfwrap" id="touchGroup">
            <div class="bannerbox" id="banner">
                <i class="qp_txt"></i>
                <i class="qp_automat"></i>
            </div>
        </div>
        <div class="rtwrap">
            <div class="logingroup" id="vmsLogin">
                <header>
                    <h2>VMS售货机管理系统</h2>
                </header>
                <form action="">
                    <section>
                        <input type="text" name="account" placeholder="请输入账号" class="input_block login_account">
                    </section>
                    <section>
                        <input type="password" name="password" placeholder="请输入密码" class="input_block login_password">
                    </section>
                    <section class="qrcode">
                        <input type="text" placeholder="请输入验证码" class="input_block login_code"><img src="" id="qrcode" class="qrcode" alt="">
                    </section>
                    <a href="javascript:;"  class="login_btn loginbtn">登录</a>
                </form>
                <footer>
                    <p>生活触手可及 用户智能互动网络</p>
                </footer>
            </div>
        </div>
    </div>
    <div class="btmgroup" id="BtmGroup">
        <div class="btmwrap">
            <p>Copyright © 2017, Beijing New Internet Converge Online Information Technology Co., LTD. All Rights Reserved.</p>
            <p>© 北京新网聚联信息科技有限公司 版权所有. 京ICP备16038003号</p>
        </div>
    </div>
</div>

</body>
	<script type="text/javascript">
		$(function(){
	   		//处理回车
			 $(window).keydown(function(event) {
			    switch (event.keyCode) {
			    case 13:
			    	RegLogin();
			      break;
			    default:
			      break;
			    }
			  });
		})
	</script>
</html>