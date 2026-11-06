<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<a href="#" class="logowrap ver pull-left">VMS | 售货机管理系统</a>
<header class="pull-left mr20">
     <ul>
     <c:forEach items="${moduleList }" var="module">
     	<c:choose>
     		<c:when test="${moduleCode ==  module.elementCode}">
     		<li class="on"><a href="${module.elementUrl }">${module.elementName}</a></li>
     		</c:when>
     		<c:otherwise>
     		<li><a href="${module.elementUrl }">${module.elementName}</a></li>
     		</c:otherwise>
     	</c:choose>
     </c:forEach>
     </ul>
</header>