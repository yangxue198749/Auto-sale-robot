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
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="11">
			<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
		</td></tr>--%>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
			<c:forEach items="${nodeList }" var="vmModel">
				<tr>
					<td class="wh3">
						<input name="vmModelCheckbox" type="checkbox" value="${vmModel.id}">
					</td>
					<td class="wh7">
						<span>${vmModel.modelName}</span>
					</td>
					<td class="wh7">
						<span>${u:getDictNameById(vmModel.modelType)}</span>
					</td>
					<td class="wh15">
						<span>${vmModel.modelDesc}</span>
					</td>
					<td class="wh8">
						<span>${u:getDictNameById(vmModel.channelType)}</span><!-- 货到类型 -->
					</td>
					<td class="wh7">
						<span>${vmModel.combinatorialPatternName}</span><!-- 组合模式 -->
					</td>
					<td class="wh8">
						<span>${vmModel.shortcutsQty}</span>
					</td>
					<td class="wh8">
						<span>${vmModel.channelLayerQty}</span>
					</td>
					<td class="wh8">
						<span>${vmModel.channelPerLayer}</span>
					</td>
					<td class="wh8">
						<button onclick="lookOtherInfo(this)" type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#lookOtherInfo"  name="${vmModel.id}">查看</button>
					</td>
					<td class="wh8">
						<p><span>${u:getUserById(vmModel.ctor).userName}</span></p>
						<p><span><fmt:formatDate value="${vmModel.ctime}" type="both"/></span></p>
					</td>
					<td class="wh8">
						<p><span>${u:getUserById(vmModel.utor).userName}</span></p>
						<p><span><fmt:formatDate value="${vmModel.utime}" type="both"/></span></p>
					</td>
					<td class="wh5">
						<span>${vmModel.deleteMark==0?"已经启用":"已经停用"}</span>
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