<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w800">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h4 class="modal-title f16" id="">新增客户</h4>
		</div>
		<form id="updateCustomer_form">
			<div class="modal-body">
				<!--新增客户 start-->
				<div class="modal_commonbody clearfix mt20">
				<!--填写基本信息-->
				<div class="step_info">
						<input name="id" value="${customer.id }" type="hidden" >
						<section class="col-md-12 clearfix">
							<span class="pull-left w100 text-right mr10">客户模式:</span>
							<c:forEach items="${customerMode.dictionary}" var="tSysDictionary">
                        		<c:choose>
                        			<c:when test="${customer.customerMode == tSysDictionary.id}">
                        				<div class="form-group pull-left mr20">
											<input type="radio" name="customerMode" value="${tSysDictionary.id}" checked disabled="disabled"> <span>${tSysDictionary.dictionaryName}</span>
										</div>
                        			</c:when>
                        			<c:otherwise>
                        				<div class="form-group pull-left mr20">
											<input type="radio" name="customerMode" value="${tSysDictionary.id}" disabled="disabled"> <span>${tSysDictionary.dictionaryName}</span>
										</div>
                        			</c:otherwise>
                        		</c:choose>
                            </c:forEach>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">客户名称:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.customerName}" class="form-control input-sm" type="text" name="customerName" placeholder="请输入客户名称" required data-msg-required="请输入客户名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">客户类型:</span> 
							<select class="form-control w220 pull-left input-sm" name="customerType">
                           		<c:forEach items="${customerType.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${customer.customerType == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
							</select>
						</section>
						<section class="col-md-12">
							<span class="pull-left w100 text-right pr5">公司地址:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.customerAddress}" class="form-control input-sm w605" type="text" name="customerAddress" placeholder="请输入公司地址" required data-msg-required="请输入公司地址" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">负责人姓名:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.managerName}" class="form-control input-sm" type="text" name="managerName" placeholder="请输入负责人姓名"  required data-msg-required="请输入负责人姓名" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">负责人手机号:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.managerPhone}" class="form-control input-sm" type="text" name="managerPhone" placeholder="请输入负责人手机号"  required data-msg-required="请输入负责人手机号" data-rule-mobile="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">开户行:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.collectionAccountType}" class="form-control input-sm" type="text" name="collectionAccountType" placeholder="请输入开户行"  required data-msg-required="请输入开户行" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">银行账户:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.collectionAccount}" class="form-control input-sm" type="text" name="collectionAccount" placeholder="请输入银行账户"  required data-msg-required="请输入银行账号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">纳说登记号:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.taxCode}" class="form-control input-sm" type="text" name="taxCode" placeholder="请输入纳税登记号" required data-msg-required="请输入纳说登记号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
							</div>
						</section>
						<section class="col-md-6">
							<span class="pull-left w100 text-right pr5">电子邮箱:</span>
							<div class="form-group w220 pull-left">
								<input value="${customer.customerEmail}" class="form-control input-sm" name="customerEmail" type="email" placeholder="请输入电子邮箱"  required data-msg-required="请输入电子邮箱" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-mail="true">
							</div>
						</section>
				</div>
			</div>
		</div>
			<div class="modal-footer">
				<button id="editCustomer_form_button" type="submit" class="btn btn-success btn-lg2">提交</button>
				<button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
    $(function(){
	  $("#updateCustomer_form").validate({
	        submitHandler:function(form){
	        	updateCustomer();
	        }    
	    });
    })
</script>
