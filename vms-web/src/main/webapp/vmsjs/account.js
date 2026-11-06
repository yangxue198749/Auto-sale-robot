// 更新 用户名
function updateUserName(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/account/updateUserName.do",
		data:$('#changeUserName_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，请稍后重试",{shade: [0.5, '#fff'],time:2000});
		},
		success: function(data) {
			$('#changeUserName').modal('hide') ;
			if(data){
				$('#userName').val($('#newUserName').val());
				$('.headavatar_wp>a').text($('#newUserName').val()).append('<i class="iconfont f14 ml5 icon-xiala"></i>');
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000});
			}else{
				layer.msg("操作失败，请稍后重试",{shade: [0.5, '#fff'],time:2000});
			}
		}
	});
}

//更新 用户手机号码
function updateUserPhone(){
    if($('#newUserPhoneCode').val() == ''){
        layer.msg("请输入验证码",{shade: [0.5, '#fff'],time:2000});
        return false;
    }else {
        $.ajax({
            cache: false,
            type: "POST",
            url: "/account/updateUserPhone.do",
            data: $('#verifyChangePhone_form').serialize(),
            async: false,
            error: function (request) {
                layer.msg("系统异常，请稍后重试", {shade: [0.5, '#fff'], time: 2000});
            },
            success: function (data) {
                $('#verifyChangePhone').modal('hide');
                if (data != null && data.state) {
                    //$('#userPhone').html($('#newUserPhone').val());
                    layer.msg("修改成功！", {shade: [0.5, '#fff'], time: 2000});
                    location.reload();
                } else {
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                }
            }
        });
    }
}

// 更新用户密码
function updateUserPassword(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/account/updateUserPassword.do",
		data:$('#verifyChangePassword_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，请稍后重试",{shade: [0.5, '#fff'],time:2000});
		},
		success: function(data) {
			$('#verifyChangePassword').modal('hide') ;
			if(data){
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000});
				searchEvent();
			}else{
				layer.msg("操作失败，请稍后重试",{shade: [0.5, '#fff'],time:2000});
			}
		}
	});
}

/**
 * 获取验证码
 */
function getSMSCodeNumber(){
	$.post("passUserPhoneCode",function(data){
		if(data){
			layer.msg("验证码已发送",{shade: [0.5, '#fff'],time:2000});
            //resendSMS_60("codeBtn");
            sendSMS("#codeBtn","60");
		}
	});
}

/**
 * 获取验证码
 */
function getSMSCodeNumber2(){
	$.post("passUserPwdCode",function(data){
		if(data){
			layer.msg("验证码已发送",{shade: [0.5, '#fff'],time:2000});
            //resendSMS_60("codeBtn2");
            sendSMS("#codeBtn2","60");

		}
	});
}

function getSMSCodeNumber3(){
    var rulePhone = /^1[3|4|5|7|8]\d{9}$/;
    if($('#newUserPhone').val() == ''){
        layer.msg("请输入新手机号码",{shade: [0.5, '#fff'],time:2000});
        return false;
    }else if(!rulePhone.test($('#newUserPhone').val())){
        layer.msg("手机号码格式不对",{shade:[0.6,'#fff'],time:2000});
        return false;
    }else {
        $.ajax({
            cache: false,
            type: "POST",
            url: "/account/passNewUserPhoneCode.do",
            data: $('#verifyChangePhone_form').serialize(),
            async: false,
            error: function (request) {
                layer.msg("系统异常，请稍后重试", {shade: [0.5, '#fff'], time: 2000});
            },
            success: function (data) {
                if (data) {
                    layer.msg("验证码已发送", {shade: [0.5, '#fff'], time: 2000});
                    //resendSMS_60("codeBtn3");
                    sendSMS("#codeBtn3", "60");
                }
            }
        });
    }
}

function verifyCode(){
	//var b = false;
	if($('#oldUserPhone').val() == ''){
        layer.msg("请输入验证码",{shade: [0.5, '#fff'],time:2000});
        return false;
	}else{
        $.ajax({
            cache: false,
            type: "POST",
            url:"/account/verifyUserPhoneCode.do",
            data:$('#verifyChangePhone_form').serialize(),
            async: false,
            error: function(request) {
                layer.msg("系统异常，请稍后重试",{shade: [0.5, '#fff'],time:2000});
                $('#codeBtn').removeClass('disabled').text('获取验证码');
            },
            success: function(data) {
            	console.log(data.state);
                $('#codeBtn').removeClass('disabled').text('获取验证码');
                if(data != null && data.state){
                   // b = true;
                    $("#verifyChangePhone_form .step_info").hide().siblings("#verifyChangePhone_form .step_change").show();
                    console.log('通过'+data.state);
                }else{
                    console.log('else - :'+data.message);
                    layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                }
            }
        });
	}
    //return b;
}

function verifyCode2(){
	//var b = false;
    if($('#userPwdCode').val() == ''){
        layer.msg("请输入验证码",{shade: [0.5, '#fff'],time:2000});
        return false;
    }else {
        $.ajax({
            cache: false,
            type: "POST",
            url: "/account/verifyUserPwdCode.do",
            data: $('#verifyChangePassword_form').serialize(),
            async: false,
            error: function (request) {
                layer.msg("系统异常，请稍后重试", {shade: [0.5, '#fff'], time: 2000});
               // $('#codeBtn2').removeClass('disabled').text('获取验证码');
            },
            success: function (data) {
                console.log(data.state);
                $('#codeBtn2').removeClass('disabled').text('获取验证码');
                if (data != null && data.state) {
                    //b = true;
                    $("#verifyChangePassword_form .step_info").hide().siblings("#verifyChangePassword_form .step_change").show();
                    console.log('通过'+data.state);
                } else {
                    console.log('else - :'+data.message);
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                }
            }
        });
    }
	//return b;
}

/*
* 重新发送 倒计时
*/
/*var countdown = 60;
function resendSMS_60(id) {
    if (countdown == 0) {
        $("#"+ id).html("获取验证码").removeAttr("disabled");
        countdown = 60;
    } else {
        $("#"+ id).html("重新发送(" + countdown + ")").attr("disabled","disabled");
        countdown--;
        setTimeout(function() {
            resendSMS_60(id);
        },1000)
    }
}*/


/*
 * 重新发送 倒计时  new 20180327
 */
function sendSMS(btnID,countNum) {
    var _this = $(btnID);
    if (countNum == 0) {
        _this.html("获取验证码").removeAttr("disabled");
        countNum = 60;
    } else {
        _this.html("重新发送(" + countNum + ")").attr("disabled","disabled");
        countNum--;
        setTimeout(function() {
            sendSMS(btnID,countNum);
        },1000)
    }
}