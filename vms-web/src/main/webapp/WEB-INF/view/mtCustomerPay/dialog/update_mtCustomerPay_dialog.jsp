<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>

<div class="modal-dialog w400">
    <form method="post" action="" id="updateAccount_form">
    	<input type="text" hidden="hidden" id="customerId" name="id" value="${customer.id}"/>
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">修改收款账号</h4>
            </div>
            <div class="modal-body">
                <!--新建收款账号 start-->
                <div class="modal_commonbody clearfix">

                    <div class="step_info">
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20" for="giroAccountType">收款渠道:</span>
                            <div class="form-group w220 pull-left">
                                <select class="form-control input-sm" name="giroAccountType" required data-msg-required="请选择一个">
                                    <option value="">请选择</option>
                                    <c:forEach items="${giroAccountType.dictionary}" var="tSysDictionary">
                                         <c:choose>
                                            <c:when test="${customer.giroAccountType == tSysDictionary.id}">
                                                <option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20" for="account">账号:</span>
                            <div class="form-group w220 pull-left">
                                <input value="${customer.giroAccount}" type="text" class="w220 form-control input-sm" name="giroAccount" placeholder="请输入账号" required data-msg-required="请输入账号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </div>
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20" for="name">真实姓名:</span>
                            <div class="form-group w220 pull-left">
                                <input value="${customer.giroAccountName}" type="text" class="w220 form-control input-sm" name="giroAccountName" placeholder="请输入真实姓名" required data-msg-required="请输入真实姓名" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </div>
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20">手机号:</span>
                            <div class="form-group w220 pull-left">
                                <span>${customer.managerPhone}</span>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20">验证码:</span>
                            <div class="form-group w220 pull-left">
                                <input id="yzm" type="text" class="pull-left w100 form-control input-sm mr15" placeholder="请输入验证码">
                                <button id="codeBtn" onclick="getSMSCodeNumber(${customer.id})" type="button" class="pull-left btn btn-default f12 btn-lg2">发送验证码</button>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2">完成</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </div>
    </form>
</div>

<script type="text/javascript">
    $(function(){
	  $("#updateAccount_form").validate({
	        submitHandler:function(form){
	        	updateCostomerAccount();
	        }    
	    });
    })
</script>