<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w400">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title" id="">增加售货机数量</h4>
		</div>
		<c:choose>
			<c:when test="${isAddCapacityPage != null && isAddCapacityPage == false}">
				<div class="modal-body">
					<p class="f14 text-center">不能给自己分配售货机</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-success" data-dismiss="modal">确定</button>
				</div>
			</c:when>
			<c:otherwise>
				<form id="addCapacityCompanyForm" action="/company/addCapacity"
					method="post">
					<div class="modal-body">
						<input name="ids" value="${ids}" type="hidden" >
						<input name="oldVmCapacitySum" value="${useSum}" type="hidden" >
						<div class="modal_commonbody clearfix">
							<section class="col-md-12">
								<span class="w100" for="newVmCapacitySum">售货机数量:</span>
								<div class="form-group">
									<input class="form-control input-sm" name="newVmCapacitySum"
										type="text" placeholder="请输入给该公司的售货机数量" 
										required digits=true data-msg-digits="必须为整数" min="${minSum}" max="${maxSum}" value="${useSum }" 
										data-msg-min="最小不能小于${minSum}" data-msg-max="最多不能超过${maxSum}">
								</div>
							</section>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="submit" class="btn btn-success commit">确定</button>
					</div>
				</form>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<script type="text/javascript">
    $(function(){
	  $("#addCapacityCompanyForm").validate({
	        submitHandler:function(form){
	        	MtCompany.addCapacity();
	        }    
	    });
    })
</script>

