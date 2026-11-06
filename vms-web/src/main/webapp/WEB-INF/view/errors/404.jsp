<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<html>
<head>
    <title>异常处理页面</title>
    <link href="${path }/plus/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path }/dist/css/error.css" rel="stylesheet" type="text/css">
</head>
<body style="font-family:微软雅黑;">
	<div class="col-md-7 col-md-offset-2" style="margin-top:12%;">
		<div class="col-md-6"><img src="${path }/dist/images/404.jpg" style="max-width:230px; margin-top:6%; float:right;" class="img-responsive" alt=""></div>
		<div class="col-md-6">
			<h1 style="font-size:64px; font-weight:700; font-family:arial;">Error,<span class="text-danger">404</span></h1>
			<p>当您看到了这个页面的时候，说明您已经飞离了轨道...</p>
			<hr style="margin-top:10px; margin-bottom:10px;"/>
			<p>
				1. 非法的访问请求，请确保正确的请求URL；<br>
				2. 可联系我们在线客服进行问题反馈。
			</p>
			<p style="color:#ccc; margin-top:5%;">
				<a href="/"> 返回首页  </a> <b class="text-danger" id="time" style="margin-left:10%;">5</b> 秒后页面将自动跳转...
				<br>
			</p>
		</div>
	</div>
	<div class="container">
		
	</div>
</body>
<script>
function run(){
   var s = document.getElementById("time");
   if(s.innerHTML == 1){
	   window.location.href='/';
	   return false;
   }
   s.innerHTML = s.innerHTML - 1;
}
window.setInterval("run();",1000);
</script>
</html>