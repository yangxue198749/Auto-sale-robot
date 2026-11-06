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
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="13">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <tbody class="auto">
        <c:forEach items="${subList}" varStatus="vs" var="object">
            <tr>
                <td class="wh3"><input type="checkbox" value="${object.id}"></td>
                <td class="wh10"><span>${object.nodeName}</span></td>
                <td class="wh5"><span>${object.vmSize}</span></td>
                <td class="wh15"><span>${u:getAreaName(object.provinceId)} ${u:getAreaName(object.cityId)} ${u:getAreaName(object.areaId)}</span></td>
                <td class="wh5"><span>${u:getBdName(object.businessDistrictId)}</span></td>
                <td class="wh5"><span>${object.repair.userName}</span></td>
                <td class="wh5"><span>${object.delivery.userName}</span></td>
                <td class="wh15"><span>${object.nodeAddress}</span></td>
                <td class="wh5"><span>${object.partner.partnerName}</span></td>
                <td class="wh5"><span>${object.fillType eq 1?'自补':object.fillType eq 2?'客户补':""}</span></td>
                <td class="wh5"><button type="button" href="${path}/mtNode/detail?nodeId=${object.id}" class="btn btn-success f12 btn-sm" data-toggle="modal" data-target="#detailInfo">查看</button></td>
                <td class="wh11">
                    <p><span>${u:getUserById(object.ctor).userName}</span></p>
                    <p><span><fmt:formatDate value="${object.ctime}" type="both"/></span></p>
                </td>
                <td class="wh11">
                    <p><span>${u:getUserById(object.utor).userName}</span></p>
                    <p><span><fmt:formatDate value="${object.utime}" type="both"/></span></p>
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