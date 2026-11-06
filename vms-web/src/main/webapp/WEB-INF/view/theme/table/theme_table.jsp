<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty themeList}">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
		<c:forEach items="${themeList}" var="theme">
			<tr>
				<td class="wh3">
					<input type="checkbox" value="${theme.id}">
				</td>
				<td class="wh20">
					<span>${theme.themeName}</span>
				</td>
				<td class="wh57">
					<span>${theme.themeDesc}</span>
				</td>
				<td class="wh10">
					<button type="button" href="${path}/theme/preview?themeId=${theme.id}" class="btn btn-success btn-sm" data-toggle="modal" data-target="#viewTheme">查看</button>
				</td>
				<td class="wh10">
					<p><span>${u:getUserById(theme.ctor).userName}</span></p>
					<p><span><fmt:formatDate value="${theme.ctime}" type="both"/></span></p>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty themeList}">
	<t:pager totalSize="${totalSize}" pageSize="${pageSize}" pageNo="${pageNo}" url="${url}" setPage="true"/>
</c:if>
