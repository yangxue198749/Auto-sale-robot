<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal-dialog w600">
         <div class="modal-content">
             <div class="modal-header">
                 <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                 <h4 class="modal-title f16">修改节能策略</h4>
             </div>
             <div class="modal-body">
                     <div class="modal_commonbody mt10 clearfix">
                         <section class="col-md-12">
                             <div class="btn-group pull-left" data-toggle="buttons">
                                 <button type="button" class="btn btn-default btn-lg2"  href="/stVmEnergySave/editAddEnergyDialog?strategyId=${strategyId }" data-toggle="modal" data-target="#editAddEnergySave">添加</button>
                                 <button type="button" class="btn btn-default btn-lg2 tableDelete" data-must="true" data-set="#editEnergySaveDetailDiv table tbody input[type=checkbox]:checked" href="/stVmEnergySave/deleteEnergySave">删除</button>
                                 <button type="button" class="btn btn-default btn-lg2" data-set="#editEnergySaveDetailDiv table tbody input[type='checkbox']:checked" data-must="true" select-one="true" href="/stVmEnergySave/editEditEnergySaveDialog" data-toggle="modal" data-target="#editEditEnergySave">修改</button>
                             </div>
                         </section>
                     </div>
                 <div class="main_commontable mt20">
                     <!--theadscroll start-->
                     <div class="theadscroll">
                         <ul>
                             <li class="wh3"><input type="checkbox" value=""></li>
                             <li class="wh17">节能类型</li>
                             <li class="wh20">开始时间</li>
                             <li class="wh20">结束时间</li>
                             <li class="wh20">保温箱编号</li>
                             <li class="wh20">温度目标值</li>
                         </ul>
                     </div>
                     <!--theadscroll end-->
                     <div id="editEnergySaveDetailDiv">
                     ${html }
                     </div>
                 </div>
             </div>
             <div class="modal-footer">
                 <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
             </div>
         </div>
		</div>
		
	<script type="text/javascript">
    $(function(){
    	$(".tableDelete").click(function(){
    		var $this = $(this);
    		var dataSet=$this.attr('data-set');
    		var url = $this.attr('href');
    		var chkBox = $(dataSet);
    		var ids="";
    		if(chkBox.length == 0){
    			layer.msg("请先选中需要删除的内容",{shade: [0.5, '#fff'],time:2000,});
    			return false;
    		}else{
    			$(dataSet).each(function() {
    				ids += $(this).val()+",";
    			});
    			ids = ids.substring(0,ids.length-1);
    			layer.confirm('您确定要删除这'+ chkBox.length +'条内容？', {
    			  btn: ['确定','取消'],
    			  title: '提示',
    			}, function(){ //成功后执行
    			  //这里ajax处理服务端删除
    				$.ajax({
    				    type : 'post',
    				    url :url,
    				    data : {ids:ids},
    				    async : true,
    				    dataType : 'json',
    				    cache : false,
    				    success : function(data) {
    				    	if(data.type="success"){
    				    		chkBox.parents("tr").remove();
    				    		layer.msg('已删除',{shade: [0.5, '#fff'],time:2000,});
    				    	}
    						tablePlaceholder();
    				    }
    				});
    			}, function(index){
    			  layer.close(index);//关闭窗口
    			});
    		}
    	});  
    })
</script>