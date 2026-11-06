<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal-dialog w1000">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">查看货道</h4>
         </div>
         <div class="modal-body">
              <div class="col_thegoods v3_2" id="showChannelGroup">
                 ${html }
              </div>
         </div>
         <div class="modal-footer">
             <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">关闭</button>
         </div>
     </div>
 </div>
<script>
    Metronic.imgEmpty('showChannelGroup');
</script>