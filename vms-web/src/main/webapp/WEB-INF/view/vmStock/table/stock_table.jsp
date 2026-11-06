<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
    <c:choose>
        <c:when test="${empty vmStockList}">
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
            <c:forEach items="${vmStockList}" var="stock">
                <tr>
                    <td>
                        <span>${stock.name}</span>
                    </td>
                    <td>
                        <span>${stock.channel}</span>
                    </td>
                    <td>
                        <span>${stock.maxCount}</span>
                    </td>
                    <td>
                        <span>${stock.currCount}</span>
                    </td>
                    <td>
                        <span>${stock.dayCount}</span>
                    </td>
                    <td>
                        <span>${stock.hasStock}</span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </c:otherwise>
    </c:choose>
<c:if test="${! empty vmStockList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false"
             id="stock-pagediv"/>
</c:if>
