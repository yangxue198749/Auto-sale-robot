<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty vmBoxUpgradeList}">
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
        <c:forEach items="${vmBoxUpgradeList}" var="vmBoxUpgrade">
        <tr>
            <td class="wh20">
                <c:choose>
                    <c:when test="${1== vmBoxUpgrade.upgradePackage.packageType}">
                        vmBox
                    </c:when>
                    <c:when test="${2== vmBoxUpgrade.upgradePackage.packageType}">
                        通信板
                    </c:when>
                    <c:when test="${3== vmBoxUpgrade.upgradePackage.packageType}">
                        出货版
                    </c:when>
                </c:choose>
            </td>
            <td class="wh20">
                <span>${vmBoxUpgrade.upgradePackage.packageVersion}</span>
            </td>
            <td class="wh20">
                <span>${u:getAreaNameById(vmBoxUpgrade.areaId)}</span>
            </td>
            <td class="wh15">
                <span>${u:getModelName(vmBoxUpgrade.vmModelId)}</span>
            </td>
            <td class="wh10">
                <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#viewUpdateDetail" href="/vmBoxUpgrade/showDetailDialog?planId=${vmBoxUpgrade.id}">查看</button>
            </td>
            <td class="wh15">
                <p><span>${u:getUserById(vmBoxUpgrade.ctor).userName}</span></p>
                <p><span><fmt:formatDate value="${vmBoxUpgrade.ctime}" type="both"/></span></p>
            </td>
        </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty vmBoxUpgradeList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
