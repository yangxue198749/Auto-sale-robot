<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新增售货机促销策略</h4>
        </div>
        <form action="${path}/stVmEvent/save" id="addAutoMatSaleForm" method="POST">
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody clearfix">
                    <input type="hidden" value="${vmEvent.strategy.strategyId}" name="strategyId" id="strategyId">
                    <div class="col_info clearfix">
                        <section class="col-md-12">
                            <span for="strategyName">策略名称:</span>
                            <div class="form-group"><input class="form-control input-sm" type="text"  placeholder="请输入策略名称" name="strategyName" value="${vmEvent.strategy.strategyName}" required data-msg-required="请输入策略名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true"></div>
                        </section>
                        <section class="col-md-6">
                            <strong for="discountType">折扣类型:</strong>
                            <div class="form-group">
                                <select class="form-control input-sm" name="discountType" id="discountTypeSlt" required data-msg-required="请选择折扣类型">
                                    <option value="1">百分比</option>
                                    <option value="2" ${vmEvent.discountType==2?'selected':''}>绝对值</option>
                                </select>
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span for="discountValue">折后额:</span>
                            <div class="form-group"><input class="form-control input-sm" type="text" placeholder="请输入折后额" name="discountValue" id="discountValueInput" value="${vmEvent.discountValue}" required data-msg-required="请输入折后额" minlength="1" min="1" digits="true"></div>
                        </section>
                        <section class="col-md-12">
                            <span for="strategyDesc">策略说明:</span>
                            <div class="form-group"><textarea class="form-control input-sm" type="text" placeholder="请输入策略说明" name="strategyDesc" required data-msg-required="请输入策略说明" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">${vmEvent.strategy.strategyDesc}</textarea></div>
                        </section>
                    </div>
                </div>
                <!--基本信息 end-->
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function(){
        $('#addAutoMatSaleForm button[type="submit"]').click(function(){
            var disTypeSlt = $('#discountTypeSlt option:selected').val();
            var disValueInp = $('#discountValueInput').val();
            if(disTypeSlt == 1){
                if(disValueInp > 100){
                    layer.msg('折扣百分比不能超出100',{shade: [0.5, '#fff'],time:1000});
                    return false;
                }
            }
        });
        StVmEvent.addStVmEvent();
    })
</script>