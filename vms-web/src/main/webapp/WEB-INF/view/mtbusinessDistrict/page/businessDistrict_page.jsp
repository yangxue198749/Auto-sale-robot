<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty nodeList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="6">
			<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
		</td></tr>--%>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
				<c:forEach items="${nodeList }" var="businessDistrict">
					<tr>
	                    <td class="wh5"><input type="checkbox" value="${businessDistrict.id}"></td>
	                    <td class="wh20">
	                        <span>${businessDistrict.businessDistrictName}</span>
	                    </td>
	                    <td class="wh20">
	                        <span>${u:getUserById(businessDistrict.ctor).userName}</span>
	                    </td>
	                    <td class="wh20">
	                        <span><fmt:formatDate value="${businessDistrict.ctime}" type="both"/></span>
	                    </td>
	                    <td class="wh20">
	                        <span>${u:getUserById(businessDistrict.utor).userName}</span>
	                    </td>
	                    <td class="wh15">
	                        <span><fmt:formatDate value="${businessDistrict.utime}" type="both"/></span>
	                    </td>
	                </tr>
				</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }"
		pageNo="${pageNo }" url="/${url }" setPage="true" />
</c:if>
