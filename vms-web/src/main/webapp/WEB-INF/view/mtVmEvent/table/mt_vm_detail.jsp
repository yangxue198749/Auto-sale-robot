<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">整机促销策略详情</h4>
        </div>
        <div class="modal-body">
            <div class="main_commonbody clearfix">
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">策略名:</span>
                    <div class="form-group">
                        <span>${mtEvent.master.eventName}</span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">策略说明:</span>
                    <div class="form-group">
                        <span>${mtEvent.master.eventDesc}</span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">折扣类型:</span>
                    <div class="form-group">
                        <span>${u:getDesc(mtEvent.discountType,"discountType")}</span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">折扣额:</span>
                    <div class="form-group">
                        <span>${mtEvent.discountValue}${u:getSymbol(mtEvent.discountType,"discountType")}</span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">生效日期:</span>
                    <div class="form-group">
                        <span><fmt:formatDate value="${mtEvent.master.beginDate}" type="date" dateStyle="default"/>-<fmt:formatDate value="${mtEvent.master.endDate}" type="date" dateStyle="default"/></span>
                    </div>
                </section>
                <section class="col-md-6">
                    <span class="w60 pull-left text-right mr10">生效时段:</span>
                    <div class="form-group">
                        <span>${mtEvent.master.startTime}-${mtEvent.master.endTime}</span>
                    </div>
                </section>
            </div>

        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            <button type="button" class="btn btn-success" data-dismiss="modal">确定</button>
        </div>
    </div>
</div>