<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w1000">
    <div class="modal-content">
        <form id="strategy-form" action="${path}/stSkuPrice/save" method="POST">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="addFlashBuyLabel"><c:choose><c:when test="${ empty stVmStrategy}">新建</c:when><c:otherwise>修改</c:otherwise></c:choose>价格策略</h4>
        </div>
        <div class="modal-body" id="setSkuPrice">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix step_info">
                <!--填写基本信息-->
                <div class="col_info clearfix">
                    <input type="hidden" value="${stVmStrategy.strategyId}" name="strategyId" id="strategyId">
                    <section class="col-md-12">
                        <span class="w205">策略名称:</span>
                        <div class="form-group"><input id="strategyName" name="strategyName" value="${stVmStrategy.strategyName}" class="form-control input-sm" type="text" placeholder="请输入策略名称"></div>
                    </section>
                    <section class="col-md-12">
                        <span class="w205">策略说明:</span>
                        <div class="form-group"><textarea id="strategyDesc" name="strategyDesc" class="form-control input-sm" type="text" placeholder="请输入策略说明">${stVmStrategy.strategyDesc}</textarea></div>
                    </section>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2 nextbtn">下一步</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>
            <!--基本信息 end-->
            <!--添加商品信息 start-->
            <div class="modal_commonbody clearfix step_choice" style="display:none;">
                <!--添加商品-->
                <div class="main_commonhead hl30 b1 clearfix">
                    <button type="button" class="btn btn-success pull-left f12 btn-lg2 addbtn" >添加商品</button>
                </div>
                <!--配置商品信息-->
                <div class="main_commontable mb10 max400" id="skuPriceDiv">
                    <table>
                        <thead>
                        <tr>
                            <td class="wh40">商品名称</td>
                            <td class="wh20">建议零售价(分)</td>
                            <td class="wh20">售价(分)</td>
                            <td class="wh20">操作</td>
                        </tr>
                        </thead>
                        <tbody>
                                <c:forEach items="${stVmStrategy.stPrice}" varStatus="vs" var="object">
                                    <tr id="${object.sku.id}">
                                        <input type="hidden" value="${object.sku.id}" id="skuId${vs.index}" name="skuIds"/>
                                        <td class="wh40">${object.sku.skuName}${object.sku.skuUnit}</td>
                                        <td class="wh20">${object.sku.offerPrice}</td>
                                        <td class="wh20"><input class="form-control input-sm w100 auto" type="text" placeholder="请输入售价" value="${object.realPrice}" id="realPrice${vs.index}" name="realPrices" required min="1" digits="true"></td>
                                        <td class="wh20"><button type="button" class="btn btn-danger f12 btn-sm btn_sku_delete">删除</button></td>
                                    </tr>
                                </c:forEach>
                        </tbody>
                    </table>
                </div>
              <%--  <script>
                    $(function(){
                        $('#skuPriceDiv input.input-sm:last-of-type').focus();
                    })
                </script>--%>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success btn-lg2">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>
            <!--配置价格策略信息 end-->
            <!--关联商品 start-->
            <div class="modal_commonbody clearfix step_relation" id="skudiv" style="display:none;">
            </div>
            <!--关联商品 end-->
        </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function(){
        StSkuPrice.addStSkuPrice();
        //价格按id分别验证
        if ($.validator) {
            $.validator.prototype.elements = function () {
                var validator = this,
                        rulesCache = {};

                // select all valid inputs inside the form (no submit or reset buttons)
                return $(this.currentForm)
                        .find("input, select, textarea")
                        .not(":submit, :reset, :image, [disabled]")
                        .not(this.settings.ignore)
                        .filter(function () {
                            if (!this.name && validator.settings.debug && window.console) {
                                console.error("%o has no name assigned", this);
                            }
                            //注释这行代码
                            // select only the first element for each name, and only those with rules specified
                            //if ( this.name in rulesCache || !validator.objectLength($(this).rules()) ) {
                            //    return false;
                            //}
                            rulesCache[this.name] = true;
                            return true;
                        });
            }
        }

    })
</script>