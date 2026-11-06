<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty giroList}">
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
				<c:forEach items="${giroList}" var="giro">
					<tr>
						<td class="wh15">
							<span>${giro.customerName}</span>
						</td>
						<td class="wh15">
							<span>${giro.customerAccount}</span>
						</td>
						<td class="wh10">
							<span>${giro.customerAccName}</span>
						</td>
						<td class="wh10">
							<span>${giro.month}</span>
						</td>
						<td class="wh10">
							<span>${giro.totle}</span>
						</td>
						<td class="wh10">
							<span>${giro.count}</span>
						</td>
						<td class="wh10">
							<span>详细</span>
						</td>
						<td class="wh10">
										<span></span>
						</td>
						<td class="wh10">
							<span></span>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty giroList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false"/>
</c:if>
