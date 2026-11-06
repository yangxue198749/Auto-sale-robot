<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>重置密码 | 贩售机管理系统 3.0</title>
    <!--依赖jQuery bootstrap-->
    <link rel="stylesheet" href="./plus/bootstrap/css/bootstrap.min.css">
    <script type="text/javascript" src="./plus/jQuery/jquery-2.2.3.min.js"></script>
    <script type="text/javascript" src="./plus/bootstrap/js/bootstrap.min.js"></script>
    <!--layer 扩展插件-->
    <script type="text/javascript" src="./plus/layer/layer.js"></script>
    <!--validate 扩展插件-->
    <script type="text/javascript" src="./plus/validate/jquery.validate.min.js"></script>
    <script type="text/javascript" src="./plus/validate/messages_zh.min.js"></script>
    <script type="text/javascript" src="./dist/js/function.validate.js"></script>
    <!--全站样式-->
    <link rel="stylesheet" type="text/css" href="./dist/css/vms.mp-3.01.css"/>
	<script type="text/javascript" src="/dist/js/login.js"></script>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>

<body style="min-width:1200px;">
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
<div class="restgroup">
    <header>
        <h2>首次登录设置</h2>
        <p>Tips:为了您的账号及相关转账业务的安全，请您设置以下内容。</p>
    </header>
    <div class="restbox">
        <form id="RestForm">
			<input hidden="hidden" name="needSetGiroAccount" value="${needSetGiroAccount}" />
	        <section class="col-md-12">
	            <p><span>用户名:</span></p>
	            <div class="form-group"><input class="form-control disabled" type="text" value="${user.userName}" readonly disabled="disabled"></div>
	        </section>
	        <section class="col-md-6">
	            <p><span for="newpassword">新密码:</span></p>
	            <div class="form-group"><input class="form-control" type="password" id="newpassword" name="newpassword" placeholder="请输入新密码" required data-msg-required="请输入新密码" data-rule-pda9="true"></div>
	        </section>
	        <section class="col-md-6">
	            <p><span for="pwd">确认密码:</span></p>
	            <div class="form-group"><input class="form-control" type="password" name="pwd" placeholder="请输入确认密码" required data-msg-required="请输入确认密码" data-rule-pda9="true" equalTo="#newpassword" data-msg-equalTo="两次密码不一致"></div>
	        </section>
	        
	        <c:if test="${needSetGiroAccount}">
		        <div class="clearfix"></div>
		        <section class="linehead">
		            <h4>请设置转账账号</h4>
		        </section>
		        <section class="col-md-6">
		            <p><span for="giroAccountName">真实姓名:</span></p>
		            <div class="form-group"><input class="form-control" type="text" name="giroAccountName" placeholder="请输入您的真实姓名" required data-msg-required="请输入您的真实姓名" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true"></div>
		        </section>
		        <section class="col-md-6">
		            <p><span for="giroAccountType">收款渠道:</span></p>
		            <div class="form-group">
		                <select class="form-control" name="giroAccountType">
		                  <c:forEach items="${giroAccountType.dictionary}" var="tSysDictionary">
                      	  	<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                          </c:forEach>
		                </select>
		            </div>
		        </section>
		        <section class="col-md-12">
		            <p><span for="payaccount">收款账号:</span></p>
					<div class="form-group"><input class="form-control" type="text" name="giroAccount" placeholder="请输入您的收款账号" required data-msg-required="请输入您的收款账号" minlength="2" data-msg-minlength="最少输入2个字符" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')"></div>
				</section>
		        <section class="col-md-6">
		            <p><span>手机号码:</span></p>
		            <div class="form-group"><input class="form-control" type="text" value="${phone}" readonly disabled="disabled"></div>
		        </section>
		        <section class="col-md-6">
		            <p><span for="codenumber">验证码:</span></p>
		            <div class="form-group qrcode_wp">
		            	<input class="form-control" name="codeNumber" type="text" placeholder="请输入验证码" required data-msg-required="请输入验证码">
		            	<a id="codeBtn" href="javascript:;" class="codebtn" onclick="getSMSCodeNumber()">获取验证码</a>
		            </div>
		        </section>
		    </c:if>    
		    
	        <section class="col-md-12 mt15">
	        	<button type="submit" class="btn btn-success btn-block">保存</button>
	        </section>
	        <div class="clearfix"></div>
        </form>
		<script>
        $(function(){
            //重置设置demo
            $("#RestForm").validate({
                submitHandler: function() {
                    //form.submit();//提交表单
                    //验证通过后 的js代码写在这里
                    restUserPwd();
                }
            });
        });
        </script>
    </div>
</div>




<%-- 	<div class="reset_box">
	<div class="reset_title">重置密码</div>
              <form action="">
                  <div class="reset_con">
                      <p>用户名:<span class="name">${user.userName}</span></p>
                      <label>
                          <span>新密码:</span><input class="input_first" type="password" placeholder="请输入新密码">
                      </label>
                      <label>
                          <span>确认密码:</span><input id="pwd" type="password" placeholder="请输入确认密码">
                      </label>
                  </div>
                  <div class="reset_con" style="border-top:1px solid #ddd;">
                      <p style="color:#f30; margin-bottom:15px;">请设置转账账号</p>
                      <label>
                          <span>收款渠道:</span><select><option>支付宝</option></select>
                      </label>
                      <label>
                          <span>账号:</span><input type="text" placeholder="请输入支付宝账号">
                      </label>
                      <label>
                          <span>真实姓名:</span><input type="text" placeholder="请输入您的真实姓名">
                      </label>
                      <p>手机号码:<span>${user.userPhone}</span></p>
                      <label>
                          <span>验证码:</span><input type="text" placeholder="请输入验证码"><input type="button" value="发送验证码">
                      </label>
                      <p>所属公司:<span>${user.company.companyName}</span></p>
                  </div>
                  <div class="bottom">
                      <a href="javascript:;" id="SaveNewpwd" class="save">保存</a>
                  </div>
              </form>


    </div> --%>
</body>
</html>
