<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w800">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">新增合伙人</h4>
        </div>
        <form action="post" id="addPartner_form">
            <div class="modal-body">
                <!--新增客户 start-->
                <div class="modal_commonbody clearfix mt20">
                    <!--填写基本信息-->
                    <div class="step_info">
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="hehuoname">合伙人名称:</span>
                            <div class="form-group w220 pull-left">
                                <input name="partnerName" class="form-control input-sm" type="text" placeholder="请输入合伙人名称" required data-msg-required="请输入合伙人名称" minlength="2" data-msg-minlength="最少输入2个字符" >
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="partnerType">合伙人类型:</span>
                            <select class="form-control w220 pull-left input-sm" name="partnerType" required data-msg-required="请选择一个">
                                <option value="">请选择</option>
                                <c:forEach items="${partnerType.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </section>
                        <section class="col-md-12">
                            <span class="pull-left w100 text-right pr5" name="address">公司地址:</span>
                            <div class="form-group w220 pull-left">
                                <input name="partnerAddress" class="form-control input-sm w605" type="text" placeholder="请输入公司地址" required data-msg-required="请输入公司地址" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="fuzename">负责人姓名:</span>
                            <div class="form-group w220 pull-left">
                                <input name="managerName" class="form-control input-sm" type="text" placeholder="请输入负责人姓名"  required data-msg-required="请输入负责人姓名" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="fuzetel">负责人手机:</span>
                            <div class="form-group w220 pull-left">
                                <input name="managerPhone" class="form-control input-sm" type="text" placeholder="请输入负责人手机号码"  required data-msg-required="请输入负责人手机号码" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-mobile="true">
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="kaihuhang">开户行:</span>
                            <div class="form-group w220 pull-left">
                                <input name="collectionAccountType" class="form-control input-sm" type="text" placeholder="请输入开户行"  required data-msg-required="请输入开户行" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="yinhangname">银行账号:</span>
                            <div class="form-group w220 pull-left">
                                <input name="collectionAccount" class="form-control input-sm" type="text" placeholder="请输入银行账户"  required data-msg-required="请输入银行账号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="dengjihao">纳税登记号:</span>
                            <div class="form-group w220 pull-left">
                                <input name="taxCode" class="form-control input-sm" type="text" placeholder="请输入纳税人登记号" required data-msg-required="请输入纳说人登记号" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left w100 text-right pr5" for="email">电子邮箱:</span>
                            <div class="form-group w220 pull-left">
                                <input name="partnerEmail" class="form-control input-sm" type="email" placeholder="请输入电子邮箱"  required data-msg-required="请输入电子邮箱" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-email="true">
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

<script>
	// 页面加载完成后执行的方法
	$(function(){
       	$("#addPartner_form").validate({
	        submitHandler:function(form){
	        	addPartner();
	        }    
	    });
	});
</script>