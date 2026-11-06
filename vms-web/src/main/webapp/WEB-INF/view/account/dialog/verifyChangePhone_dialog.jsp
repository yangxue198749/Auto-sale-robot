<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div class="modal-dialog w400">
    <form method="post" id="verifyChangePhone_form">
        <div class="modal-content">

            <div class="step_info">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16">验证身份</h4>
                </div>
                <div class="modal-body">
                    <div class="modal_commonbody clearfix" >
                        <p>为了保证你的帐号安全，请验证身份。验证成功后进行下一步操作。</p>
                        <section class="col-md-12">
                            <div class="mt20">
                                <span class="pull-left w60 text-right mr10">手机号码:</span>
                                <span>${user.userPhone}</span>
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span class="pull-left w60 text-right mr10" for="yzm">验证码:</span>
                            <div class="form-group pull-left">
                                <input name="userPhoneCode" id="oldUserPhone" class="mr10 form-control input-sm w120" type="text" placeholder="请输入验证码" data-msg-required="请输入验证码" minlength="4" required data-msg-minlength="最少输入4个字符" data-rule-Au9="true">
                            </div>
                            <%--<button id="codeBtn" onclick="getSMSCodeNumber()" type="button" class="pull-left btn btn-default f12 btn-lg2 mr10">获取验证码</button>--%>
                            <button id="codeBtn" type="button" class="pull-left btn btn-default f12 btn-lg2 mr10">获取验证码</button>
                        </section>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2 nextbtn">下一步</button>
                    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                </div>
            </div>


            <div class="step_change" style="display: none">
            	<div class="modal-header">
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				    <h4 class="modal-title f16">修改手机号码</h4>
				</div>
				<div class="modal-body">
				    <div class="modal_commonbody clearfix" >
				        <section class="col-md-12">
				            <span class="pull-left w80 text-right mr10" for="newTel">新手机号码:</span>
				            <div class="form-group pull-left">
				                <input id="newUserPhone" class="mr10 form-control input-sm w120" type="tel" name="newUserPhone" placeholder="请输入新手机号" required data-msg-required="请输入新手机号" minlength="11" data-msg-minlength="最少输入11个字符" data-rule-Au9="true">
				            </div>
				        </section>
				        <section class="col-md-12">
				            <span class="pull-left w80 text-right mr10" for="yzm">验证码:</span>
				            <div class="form-group pull-left">
				                <input class="form-control input-sm w120 mr10" id="newUserPhoneCode" name="newUserPhoneCode" type="text" placeholder="请输入验证码" required data-msg-required="请输入验证码" minlength="4" data-msg-minlength="最少输入4个字符" data-rule-Au9="true">
				            </div>
				            <%--<button id="codeBtn3" onclick="getSMSCodeNumber3()" type="button" class="pull-left btn btn-default f12 btn-lg2 m80">获取验证码</button>--%>
                            <button id="codeBtn3" type="button" class="pull-left btn btn-default f12 btn-lg2 m80">获取验证码</button>
				        </section>
				    </div>
				</div>
				<div class="modal-footer">
				    <button type="submit" class="btn btn-success btn-lg2 nextbtn">确定</button>
				    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
				</div>
            </div>


        </div>

    </form>
</div>

<script>
	
	// 页面加载完成后执行的方法
	$(function(){
/*		$("#verifyChangePhone_form .step_info .nextbtn").click(function(){
			if(verifyCode() == true){
			    console.log(verifyCode());
		      	$(this).parents(".step_info").hide().siblings(".step_change").show();
			}
	   	});*/

        $("#verifyChangePhone_form .step_info .nextbtn").click(function(){
            verifyCode();
            return false;
        });
	
       	$("#verifyChangePhone_form").validate({
	        submitHandler:function(form){
	        	updateUserPhone();
	        }    
	    });

       	// 验证身份 原手机的验证码
        $("#codeBtn").click(function(){
            getSMSCodeNumber();
            return false;
        });

        // 验证身份 新手机的验证码
        $("#codeBtn3").click(function(){
            getSMSCodeNumber3();
            return false;
        });
	});

</script>
