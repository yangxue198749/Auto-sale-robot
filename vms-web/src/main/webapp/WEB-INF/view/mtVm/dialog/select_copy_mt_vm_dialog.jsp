<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog w700">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">选择要复制的售货机策略</h4>
            </div>
            <div class="modal-body">
                <div class="main_commonhead hl30">
                <form action="/mtVm/selectCopyMtVmPage" id="srarch-copy-mt-vm-form" method="POST">
                    <section>
                        <strong>售货机编号:</strong>
                        <div class="form-group w120 pull-left">
                        	<input class="form-control input-sm" name="searchInnerCode" type="text" placeholder="请输入售货机编号">
                        </div>
                    </section>
                    <section>
                        <strong>点位:</strong>
                        <div class="form-group w120 pull-left">
                       		<input class="form-control input-sm" name="searchNodeName" type="text" placeholder="请输入点位名称">
                        </div>
                    </section>
                    <button type="button" id="srarch-copy-mt-vm-btn" class="btn btn-default f12 btn-lg2">查询</button>
                </form>
                </div>
                <form action="" name="">
                <div class="main_commontable max300 border_line" id="copyMtVmDiv">
                    <jsp:include page="/mtVm/selectCopyMtVmPage" flush="true"/>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" id="select-copy-mt-vm-btn" class="btn btn-success btn-lg2">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
                </form>
                
            </div>
        </div>
    </div>
 <script type="text/javascript">
$(function(){
	MtVm.selectCopyMtVm();
})
</script>
 
