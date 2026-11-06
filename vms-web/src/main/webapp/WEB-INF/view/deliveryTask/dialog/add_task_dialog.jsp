<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal-dialog w400">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">新增任务</h4>
         </div>
         <form action="/deliveryTask/addTask" method="POST" id="add-task-form" class="unKeyCode">
         <input type="hidden" name="vmIds" id="addTaskVmIds">
         <input type="hidden" name="frmjson" id="adjustData">
         <input type="hidden" id="vmVal">
         <div class="modal-body">
             <!--基本信息 start-->
             <div class="modal_commonbody clearfix">
                 <!--填写基本信息-->
                 <div class="col_info clearfix">
                     <section class="col-md-12">
                         <span>售货机:<span class="ml10" id="innerCodeP"></span></span>
                         <div class="form-group">
                              <button type="button" class="btn btn-default btn-sm2 f12 btn-block" id="selectVm" href="/deliveryTask/mtVmList" data-toggle="modal" data-target="#selectMtVm">选择售货机</button>
                         </div>
                     </section>
                     <section class="col-md-12">
                         <span>配送员:</span>
                         <div class="form-group">
                             <select class="form-control input-sm" id="ownerId" name="ownerId">
                               <option>请选择</option>
                             </select>
                         </div>
                     </section>
                     <div class="clearfix"></div>
                     <div class="otherInfoTips"><span class="tipsBtn" id="otherInfoBtn">更多选项</span></div>
                     <div class="otherInfoWrap" id="otherInfoWrap">
                         <div class="col-md-12">
                             <span class="pull-left w60 text-right mr20">补货方式:</span>
                             <div class="form-group pull-left" id="handBtnGroup">
                                 <label class="w70 mr10 val0"><input class="input_v4 mr5" type="radio" name="deliveryPattern" value="0" checked="checked"><span>补满</span></label>
                                 <label class="w100 val1"><input class="input_v4 mr5" type="radio" name="deliveryPattern" value="1" id="adjustment"><span>手动</span></label>
                                 <button type="button" class="w220 form-control input-sm handBtn adjust" style="display: none" data-toggle="modal" id="adJustModelBtn" data-target="#adJustModel">手动调量</button>
                             </div>
                         </div>
                         <div class="clearfix"></div>
                     </div>
                 </div>
             </div>
         </div>
         <!--按钮-->
         <div class="modal-footer">
             <button type="submit" class="btn btn-success btn-lg2">发布</button>
             <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
         </div>
         </form>
     </div>
 </div>
 <script type="text/javascript">
$(function(){
    $('#add-task-form .btn-success').click(function(){
        var adjustment = $('#adjustment');
        if(adjustment.prop('checked')){
            if($('#adjustData').val() == '' || $('#adjustData').val() == undefined){
                layer.msg("请先进行手动调量的操作",{shade: [0.5, '#fff'],time:2000});
                return false;
            }
        }
        DeliveryTask.addTask();
    });
	//DeliveryTask.addTask();
    DeliveryTask.cheaddTask();
    DeliveryTask.otherClick();
})
</script>