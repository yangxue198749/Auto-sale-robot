<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="modal-dialog w400">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h4 class="modal-title" id="">修改公司</h4>
		</div>
		<form id="changeCompanyForm" action="/company/update" method="post">
			<div class="modal-body">
				<input name="ids" value="${ids }" type="hidden" >
				<div class="modal_commonbody clearfix">
					<section class="col-md-12">
						<span class="w100" for="companyName">公司名称:</span>
						<div class="form-group">
							<input class="form-control input-sm" name="companyName"
								type="text" placeholder="请输入公司名称" required
								data-msg-required="请输入公司名称" minlength="2"
								data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
						</div>
					</section>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="submit" class="btn btn-success">确定</button>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		$("#changeCompanyForm").validate({
	        submitHandler:function(form){
	        	MtCompany.editCompany();
	        }    
	    });
		//MtCompany.editCompany();
	})
</script>