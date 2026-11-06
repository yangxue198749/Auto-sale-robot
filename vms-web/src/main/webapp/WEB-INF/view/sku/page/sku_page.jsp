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
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
				<c:forEach items="${nodeList }" var="sku">
					<tr>
	                    <td class="wh3">
	                        <input type="checkbox" value="${sku.id}">
	                    </td>
	                    <td class="wh8">
	                        <img src="${sku.imgUrl}" style="width: 40px;height: 71px">
	                    </td>
	                    <td class="wh7">
	                        <span>${sku.skuName}</span>
	                    </td>
	                    <td class="wh5">
	                        <span>${sku.skuUnit}</span>
	                    </td>
						<td class="wh5">
							<span>${sku.id}</span>
						</td>
	                    <td class="wh10">
	                        <span>${sku.offerPrice}</span>
	                    </td>
	                    <td class="wh5">
	                        <span>${sku.skuType.parent.typeName}</span>
	                    </td>
	                    <td class="wh5">
	                        <span>${sku.skuType.typeName}</span>
	                    </td>
	                    <td class="wh8">
	                        <p><span>厚:${sku.skuLength}</span></p>
	                        <p><span>宽:${sku.skuWidth}</span></p>
	                        <p><span>高:${sku.skuHeight}</span></p>
	                    </td>
	                    <td class="wh10">
	                        <span>${sku.skuDesc}</span>
	                    </td>
	                    <td class="wh7">
	                        <span>${sku.saleArea}</span>
	                    </td>
	                    <td class="wh7">
	                        <span onclick="lookOther(this)" class="btn btn-success btn-sm" data-toggle="modal" data-target="#checkInfo" name="${sku.id}" >查看</span>
	                    </td>
	                    <td class="wh10">
	                        <p><span>${u:getUserById(sku.ctor).userName}</span></p>
							<p><span><fmt:formatDate value="${sku.ctime}" type="both"/></span></p>
	                    </td>
	                    <td class="wh10">
	                        <p><span>${u:getUserById(sku.utor).userName}</span></p>
							<p><span><fmt:formatDate value="${sku.utime}" type="both"/></span></p>
	                    </td>
	                </tr>
				</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }"
		pageNo="${pageNo }" url="${path}/${url }" setPage="true" />
</c:if>