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
                <td class="wh3"><input type="checkbox" value="${object.id}"></td>
                <td class="wh17"><span>${object.innerCode}</span></td>
                <td class="wh20"><span>${u:getAreaName(object.node.provinceId)} ${u:getAreaName(object.node.cityId)} ${u:getAreaName(object.node.areaId)}</span></td>
                <td class="wh30"><span>${object.node.nodeName}</span></td>
                <td class="wh30"><span>${object.uniqueCode}</span></td>
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