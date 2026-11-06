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
    </c:when>
    <c:otherwise>
    <table>
        <tbody class="auto">
        <c:forEach items="${subList}" var="order">
            <tr>
                <td class="wh10">
                    <span>${order.nodeName}</span>
                </td>
                <td class="wh7">
                    <span>${order.innerCode}</span>
                </td>
                <td class="wh5">
                    <span>${order.skuName}</span>
                </td>
                <td class="wh5">
                    <b class="cf_green">${order.goodPrice}</b>
                </td>
                <td class="wh10">
                    <span class="icon_state green">${order.payType}</span>
                </td>
                <td class="wh5">
                    <span>${order.saleType}</span>
                </td>
                <td class="wh5">
                    <span>${order.costTime}</span>
                </td>
                <td class="wh5">
                    <span>${order.outTime}</span>
                </td>
                <td class="wh5">
                    <span>${order.outStatus}</span>
                </td>
                <td class="wh8 cf_red">
                    <span>${order.failReason}</span>
                </td>
                <td class="wh5">
                    <span>${order.refundGoodStatus}</span>
                    <c:if test="${u:isPermitted('pm-order-refund')}">
                        ${order.refundGoodButton}
                    </c:if>
                </td>
                <td class="wh5">
                    <span>${order.channelCode}</span>
                </td>
                <td class="wh5">
                    <span>${order.seq}</span>
                </td>
                <td class="wh10">
                    <span>${order.dealSeq}</span>
                </td>
                <td class="wh10">
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
