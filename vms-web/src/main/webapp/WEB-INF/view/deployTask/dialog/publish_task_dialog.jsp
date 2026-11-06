<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script src="${path}/plus/datepicker/js/function.datepicker.js"></script>
 <div class="modal-dialog w400">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">发布任务</h4>
         </div>
         <form action="/deployTask/publishTask" method="POST" id="publish-task-form" class="unKeyCode">
             <c:forEach items="${taskVmIdList}" var="vmId" >
                 <input type="hidden" name="ids" value="${vmId}">
             </c:forEach>
         <div class="modal-body">
             <!--基本信息 start-->
             <div class="modal_commonbody clearfix">
                 <!--填写基本信息-->
                 <div class="col_info clearfix">
                     <section class="col-md-6">
                         <span>任务负责人:</span>
                         <div class="form-group">
                             <select class="form-control input-sm" name="ownerId">
                              ${html }
                             </select>
                         </div>
                     </section>
                     <section class="col-md-6">
                         <span>投放日期:</span>
                         <div class="form-group"><input class="form-control input-sm DateDefault" name="taskTime" type="text" placeholder="选择投放日期"></div>
                     </section>
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
	DeployTask.publishTask();
})
</script>