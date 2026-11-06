<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal-dialog w800">
     <div class="modal-content">
     <form id="addStVmText-form" action="/stVmText/submitAddStVmText" method="POST">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">新建走马灯策略</h4>
         </div>
         <div class="modal-body">
             <!--走马灯 start-->
             <div class="modal_commonbody clearfix mt20">
                 <!--填写基本信息-->
                 <div class="step_info">
                     <section class="col-md-6">
                         <span class="pull-left w80 text-right pr5">策略名称:</span>
                         <div class="form-group w160 pull-left"><input class="form-control input-sm" name="addStVmName" type="text" placeholder="请输入策略名称"></div>
                     </section>
                     <section class="col-md-6">
                         <span class="pull-left w80 text-right pr5">策略说明:</span>
                         <div class="form-group w160 pull-left"><input class="form-control input-sm" name="addStVmDesc" type="text" placeholder="请输入策略说明"></div>
                     </section>
                     <section class="col-md-1">
                         <span class="pull-left w80 text-right pr5">文案:</span>

                     </section>
                     <section class="col-md-11">
                         <span class="form-group w160 pull-left ml20">1000字以内</span>
                         <textarea name="addStVmText" class="form-control input-sm pull-left w545 text-left ml15" placeholder="请输入文案内容" maxlength="1000"></textarea>
                     </section>


                 </div>
             </div>

         </div>
         <div class="modal-footer">
             <button type="submit" class="btn btn-success btn-lg2">确定</button>
             <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
         </div>
         </form>
     </div>
 </div>
 <script type="text/javascript">
    $(function(){
    	StVmText.addStVmText();
    	
    })
</script>