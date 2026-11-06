<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty mtNodeList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
	</c:when>
	<c:otherwise>
	<table>
		 <thead>
			 <tr>
				<td class="wh10"></td>
				<td class="wh20">点位</td>
				<td class="wh25">区域</td>
				<td class="wh30">地址</td>
			 </tr>
		 </thead>
		 <tbody class="auto">
			<c:forEach items="${mtNodeList }" var="node">
			 <tr>
				<td class="wh10"><input type="checkbox" selectNodeName="${node.nodeName }" value="${node.id }"></td>
				<td class="wh20">${node.nodeName }</td>
				<td class="wh25">${u:getAreaName(node.provinceId)}${u:getAreaName(node.cityId)}${u:getAreaName(node.areaId)}</td>
				<td class="wh30">${node.nodeAddress }</td>
			 </tr>
			</c:forEach>
		 </tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  mtNodeList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" id="nodeDiv" url="${url }" setPage="false" />
</c:if>