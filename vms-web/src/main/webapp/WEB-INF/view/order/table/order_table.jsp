<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!-- 20171222 09:56 -->
<c:choose>
    <c:when test="${empty subList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="17">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <tbody class="auto">
        <c:forEach items="${subList}" var="order">
            <tr>
                <td class="wh5">
                    <span>${order.nodeName}</span>
                </td>
                <td class="wh5">
                    <span>${order.innerCode}</span>
                </td>
                <td class="wh5">
                    <span>${order.detailId}</span>
                </td>
                <td class="wh10">
                    <span class="${order.color}">${order.orderSeq}</span>
                </td>
                <td class="wh10">
                    <span>${order.outerSeq}</span>
                </td>
                <td class="wh5">
                    <span>${order.orderType}</span>
                </td>
                <td class="wh5">
                    <span>${order.skuId}</span>
                </td>
                <td class="wh10">
                    <span>${order.skuName}</span>
                </td>
                <td class="wh5">
                    <span>${order.payType}</span>
                </td>
                <td class="wh5">
                    <span>${order.skuAmount}</span>
                </td>
                <td class="wh5">
                    <span>${order.payAmount}</span>
                </td>
                <td class="wh5">
                    <span>${order.cardAmount}</span>
                </td>
                <td class="wh5">
                    <span>${order.orderTime}</span>
                </td>
                <td class="wh5">
                    <span>${order.validTime}</span>
                </td>
                <td class="wh5">
                    <span>${order.orderStatus}</span>
                </td>
                <td class="wh5">
                    <span>${order.outStatus}</span>
                </td>
                <td class="wh5">
                    <span>${order.refundStatus}</span>
                    <c:if test="${u:isPermitted('order-refund')}">
                        ${order.refundButton}
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
