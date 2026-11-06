<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty mtVmList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
    </c:when>
    <c:otherwise>
    <table>
        <thead>
            <tr>
                <td>售货机编号</td>
                <td>软件版本</td>
                <td>已下载版本</td>
            </tr>
        </thead>
        <tbody class="auto">
        <c:forEach items="${mtVmList}" var="mtVm">
            <tr>
                <td>${mtVm.innerCode}</td>
                <td>
                    <c:choose>
                        <c:when test="${1== plan.upgradePackage.packageType}">
                            <c:forEach items="${mtVm.vmVersion}" var="vmVersion">
                                <c:if test="${ 1== vmVersion.deviceType}">
                                    ${u:getNameByCode("vmVersion",vmVersion.deviceType,"")}:${vmVersion.deviceVersion}
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:when test="${2== plan.upgradePackage.packageType}">
                            <c:forEach items="${mtVm.vmVersion}" var="vmVersion">
                                <c:if test="${ 2== vmVersion.deviceType}">
                                    ${u:getNameByCode("vmVersion",vmVersion.deviceType,"")}:${vmVersion.deviceVersion}
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:when test="${3== plan.upgradePackage.packageType}">
                            <c:forEach items="${mtVm.vmVersion}" var="vmVersion">
                                <c:if test="${ 3== vmVersion.deviceType}">
                                    ${u:getNameByCode("vmVersion",vmVersion.deviceType,"")}:${vmVersion.deviceVersion}
                                </c:if>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${1== plan.upgradePackage.packageType}">
                            ${mtVm.dlVmBoxVer}
                        </c:when>
                        <c:when test="${2== plan.upgradePackage.packageType}">
                            ${mtVm.dlCommunicationVer}
                        </c:when>
                        <c:when test="${3== plan.upgradePackage.packageType}">
                            ${mtVm.dlDeliveryVer}
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty mtVmList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" id="upgradeDetailDiv" setPage="false" />
</c:if>