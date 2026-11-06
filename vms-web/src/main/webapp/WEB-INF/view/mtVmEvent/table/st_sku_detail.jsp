<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w800">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">策略详情</h4>
        </div>
        <div class="modal-body" id="setSalePromotion">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix step_info">
                <div class="main_commontable max300">
                    <!--theadscroll start-->
                    <div class="theadscroll" id="theadScroll">
                        <ul>
                            <li class="wh20">商品</li>
                            <li class="wh20">折扣类型</li>
                            <li class="wh20">折扣价</li>
                            <li class="wh20">大类</li>
                            <li class="wh20">小类</li>
                        </ul>
                    </div>
                    <!--theadscroll end-->
                    <table>
                        <tbody class="auto">
                        <c:forEach items="${stVmStrategy.stEvent}" var="evt">
                            <tr>
                                <td class="wh20"><span>${evt.sku.skuName}</span></td>
                                <td class="wh20"><span>${u:getDesc(evt.discountType,"discountType")}</span></td>
                                <td class="wh20"><span>${evt.discountValue}${u:getSymbol(evt.discountType,"discountType")}</span></td>
                                <td class="wh20"><span>${evt.sku.skuType.parent.typeName}</span></td>
                                <td class="wh20"><span>${evt.sku.skuType.typeName}</span></td>
                            </tr>
                        </c:forEach>
                        </tbody>

                    </table>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2 nextbtn" data-dismiss="modal">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>

        </div>

    </div>
</div>