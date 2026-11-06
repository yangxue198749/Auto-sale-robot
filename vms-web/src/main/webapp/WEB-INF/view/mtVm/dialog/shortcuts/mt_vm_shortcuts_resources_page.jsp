<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty mtVmResourceList }">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="5"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <thead>
            <tr>
                <td class="wh10"></td>
                <td class="wh40">跳转页名称</td>
                <td class="wh50">跳转页描述</td>
            </tr>
        </thead>
        <tbody class="auto">
        <c:forEach  items="${mtVmResourceList }" var="mtVmResources">
            <tr>
                <td><input type="checkbox" value="${mtVmResources.id}" resourceName="${mtVmResources.resourceName}"></td>
                <td><span>${mtVmResources.resourceName}</span></td>
                <td><span>${mtVmResources.resourceDesc}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty  mtVmResourceList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false" id="selectMtVmResourcesDiv" />
</c:if>
