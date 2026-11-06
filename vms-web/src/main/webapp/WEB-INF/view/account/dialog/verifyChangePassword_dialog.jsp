<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w400">
    <form method="post" action="" name="form" id="verifyChangePassword_form">
        <div class="modal-content">
                <div class="step_info">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">验证身份</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix" >
                            <p>为了保证你的帐号安全，请验证身份。验证成功后进行下一步操作。</p>
                            <section>
                                <div class="mt20">
                                    <span class="pull-left w60 text-right mr10">手机号码:</span>
                                    <span>${user.userPhone}</span>
                                </div>
                                <div class="mt10">
                                    <span class="pull-left w60 text-right mr10" for="yzm">验证码:</span>
                                    <div class="form-group pull-left">
                                        <input class="mr10 form-control input-sm w160" type="text" id="userPwdCode" name="userPwdCode" placeholder="请输入验证码" required data-msg-required="请输入验证码" minlength="4" data-msg-minlength="最少输入4个字符" data-rule-Au9="true">
                                    </div>
                                    <%--<button id="codeBtn2" onclick="getSMSCodeNumber2()" type="button" class="pull-left btn btn-default f12 btn-lg2 mr10">获取验证码</button>--%>
                                    <button id="codeBtn2" type="button" class="pull-left btn btn-default f12 btn-lg2 mr10">获取验证码</button>
                                </div>
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
                    <h4 class="modal-title f16">修改密码</h4>
                </div>
                <div class="modal-body">
                    <div class="modal_commonbody clearfix" >
                        <section class="col-md-12">
                                <span for="newPassword">新密码:</span>
                                <div class="form-group">
                                    <input id="newPassword" class="form-control input-sm" type="password" name="newPassword" placeholder="请输入新密码" required data-msg-required="请输入密码" data-rule-pda9="true">
                                </div>

                        </section>
                        <section class="col-md-12">
                                <span for="pwd">再次输入:</span>
                                <div class="form-group">
                                    <input class="form-control input-sm" type="password" name="pwd" placeholder="请再次输入密码" required data-msg-required="请再次输入密码" data-rule-pda9="true" equalTo="#newPassword" data-msg-equalTo="两次密码不一致">
                                </div>
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
		$("#verifyChangePassword_form .step_info .nextbtn").click(function(){
			/*if(verifyCode2()){
		      	$(this).parents(".step_info").hide().siblings(".step_change").show();
			}*/
            verifyCode2();
            return false;
	   	});
		
       	$("#verifyChangePassword_form").validate({
	        submitHandler:function(form){
	        	updateUserPassword();
	        }    
	    });

        // 验证身份 手机验证码
        $("#codeBtn2").click(function(){
            getSMSCodeNumber2();
            return false;
        });

	});

</script>
