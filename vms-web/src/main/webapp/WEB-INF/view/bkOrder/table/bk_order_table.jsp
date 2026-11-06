<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<c:choose>
    <c:when test="${empty subList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="14">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <tbody class="auto">
        <c:forEach items="${subList}" var="order">
            <tr>
                <td class="wh7">
                    <span>${order.nodeName}</span>
                </td>
                <td class="wh7">
                    <span>${order.innerCode}</span>
                </td>
                <td class="wh8">
                    <span>${order.skuName}</span>
                </td>
                <td class="wh8">
                    <p>商品:<b class="cf_green">${order.goodPrice}</b></p>
                    <p>押金:<b class="cf_red">${order.depositPrice}</b></p>
                </td>
                <td class="wh7">
                    <span>${order.validDay}</span>
                </td>
                <td class="wh8">
                    <span>${order.vendOutTime}</span>
                </td>
                <td class="wh5">
                    <span>${order.vendOutStatus}</span>
                </td>
                <td class="wh8 cf_red">
                    <span>${order.failReason}</span>
                </td>
                <td class="wh5">
                    <span>${order.orderStatus}</span>
                </td>
                <td class="wh8">
                    <span>${order.refundGoodStatus}</span>
                    <c:if test="${u:isPermitted('bk-order-good-refund')}">
                        ${order.refundGoodButton}
                    </c:if>
                </td>
                <td class="wh7">
                    <span>${order.refundDepositStatus}</span>
                    <c:if test="${u:isPermitted('bk-order-deposit-refund')}">
                        ${order.refundDepositButton}
                    </c:if>
                </td>
                <td class="wh5">
                    <span>${order.channelCode}</span>
                </td>
                <td class="wh10">
                    <span>${order.seq}</span>
                </td>
                <td class="wh7">
                    <span>${order.phone}</span>
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
