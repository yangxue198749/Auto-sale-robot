$(function(){
    //layer 初始化
    layer.config({ shadeClose: true });
	getCode();
	$('#qrcode').click(function(){
		getCode();
	});
	//vmsLogin
	$("#vmsLogin .login_btn").click(function(){
		RegLogin();
	})
});

//验证
function RegLogin(){
	var parameter ={};
	parameter.userName=$("#vmsLogin .login_account").val();
	parameter.password=$("#vmsLogin .login_password").val();
	parameter.validCode=$("#vmsLogin .login_code").val();
	//验证
	if(parameter.userName == ""){
		layer.msg("请输入您的登录账号",{shade: [0.3,'#fff'],time:2000});
		return false;
	}
	if(parameter.password == ""){
		layer.msg("请输入您的登录密码",{shade: [0.3,'#fff'],time:2000});
		return false;
	}
	if(parameter.validCode == ""){
		layer.msg("请输入验证码",{shade: [0.3,'#fff'],time:2000});
		return false;
	}
	else{
		$.post("submitLogin" ,parameter ,function(data){
			if(data != null){
				if(data.code == 1){//登陆成功
					location.href =data.href;
				}else if(data.code == 2){//初次登陆
					location.href =data.href;
//						updateUserPwd();
				}else{//出问题
					getCode();
					//alert(data.message);
					layer.msg(data.message,{shade: [0.3,'#fff'],time:2000});
				}
			}
		})
	}
}
//处理注册、登录页面底部信息，置于最底部
$(function(){
    lrFixFooter("#BtmGroup");
    function lrFixFooter(obj){
        var footer = $(obj),doc = $(document);
        function fixFooter(){
            if(doc.height()-4 <= $(window).height()){
                footer.css({
                    width:"100%",
                    position:"absolute",
                    left:0,
                    bottom:0
                });
            }else{
                footer.css({position:"static"});
            }
        }
        fixFooter();
        $(window).on('resize.footer', function(){
            fixFooter();
        });
        $(window).on('scroll.footer',function(){
            fixFooter();
        });
    }
    //touch
    $('#touchGroup').on('mousemove', function(e){
        var offset = $('#touchGroup').offset();
        var x = e.pageX - offset.left;
        var y = e.pageY - offset.top;
        var centerX = $('#touchGroup').outerWidth() /2;
        var centerY = $('#touchGroup').outerHeight() /2;
        var deltaX = x - centerX;
        var deltaY = y - centerY;
        var percentX = deltaX / centerX;
        var percentY = deltaY / centerY;
        var deg = 10;
        $('#banner').css({
            transform: 'rotateX('+deg*-percentY + 'deg)'+
            ' rotateY('+deg*percentX+'deg)'
        });
    });
    $('#touchGroup').on('mouseleave', function(){
        $('#banner').css({
            transform: ''
        });
    });
});
/*
 * 获取验证码
 */
function getSMSCodeNumber(){
	$.post("passSMS",function(data){
		if(data){
			layer.msg("验证码已发送",{shade: [0.3,'#fff'],time:2000});
			//$("#codeBtn").html("重新获取");
            resendSMS_60("codeBtn");
		}
	});
}
/*
 * 修改密码
 */
function restUserPwd(){
	$.post("setPayAccount",$('#RestForm').serialize(),function(data){
		if(data.state){
			location.href =data.href;
		}else{
			layer.msg(data.message,{shade: [0.3,'#fff'],time:2000});
		}
	})
}
//验证码
function getCode(){
	$("#qrcode").attr("src","/createCode?rand=" +new Date().getMilliseconds());
}
/*
 * 重新发送 倒计时
 */
var countdown = 60;
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
}