<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
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
            <c:forEach items="${subList}" varStatus="vs" var="object">
            <tr>
                <td class="wh3"><input type="checkbox" value="${object.strategyId}"></td>
                <td class="wh12"><span class="cf_green">${object.strategyName}</span></td>
                <td class="wh25"><span>${object.strategyDesc}</span></td>
                <td class="wh10"><button type="button" href="${path}/stSkuPrice/detail?strategyId=${object.strategyId}" class="btn btn-success f12 btn-sm" data-toggle="modal" data-target="#viewPrice">查看</button></td>
                <td class="wh10"><span>${u:getUserById(object.ctor).userName}</span></td>
                <td class="wh15"><span><fmt:formatDate value="${object.ctime}" type="both" dateStyle="default"/></span></td>
                <td class="wh10"><span>${u:getUserById(object.utor).userName}</span></td>
                <td class="wh15"><span><fmt:formatDate value="${object.utime}" type="both" dateStyle="default"/></span></td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${!empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" setPage="true"/>
</c:if>

