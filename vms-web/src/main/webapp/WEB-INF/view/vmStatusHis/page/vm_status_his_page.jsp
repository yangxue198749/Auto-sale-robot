<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty vmStatusHisList}">
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
		<c:forEach items="${vmStatusHisList}" var="vmStatusHis">
		<tr>
			<td class="wh5">
				<input type="checkbox" value="">
			</td>
			<td class="wh10">
				<span>${u:getInnerCodeById(vmStatusHis.vmId)}</span>
			</td>
			<td class="wh20">
				<span><fmt:formatDate value="${vmStatusHis.vmTime}" type="both" dateStyle="full"/></span>
			</td>
			<td class="wh15">
				<span>${u:getNodeName(vmStatusHis.vmId)}</span>
			</td>
			<td class="wh10">
				<span>
					<c:choose>
						<c:when test="${vmStatusHis.doorStatus == 1 }">开门</c:when>
						<c:when test="${vmStatusHis.doorStatus == 2 }">关门</c:when>
					</c:choose>
				</span>
			</td>
			<td class="wh10">
				<span>${u:getHisTempValue(vmStatusHis.tempValue)}</span>
			</td>
			<td class="wh10">
				${u:getVmStatus(vmStatusHis.vmStatus)}
			</td>
			<td class="wh20">
				<span>${u:vmStatusDetailStr(vmStatusHis.vmStatusDatail)}</span>
			</td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty vmStatusHisList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>