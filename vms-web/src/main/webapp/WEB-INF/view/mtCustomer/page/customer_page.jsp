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
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="10">
			<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
		</td></tr>--%>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
				<c:forEach items="${nodeList }" var="customer">
					<tr>
						<td class="wh3"><input class="customerIds" name="customerIds" type="checkbox" value="${customer.id}"></td>
						<td class="wh10"><span>${customer.customerName}</span></td>
						<td class="wh6">
							<span>${u:getDictNameById(customer.customerMode)}</span>
						</td>
						<td class="wh6">
							<span>${u:getDictNameById(customer.customerType)}</span>
						</td>
						<td class="wh15"><span>${customer.customerAddress}</span></td>
						<td class="wh12">
							<p>
								<span>${customer.managerName}</span>
							</p>
							<p>
								<span>${customer.managerPhone}</span>
							</p>
							<p>
								<span>${customer.customerEmail}</span>
							</p>
						</td>
						<td class="wh12">
							<p>
								<span>${customer.collectionAccountType}</span>
							</p>
							<p>
								<b>${customer.collectionAccount}</b>
							</p>
						</td>
						<td class="wh12"><span>${customer.taxCode}</span></td>
						<td class="wh12">
							<p><span>${u:getUserById(customer.ctor).userName}</span></p>
							<p><span><fmt:formatDate value="${customer.ctime}" type="both"/></span></p>
						</td>
						<td class="wh12">
							<p><span>${u:getUserById(customer.utor).userName}</span></p>
							<p><span><fmt:formatDate value="${customer.utime}" type="both"/></span></p>
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