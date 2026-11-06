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
            <c:forEach items="${subList}" varStatus="vs" var="detail">
                    <tr>
                        <td class="wh10">
                            <span>${detail.nodeName}</span>
                        </td>
                        <td class="wh5">
                            <span>${detail.innerCode}</span>
                        </td>
                        <td class="wh10">
                            <span class="${detail.color}">${detail.orderSeq}</span>
                        </td>
                        <td class="wh5">
                            <span>
                                    ${detail.channelCode}
                            </span>
                        </td>
                        <td class="wh10">
                            <span>${detail.skuName}${detail.skuUnit}
                            </span>
                        </td>
                        <td class="wh5">
                            <span>${detail.sku.id}</span>
                        </td>
                        <td class="wh5">
                            <span>${'虚拟卡' == detail.costTypeName ? '微信支付' : detail.costTypeName}</span>
                        </td>
                        <td class="wh5">
                            <span>${detail.skuMoney}</span>
                        </td>
                            <td class="wh5">
                                <span>${u:formatAmount(detail.costMoney) }</span>
                            </td>
                            <td class="wh5">
                                <span>${u:formatAmount(detail.cardCostMoney)}</span>
                            </td>
                            <td class="wh5">
                                <span>
                                       ${detail.payDay}
                                </span>
                            </td>
                           <td class="wh5">
                               <span>
                                    ${detail.vendoutDay}
                               </span>
                           </td>
                           <td class="wh5">
                               <span>${detail.outStatusName}</span>
                           </td>
                           <td class="wh5">
                               <span>${detail.outStatusFailReason}</span>
                           </td>
                           <td class="wh5">
                               <span>${detail.refundInfo}</span>
                               <c:if test="${u:isPermitted('vendout-refund')}">
                                   ${detail.refundButton}
                               </c:if>
                           </td>
                        <td class="wh5">
                            <span title="${detail.memo}" style="white-space: nowrap; text-overflow: ellipsis; overflow: hidden; display: inline-block; width: 100%;">
                                    ${detail.memo}
                            </span>
                        </td>
                        <td class="wh5">
                            <span><button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#addRemarkModel" href="/vendout/findMemo?detailId=${detail.id}">备注</button></span>
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