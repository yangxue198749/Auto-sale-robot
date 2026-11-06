<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- <%@ page isELIgnored ="false" %> --%>

<div class="modal-dialog">
	<form method="post" name="form" id="addCompanyForm"
		action="/company/create">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title f16" id="">新建公司</h4>
			</div>

			<div class="modal-body">
				<!--新建公司 start-->
				<div class="modal_commonbody clearfix">
					<!--填写基本信息-->
					<div class="step_info">
						<section class="col-md-12">
							<span class="w100" for="companyName">公司名称:</span>
							<div class="form-group">
								<input class="form-control input-sm" name="companyName"
									type="text" placeholder="请输入公司名称" required
									data-msg-required="请输入公司名称" minlength="2"
									data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="w100" for="customerId">所属客户:</span>
							<div class="form-group">
								<c:choose>
									<c:when test="${customerName != null}">
										<div class="form-control input-sm">${customerName}</div>
									</c:when>
									<c:otherwise>
										<select class="form-control input-sm" name="customerId" required
											data-msg-required="请选择客户">
											<option value="">请选择</option>
											<c:forEach items="${customerList }" var="customer">
												<option value="${customer.id }">${customer.customerName }</option>
											</c:forEach>
										</select>
										
									</c:otherwise>
								</c:choose>
							</div>
						</section>
						<section class="col-md-6">
							<span class="w100" for="adminName">管理员姓名:</span>
							<div class="form-group">
								<input class="form-control input-sm" name="adminName"
									type="text" placeholder="请输入管理员姓名" required
									data-msg-required="请输入管理员姓名" minlength="2"
									data-msg-minlength="最少输入2个字符" maxlength="10" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="w100" for="adminAccount">登录账户:</span>
							<div class="form-group">
								<input id="adminAccount" onchange='verifyAccount()' class="form-control input-sm" name="adminAccount"
									type="text" placeholder="请输入登录账户" required data-rule-A9="true" maxlength="20"
									data-msg-required="请输入登录账户" >
							</div>
						</section>
						<section class="col-md-6">
							<span class="w100" for="userPhone">手机号码:</span>
							<div class="form-group">
								<input class="form-control input-sm" name="userPhone"
									type="text" placeholder="请输入手机号码" required
									data-msg-required="请输入手机号码" data-rule-mobile="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="w100" for="role">所属角色:</span>
							<div class="form-group">
								<div class="form-control input-sm">管理员</div>
							</div>
						</section>
						<section class="col-md-6">
							<span class="w100" for="maxVmCapacity">售货机数量:</span>
							<div class="form-group">
								<input class="form-control input-sm" name="maxVmCapacity"
									type="text" placeholder="请输入售货机数量" required
									data-msg-required="请输入售货机数量" digits data-msg-digits="售货机数量必须为整数数字" 
									max="${maxSum }" data-msg-max="售货机数量不能大于${maxSum }" min="0" data-msg-max="售货机数量不能小于0">
							</div>
						</section>


					</div>
				</div>

			</div>

			<div class="modal-footer">
				<button type="submit" class="btn btn-success btn-lg2">完成</button>
				<button type="button" class="btn btn-default btn-lg2"
					data-dismiss="modal">取消</button>
			</div>
		</div>
	</form>
</div>

 <script type="text/javascript">
    $(function(){
    	$("#addCompanyForm").validate({
	        submitHandler:function(form){
	        	MtCompany.addCompany();
	        }    
	    });
    	//MtCompany.addCompany();
    })
</script>