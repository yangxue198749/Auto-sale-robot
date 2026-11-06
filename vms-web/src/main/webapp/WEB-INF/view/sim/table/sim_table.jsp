<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty simList}">
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
				<c:forEach items="${simList}" var="sim">
					<tr>
						<td class="wh3">
							<input type="checkbox" value="${sim.id}">
						</td>
						<td class="wh15">
							<span>${sim.simPhone}</span>
						</td>
						<td class="wh20">
							<span>${sim.simNumber}</span>
						</td>
						<td class="wh12">
							<c:choose>
								<c:when test="${1 == sim.serviceProvider}">
									<span>移动</span>
								</c:when>
								<c:when test="${2 == sim.serviceProvider}">
									<span>联通</span>
								</c:when>
								<c:when test="${3 == sim.serviceProvider}">
									<span>电信</span>
								</c:when>
							</c:choose>
						</td>
						<td class="wh10">
							<c:choose>
								<c:when test="${empty sim.mtVm}">
									<span class="icon_state red">未使用</span>
								</c:when>
								<c:otherwise>
									<span class="icon_state green">使用中</span>
								</c:otherwise>
							</c:choose>
						</td>
						<td class="wh20">
							<p>
								<span>${u:getUserById(sim.ctor).userName}</span>
							</p>
							<p>
								<span><fmt:formatDate value="${sim.ctime}" type="both"/></span>
							</p>
						</td>
						<td class="wh20">
							<p>
								<span>${u:getUserById(sim.utor).userName}</span>
							</p>
							<p>
								<span><fmt:formatDate value="${sim.utime}" type="both"/></span>
							</p>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty simList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
