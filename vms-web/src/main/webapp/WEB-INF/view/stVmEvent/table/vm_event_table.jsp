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
                <td class="wh3"><input type="checkbox" value="${object.strategy.strategyId}"></td>
                <td class="wh17"><span>${object.strategy.strategyName}</span></td>
                <td class="wh20"><span>${object.strategy.strategyDesc}</span></td>
                <td class="wh15"><span>${u:getDesc(object.discountType,"discountType")}</span></td>
                <td class="wh15"><span>${object.discountValue}${u:getSymbol(object.discountType,"discountType")}</span></td>
                <td class="wh15">
                    <%--<p><a href="#" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="创建日期：<fmt:formatDate value="${object.strategy.ctime}" type="both" dateStyle="full"/>"><span>${u:getUserById(object.strategy.ctor).userName}</span><i class="iconfont f14 ml5 icon-tishi cf_blue"></i></a></p>--%>
                    <p><span>${u:getUserById(object.strategy.ctor).userName}</span></p>
                    <p><fmt:formatDate value="${object.strategy.ctime}" type="both"/></p>
                </td>
                <td class="wh15">
                    <p><span>${u:getUserById(object.strategy.utor).userName}</span></p>
                    <p><fmt:formatDate value="${object.strategy.utime}" type="both"/></p>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<!--table footer start-->
<c:if test="${!empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" setPage="true"/>
</c:if>
<!--table footer end-->