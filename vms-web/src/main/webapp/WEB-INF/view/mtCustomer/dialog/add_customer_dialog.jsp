<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w800">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title f16">新增客户</h4>
		</div>
		<form id="addCustomer_form">
			<div class="modal-body">
				<!--新增客户 start-->
				<div class="modal_commonbody clearfix mt20">
					<!--填写基本信息-->
					<div class="step_info">
							<section class="col-md-12 clearfix"> 
								<span class="pull-left w100 text-right mr10" for="customerMode" >客户模式:</span>
								<c:forEach items="${customerMode.dictionary}" var="tSysDictionary" varStatus="i">
									<div class="form-group pull-left mr20">
										<c:choose>
											<c:when test="${i.first}">
												<input type="radio" name="customerMode" value="${tSysDictionary.id}" checked> <span>${tSysDictionary.dictionaryName}</span>
											</c:when>
											<c:otherwise>
												<input type="radio" name="customerMode" value="${tSysDictionary.id}"> <span>${tSysDictionary.dictionaryName}</span>
											</c:otherwise>
										</c:choose>
									</div>
                                </c:forEach>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="customerName" >客户名称:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm" type="text" name="customerName" placeholder="请输入客户名称" required data-msg-required="请输入客户名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
								</div>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="customerType" >客户类型:</span>
								<div class="form-group w220 pull-left">
									<select class="form-control w220 input-sm" name="customerType" required data-msg-required="请选择一个">
											<option value="">请选择</option>
											<c:forEach items="${customerType.dictionary}" var="tSysDictionary">
												<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
											</c:forEach>
									</select>
								</div>
							</section>
							<section class="col-md-12">
								<span class="pull-left w100 text-right pr5" for="customerAddress" >公司地址:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm w605" type="text" name="customerAddress" placeholder="请输入公司地址" required data-msg-required="请输入公司地址" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
								</div>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="managerName" >负责人姓名:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm" type="text" name="managerName" placeholder="请输入负责人姓名"  required data-msg-required="请输入负责人姓名" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
								</div>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="managerPhone" >负责人手机号:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm" type="text" name="managerPhone" placeholder="请输入负责人手机号"  required data-msg-required="请输入负责人手机号" data-rule-mobile="true">
								</div>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="collectionAccountType" >开户行:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm" type="text" name="collectionAccountType" placeholder="请输入开户行"  required data-msg-required="请输入开户行" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
								</div>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="collectionAccount" >银行账户:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm" type="text" name="collectionAccount" placeholder="请输入银行账户"  required data-msg-required="请输入银行账号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
								</div>
							</section>
							<section class="col-md-6">
								<span class="pull-left w100 text-right pr5" for="taxCode" >纳说登记号:</span>
								<div class="form-group w220 pull-left">
									<input class="form-control input-sm" type="text" name="taxCode" placeholder="请输入纳税登记号" required data-msg-required="请输入纳说登记号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
								</div>
							</section>
							<section class="col-md-6">
							<span class="pull-left w100 text-right pr5" for="customerEmail" >电子邮箱:</span>
							<div class="form-group w220 pull-left">
								<input class="form-control input-sm" name="customerEmail" type="email" placeholder="请输入电子邮箱"  required data-msg-required="请输入电子邮箱" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-mail="true">
							</div>
						</section>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="submit" class="btn btn-success btn-lg2">提交</button>
				<button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
    $(function(){
	  $("#addCustomer_form").validate({
	        submitHandler:function(form){
	        	addCustomer();
	        }    
	    });
    })
</script>
