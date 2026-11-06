<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">商品促销策略详情</h4>
        </div>
        <div class="modal-body">
            <div class="main_commonbody clearfix">
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">策略名:</span>
                    <div class="form-group">
                        <span>${mtEvent.eventName}</span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">策略说明:</span>
                    <div class="form-group">
                        <span>${mtEvent.eventName}</span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">生效日期:</span>
                    <div class="form-group">
                        <span><fmt:formatDate value="${mtEvent.beginDate}" type="date" dateStyle="default"/>-<fmt:formatDate value="${mtEvent.endDate}" type="date" dateStyle="default"/></span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">生效时段:</span>
                    <div class="form-group">
                        <span>${mtEvent.startTime}-${mtEvent.endTime}</span>
                    </div>
                </section>
            </div>
            <div class="main_commontable tablePlaceholder max400">
                <table>
                    <thead>
                        <tr>
                            <td class="wh20">商品</td>
                            <td class="wh20">折扣类型</td>
                            <td class="wh20">折扣额</td>
                            <td class="wh20">大类</td>
                            <td class="wh20">小类</td>
                        </tr>
                    </thead>
                    <tbody class="auto">
                    <c:forEach items="${mtEvent.events}" var="evt">
                        <tr>
                            <td><span>${evt.sku.skuName}</span></td>
                            <td><span>${u:getDesc(evt.discountType,"discountType")}</span></td>
                            <td><span>${evt.discountValue}${u:getSymbol(evt.discountType,"discountType")}</span></td>
                            <td><span>${evt.sku.skuType.parent.typeName}</span></td>
                            <td><span>${evt.sku.skuType.typeName}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-success" data-dismiss="modal">确定</button>
        </div>
    </div>
</div>