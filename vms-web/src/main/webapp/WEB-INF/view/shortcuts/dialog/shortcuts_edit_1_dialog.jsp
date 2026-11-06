<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w800">
    <div class="modal-content">
        <form id="edit-shortcuts-form-1" action="${path}/stShortcuts/editPage2" method="POST">
          <input type="hidden" name="strategyId" value="${strategy.strategyId}" >
          <input type="hidden" name="modelId" value="${strategy.vmModel.id}" >
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="addFlashBuyLabel">编辑闪购键配置策略</h4>
        </div>
        <div class="modal-body" id="setFlashBuy">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix step_info">
                <!--填写基本信息-->
                <div class="col_info clearfix">
                    <section class="col-md-6">
                        <span class="" for="clname">策略名称:</span>
                        <div class="form-group">
                          <input class="form-control input-sm" type="text" placeholder="请输入策略名称" name="strategyName" required data-msg-required="请输入策略名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true" value="${strategy.strategyName}" id="strName_edit">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="">售货机型号:</span>
                        <div class="form-group">
                          <input type="text"class="form-control input-sm" placeholder="${strategy.vmModel.modelName}" value="${strategy.vmModel.modelName}" readonly="readonly">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w205" for="moretext">策略说明:</span>
                        <div class="form-group">
                          <textarea class="form-control input-sm" type="text" placeholder="请输入策略说明" name="strategyDesc" required data-msg-required="请输入策略说明" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true" >${strategy.strategyDesc}</textarea>
                        </div>
                    </section>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" id="nextBtn_edit" class="btn btn-success btn-lg2 nextbtn">下一步</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>
            <!--基本信息 end-->
        </div>
      </form>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        Shortcuts.bindEditShortcuts1();
    })
</script>
