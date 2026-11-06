<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal-dialog w400">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">修改任务</h4>
         </div>
         <form action="/deliveryTask/editTaskVm" id="edit-task-form" method="POST" class="unKeyCode">
         <input name="taskVmId" type="hidden" value="${taskVm.taskVmId }">
         <div class="modal-body">
             <!--基本信息 start-->
             <div class="modal_commonbody clearfix">
                 <!--填写基本信息-->
                 <div class="col_info clearfix">
                     <section class="col-md-12">
                         <span>售货机:<span class="ml10">${taskVm.mtVm.innerCode }</span></span>
                     </section>
                     <section class="col-md-12">
                         <span>配送员:</span>
                         <div class="form-group">
                             <select class="form-control input-sm" name="editOwnerId">
                             ${html }
                             </select>
                         </div>
                     </section>
                 </div>
             </div>
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
	DeliveryTask.editTaskVm();
})
</script>