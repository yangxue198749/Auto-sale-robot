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
                <td class="wh7"><span>${object.mtVm.innerCode}</span></td>
                <td class="wh10"><span>${object.mtVm.node.nodeName}</span></td>
                <td class="wh15"><span class="mr5">${u:getAreaName(object.mtVm.node.provinceId)}</span><span class="mr5">${u:getAreaName(object.mtVm.node.cityId)}</span><span>${u:getAreaName(object.mtVm.node.areaId)}</span></td>
                <td class="wh10"><span>${object.mtVm.vmDesc}</span></td>
                <td class="wh7"><span>${object.eventType==2?'商品促销':'整机促销'}</span></td>
                <td class="wh10"><span>${object.eventName}</span></td>
                <td class="wh10">
                    <p><fmt:formatDate value="${object.beginDate}" type="date" dateStyle="default"/></p>
                    <p><fmt:formatDate value="${object.endDate}" type="date" dateStyle="default"/></p>
                </td>
                <td class="wh8"><span>${object.startTime}-${object.endTime}</span></td>
                <td class="wh8">
                    <button type="button" class="btn btn-success f12 btn-sm" href="${path}/mtVmEvent/detail/mt?mtVmEventId=${object.id}" data-toggle="modal" data-target="#checkStInfo">查看</button>
                </td>
                <td class="wh12">
                    <p><span>${u:getUserById(object.ctor).userName}</span></p>
                    <p><span><fmt:formatDate value="${object.ctime}" type="both" dateStyle="default"/></span></p>
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