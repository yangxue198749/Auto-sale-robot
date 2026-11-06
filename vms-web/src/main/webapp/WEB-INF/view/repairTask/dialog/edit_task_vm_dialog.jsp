<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!--datepicker 扩展插件-->
<link href="${path}/plus/datepicker/css/foundation-datepicker.css" rel="stylesheet" type="text/css">
<script src="${path}/plus/datepicker/js/foundation-datepicker.js"></script>
<script src="${path}/plus/datepicker/js/locales/foundation-datepicker.zh-CN.js"></script>
<script src="${path}/plus/datepicker/js/function.datepicker.js"></script>
<div class="modal-dialog w400">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">修改任务</h4>
         </div>
         <form action="/repairTask/editTaskVm" id="edit-task-form" method="POST">
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
                         <span>负责人:</span>
                         <div class="form-group">
                             <select class="form-control input-sm" name="editOwnerId">
                             ${html }
                             </select>
                         </div>
                     </section>
                     <section class="col-md-6">
                        <span>执行时间:</span>
                        <div class="form-group">
                            <input class="form-control input-sm DateDefault transDate" name="taskTime" value="${taskVm.taskTime }" type="text" placeholder="请选择时间">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span>任务说明:</span>
                        <div class="form-group"><textarea class="form-control input-sm" name="remarks" type="text" placeholder="请输入任务说明">${taskVm.taskRemarks }</textarea></div>
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
	RepairTask.editTaskVm();
    function transDate(){
		var tDate = $(".transDate").val();
		var newtDate = new Date(tDate);
		var str = newtDate.toLocaleDateString().replace(/(\d+)[^\d](\d+)[^\d](\d+)[^\d]?/, "$1-$2-$3");
		$(".transDate").val(str);
    };
    transDate(); //转换日期
})
</script>