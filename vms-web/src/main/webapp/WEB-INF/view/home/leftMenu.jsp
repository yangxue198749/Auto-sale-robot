<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:forEach items="${littleModuleList }" var="littleModule">
	 <dl>
		<dt><i class="iconfont mt0"></i>${littleModule.elementName }<i class="iconfont fr_arr icon-xiala"></i></dt>
	     <dd>
	     	<c:forEach items="${menuList }" var="menu">
		     	<c:if test="${menu.parentElement.eleMentId == littleModule.eleMentId }">
		     		<c:choose>
		     			<c:when test="${menuCode == menu.elementCode }">
				         	<a href="${menu.elementUrl }" class="on">${menu.elementName }</a>
		     			</c:when>
		     			<c:otherwise>
		         			<a href="${menu.elementUrl }">${menu.elementName }</a>
		     			</c:otherwise>
		     		</c:choose>
		     	</c:if>
	     	</c:forEach>
	     </dd>
	 </dl>
</c:forEach>
<div class="switchMenu" id="switchMenu">switch</div>