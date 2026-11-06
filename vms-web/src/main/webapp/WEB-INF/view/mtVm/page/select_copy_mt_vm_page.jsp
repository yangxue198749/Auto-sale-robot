<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty mtVmList }">
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
				<td class="wh30">售货机编号</td>
				<td class="wh40">点位</td>
				<td class="wh20">售货机型号</td>
			 </tr>
		 </thead>
		 <tbody class="auto">
			<c:forEach items="${mtVmList }" var="mtVm">
				 <tr>
					<td class="wh10"><input type="checkbox" innerCode="${mtVm.innerCode }" value="${mtVm.id }"></td>
					<td class="wh30">${mtVm.innerCode }</td>
					<td class="wh40">${mtVm.node.nodeName }</td>
					<td class="wh20">${u:getModelName(mtVm.vmModelId)}</td>
				 </tr>
			</c:forEach>
		 </tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  mtVmList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" id="copyMtVmDiv" setPage="false" />
</c:if>