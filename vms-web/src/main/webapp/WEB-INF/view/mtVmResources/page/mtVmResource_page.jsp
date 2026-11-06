<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty mtVmResourceList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="3">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <tbody class="auto">
        <c:forEach items="${mtVmResourceList}" var="mtVmResource">
            <%--111--%>
            <tr>
                <td class="wh5">
                    <input type="checkbox" value="${mtVmResource.id}">
                </td>
                <td class="wh30">
                    <span>${mtVmResource.resourceName}</span>
                </td>
                <td class="wh55">
                    <span>${mtVmResource.resourceDesc}</span>
                </td>
                <td class="wh11">
                    <p><span>${u:getUserById(mtVmResource.ctor).userName}</span></p>
                    <p><span><fmt:formatDate value="${mtVmResource.ctime}" type="both"/></span></p>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty mtVmResourceList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true" />
</c:if>