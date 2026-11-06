<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal-dialog w1000">
	     <div class="modal-content">
	         <div class="modal-header">
	             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	             <h4 class="modal-title f16" id="">货道设置</h4>
	         </div>
	         <div class="modal-body">
	                 <!--货道设置信息 start-->
	                 <div class="modal_commonbody clearfix">
	                      <div class="btn-group" data-toggle="buttons">
	         					<button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/stVmChannel/selectSkuDialog?relateType=edit" data-target="#SelectSku" data-must="true" li-set="#editChannelDetailDiv li.on input">关联商品</button>
	         					<button type="button" id="editCancelRelate"  href="/stVmChannel/cancelRelate" class="btn btn-default btn-lg2">取消关联</button>
	         					<button type="button" id="editMergeButton" href="/stVmChannel/mergeChannel" class="btn btn-default btn-lg2">合并货道</button>
	         					<button type="button" id="editCancelMerge" href="/stVmChannel/cancelMerge"  class="btn btn-default btn-lg2">解除合并</button>
	         					<%--
	         					5.0.0a 去掉列表页设置
	         					<button type="button" id="editSetSort" href="/stVmChannel/setSortDialog?strategyId=${strategyId }" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#setSort">列表页设置</button>--%>
	                      </div>
	                      <div id="editChannelDetailDiv" class="col_thegoods mt15 thegoods v3_2">
	                      ${html }
	                      </div>
	                 </div>
	         </div>
	         <!--按钮-->
	         <div class="modal-footer">
	             <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
				 <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
	         </div>
	     </div>
 	</div>
<%--	<!-----修改货道满量 模态框 start----->
<div class="modal fade" id="setFullSku" tabindex="-1" role="dialog" aria-labelledby="设置满量" aria-hidden="true">

</div>--%>

 <script type="text/javascript">
    $(function(){
    	$("#editChannelDetailDiv").on("click","li", function(){
    	   	$(this).toggleClass("on");
    	   });
		$("[data-toggle='tooltip']").tooltip();
    	StVmChannel.editMergeChannel();
    	StVmChannel.editCancelMerge();
    	StVmChannel.editCancelRelate();
        Metronic.imgEmpty('editChannelDetailDiv');
    })
</script>