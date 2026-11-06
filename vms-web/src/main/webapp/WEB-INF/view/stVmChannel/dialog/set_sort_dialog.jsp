<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">列表页设置</h4>
        </div>
        <div class="modal-body">
                <!--货道设置信息 start-->
                <div class="modal_commonbody clearfix">
                     <div class="main_commonhead hl30">
                            <button type="button" id="chengeSortBtn" href="/stVmChannel/changeSort" class="btn btn-default f12 btn-lg2 mr10">交换</button>
                         <button type="button" id="copySortBtn" href="/stVmChannel/copySort" class="btn btn-default f12 btn-lg2 mr10">复制</button>
                         <button type="button" id="deleteSortBtn" href="/stVmChannel/deleteSort" class="btn btn-default f12 btn-lg2 mr10">删除</button>

                     </div>
                    <!--商品 box start-->
                    <div id="showSortDiv" class="col_relation mt15 small choiceTheGoods">
                         ${html }
                    </div>
                    <!--商品 box end-->
                     
                </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">关闭</button>
        </div>
    </div>
</div>
        
 <script>
	$(function(){
	   $("#showSortDiv").on("click","li", function(){
	   		$(this).toggleClass("on");
		})
		StVmChannel.changeSort();
		StVmChannel.copySort();
		StVmChannel.deleteSort();
	})
</script>