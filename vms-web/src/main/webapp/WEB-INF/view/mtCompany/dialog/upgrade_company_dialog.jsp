<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w400">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h4 class="modal-title" id="">升级公司</h4>
		</div>
		<form id="upgradeCompanyForm" action=""
			method="post">
			<div class="modal-body">
				<div class="modal_commonbody clearfix">
					<section class="col-md-12">
						<span for="companyName">公司名称:</span>
						<div class="form-group">
							<input class="form-control input-sm" name="companyName"
								type="text" placeholder="请输入公司名称" required
								data-msg-required="请输入公司名称" minlength="2"
								data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
						</div>
					</section>
                          <section class="col-md-12">
                              <span for="newAdminUserId">指定原公司新管理员:</span>
                              <div class="form-group">
                                  <select class="form-control input-sm" name="newAdminUserId" required data-msg-required="请选择一个新管理员">
                                      <option value="">请选择</option>
                                      <c:forEach items="${userList}" var="user">
                                      	<option value="${user.id}">${user.userName}</option>
                                      </c:forEach>
                                  </select>
                              </div>
                          </section>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="submit" class="btn btn-success commit">确定</button>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	$(function() {
		$("#upgradeCompanyForm").validate({
	        submitHandler:function(form){
	        	MtCompany.upgradeCompany();
	        }    
	    });
		//MtCompany.editCompany();
	})
</script>
