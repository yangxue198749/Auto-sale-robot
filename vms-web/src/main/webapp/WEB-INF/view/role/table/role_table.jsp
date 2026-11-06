<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty roleList}">
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
				<c:forEach items="${roleList}" var="role">
					<tr>
						<td class="wh3">
							<input type="checkbox" value="${role.id}">
						</td>
						<td class="wh22">
							<span>${role.roleName}</span>
						</td>
						<td class="wh20">
							<c:choose>
								<c:when test="${1 == role.roleType}">
									<span class="icon_state2 flesh" >系统内部</span>
								</c:when>
								<c:when test="${2 == role.roleType}">
									<span class="icon_state2">系统预设</span>
								</c:when>
								<c:when test="${3 == role.roleType}">
									<span class="icon_state2 orange" >用户创建</span>
								</c:when>
							</c:choose>
						</td>
						<td class="wh25">
							<span>${role.roleDesc}</span>
						</td>
						<td class="wh15">
							<p><span>${u:getUserById(role.ctor).userName}</span></p>
							<p><span><fmt:formatDate value="${role.ctime}" type="both"/></span></p>
							<%--<p><span><fmt:formatDate value="${role.ctime}" type="both" dateStyle="full"/></span></p>--%>
						</td>
						<td class="wh15">
							<p><span>${u:getUserById(role.utor).userName}</span></p>
							<p><span><fmt:formatDate value="${role.utime}" type="both"/></span></p>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty roleList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
