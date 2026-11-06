<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<c:choose>
    <c:when test="${empty reserveStatisticsList}">
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
            <c:forEach items="${reserveStatisticsList}" var="reserveStatistics">
            <tr>
                <td class="wh20">
                    <span>${reserveStatistics.statisticsDate}</span>
                </td>
                <td class="wh15">
                    <span>${reserveStatistics.vm.innerCode}</span>
                </td>
                <td class="wh20">
                    <span>${reserveStatistics.sku.skuName}${reserveStatistics.sku.skuUnit}</span>
                </td>
                <td class="wh15">
                    <span class="icon_state blue">${reserveStatistics.pendingNum}</span>
                </td>
                <td class="wh15">
                    <span class="icon_state green">${reserveStatistics.distributionNum}</span>
                </td>
                <td class="wh15">
                    <span class="icon_state gray">${u:findOutSuccessReserveCount(reserveStatistics.vm.id,reserveStatistics.sku.id,reserveStatistics.statisticsDate)}</span>
                </td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty reserveStatisticsList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
