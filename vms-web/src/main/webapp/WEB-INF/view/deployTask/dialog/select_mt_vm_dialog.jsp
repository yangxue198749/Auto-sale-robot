<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript" src="/vmsjs/metronic.js"></script>
<div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">选择设备</h4>
                </div>
                <div class="modal-body">
                    <div class="main_commonhead hl30">
                    <form action="/deployTask/mtVmPage" id="search-mtVm-form" method="POST" class="unKeyCode">
                        <section>
                            <strong>点位:</strong>
                            <div class="form-group w120 pull-left">
                            	<input class="form-control input-sm" name="searchNodeName" type="text" placeholder="请输入点位名称">
                            </div>
                        </section>
                        <section>
                            <strong>售货机编号:</strong>
                            <div class="form-group w120 pull-left">
                            	<input class="form-control input-sm" name="searchInnerCode" type="text" placeholder="请输入售货机编号">
                            </div>
                        </section>
                        <button type="button" id="search-mtVm-btn" class="btn btn-default f12 btn-lg2 pull-left">查询</button>
                    </form>
                    </div>
                    <div class="clearfix"></div>
                    <div id="mtVmDiv">
                    	<jsp:include page="/deployTask/mtVmPage" flush="true"/>
                    </div>
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="button" id="select-mtVm-btn" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </div>
        </div>
<script>
  $(function(){
	  DeployTask.selectMtVm();
	//处理table单选
	$(".oneCheck td").on("click","input[type='checkbox']",function(){
		if($(this).prop("checked")){
			$(this).parents().siblings().find("input[type='checkbox']").prop("checked",false);
		}
	});
 })
</script>