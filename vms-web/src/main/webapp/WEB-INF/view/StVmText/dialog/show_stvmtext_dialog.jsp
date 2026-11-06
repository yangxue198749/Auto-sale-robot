<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal-dialog w600">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">查看走马灯策略</h4>
             <input type="hidden" name="editStrategyId" value="${editStrategyId }">
         </div>
         <div class="modal-body">
             <!--走马灯 start-->
             <div class="modal_commonbody clearfix">
                 <section class="col-md-12 mt20 mb20">
                     <%--<p class="form-group">1000字以内</p>--%>
                     <textarea name="editStVmText" class="form-control input-sm pull-left wh100 text-left" disabled="disabled" placeholder="暂无走马灯文案内容，如需添加，请选择修改并补充走马灯文案">${stVmText.textContents }</textarea>
                 </section>
             </div>
         </div>
         <div class="modal-footer">
             <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">关闭</button>
         </div>
     </div>
 </div>
