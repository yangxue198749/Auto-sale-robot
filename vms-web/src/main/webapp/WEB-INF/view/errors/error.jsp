<%@page import="com.kdx.vms.commons.exception.RemoteServerException"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>


<%@page import="org.apache.shiro.authz.AuthorizationException"%>
<html>
<head>
    <title>异常处理页面</title>
    <link href="${path }/plus/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path }/dist/css/error.css" rel="stylesheet" type="text/css">
    <style>
       body{font-family:微软雅黑;}
    </style>
</head>
<body class="page-500-full-page">
 <% 
	Object ex = request.getAttribute("ex"); 
	String errorCode = "", errorMsg = "";
	if(ex instanceof RemoteServerException ) {
		RemoteServerException serverException = (RemoteServerException) ex;
		errorCode = serverException.getCode();
		errorMsg = serverException.getMessage();
	}else if(ex instanceof AuthorizationException ) {
		AuthorizationException authorizationException = (AuthorizationException) ex;
		errorCode = "error:user-no-permission";
		errorMsg = "不好意思，您没有该页面的访问权限！";
	} else {
		Exception exception = (Exception) ex;
		errorCode = "error:system";
		errorMsg = exception.getMessage();
	}
 %>
<div class="row">
	<div class="col-md-12 page-500">
		<div class="number text-danger">
			 500
		</div>
		<div class="details">
			<h3 style="font-family:微软雅黑; font-size:48px; font-weight:800;">对不起，出错啦！</h3>
			<h4 style="font-size:30px; font-family:arial;">SORRY AN ERROR HAS OCCURED!</h4>
			  错误编码： <%=errorCode %> <br/>
			  错误详情： <%=errorMsg %>
			<hr style="margin-top:10px; margin-bottom:10px;"/>
			<p style="font-family:微软雅黑;" class="text-danger">
				1. 非法的访问请求，请确保正确的请求URL； <br/>
				2. 系统繁忙，请稍侯再试； <br/>
				3. 可通过支持中心进行问题反馈。   <br/>
			</p>
			<p style="font-family:微软雅黑;"><a href="#">&lt;&lt;联系我们</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:history.go(-1);">&lt;&lt;返回首页</a></p>
		</div>
	</div>
</div>
</body>
</html>