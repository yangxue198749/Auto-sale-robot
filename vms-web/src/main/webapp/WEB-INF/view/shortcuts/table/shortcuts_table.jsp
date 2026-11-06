<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty strategyList}">
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
			<c:forEach items="${strategyList}" var="strategy">
			<tr>
				<td class="wh3"><input type="checkbox" value="${strategy.strategyId}"></td>
				<td class="wh10"><span>${strategy.strategyName}</span></td>
				<td class="wh15"><span>${strategy.strategyDesc}</span></td>
				<td class="wh10"><span>智能贩售机</span></td>
				<td class="wh10"><span>${strategy.vmModel.modelName}</span></td>
				<td class="wh10"><span>${strategy.vmModel.shortcutsQty}</span></td>
				<td class="wh12"><button type="button" class="btn btn-success f12 btn-sm" href="${path}/stShortcuts/view?strategyId=${strategy.strategyId}" data-toggle="modal" data-target="#edit-shortcuts-div">查看</button></td>
				<td class="wh15">
					<p><span>${u:getUserById(strategy.ctor).userName}</span></p>
					<p><span><fmt:formatDate value="${strategy.ctime}" type="both"/></span></p>
				</td>
				<td class="wh15">
					<p><span>${u:getUserById(strategy.utor).userName}</span></p>
					<p><span><fmt:formatDate value="${strategy.utime}" type="both"/></span></p>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty strategyList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
