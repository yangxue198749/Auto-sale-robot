<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty tMtVmChannels}">
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
        <c:forEach items="${tMtVmChannels}"  var="tMtVmChannel">
            <tr>
                <td class="wh10"><span>
                    <c:if test="${null !=tMtVmChannel.sku}">
                        ${tMtVmChannel.sku.id}
                    </c:if>
                </span></td>
                <td class="wh15"><span>
                    <c:if test="${null !=tMtVmChannel.sku}">
                        ${tMtVmChannel.sku.skuName}
                    </c:if>
                </span></td>
                <td class="wh5"><span>
                    <c:if test="${null !=tMtVmChannel.sku}">
                        ${tMtVmChannel.sku.skuUnit}
                    </c:if>
                </span></td>
                <td class="wh5"><span>
                    ${tMtVmChannel.day}
                </span></td>
                <td class="wh10"><span>
                    <c:if test="${null !=tMtVmChannel.mtVm}">
                        ${tMtVmChannel.mtVm.innerCode}
                    </c:if>
                </span></td>
                <td class="wh5"><span>
                    ${tMtVmChannel.channelCode}
                </span></td>
                <td class="wh5"><span>
                    ${tMtVmChannel.currNum}
                </span></td>
                <td class="wh10"><span>
                    <fmt:formatDate value="${tMtVmChannel.manufactureDate}" type="date"/>
                </span></td>
                <td class="wh10"><span>
                    <fmt:formatDate value="${tMtVmChannel.manufactureEndDate}" type="date"/>
                </span></td>
                <td class="wh10"><span>
                    ${tMtVmChannel.batchNo}
                </span></td>
                <td class="wh10"><span>
                    <c:if test="${null !=tMtVmChannel.mtVm.node}">
                        ${tMtVmChannel.mtVm.node.nodeName}
                    </c:if>
                </span></td>
                <td class="wh5"><span>
                    <c:if test="${null !=tMtVmChannel.mtVm.node.delivery}">
                        ${tMtVmChannel.mtVm.node.delivery.userName}
                    </c:if>
                </span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<!--table footer start-->
<c:if test="${!empty tMtVmChannels}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" setPage="true"/>
</c:if>
<!--table footer end-->