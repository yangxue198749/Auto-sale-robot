$(function() {
	$("#searchButton").bind("click", function() {
		searchEvent();
	});
	
	$("#deleteButton").bind("click", function() {
		var customerIds = new Array();
		$('input:checkbox[name=customerIds]:checked').each(function(i){
			customerIds.push($(this).val());
		});
		if(customerIds.length == 0){
			return ;
		}
		$.ajax({
            cache: false,
            type: "POST",
            url:"/customer/delete.do",
            data:{"customerIds":customerIds.join()},
            async: false,
            error: function(request) {
            	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
            },
            success: function(data) {
            	if(data){
            		layer.msg("删除成功！",{shade: [0.5, '#fff'],time:2000,});
            		searchEvent();
            	}else{
            		layer.msg("失败,关联过公司的客户不能删除",{shade: [0.5, '#fff'],time:2000,});
            	}
            }
        });
	});
    
})

//查询
function searchEvent() {
	var options = {
		type : 'POST',
		beforeSubmit : function() {},
		success : function(data) {
			var hasLogin = Metronic.hasLogin();
        	if(hasLogin==false){
        		window.location.href="/";
        		return;
        	}
			$("#pagediv").html(data);
		}
	};
	$("#search-form").ajaxSubmit(options);
}


/**
 * 创建客户
 */
function addCustomer(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/customer/create.do",
		data:$('#addCustomer_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#addCustomer').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

/**
 * 修改客户
 */
function updateCustomer(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/customer/update.do",
		data:$('#updateCustomer_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#updateCustomer').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}


function reset(){
	$('#search-form').reset();
}


/**
 * 获取验证码
 */
function getSMSCodeNumber(id){
	$.post("/customerAccount/passSMS.do",{"customerId":id},function(data){
		if(data){
			layer.msg("验证码已发送",{shade: [0.5, '#fff'],time:2000,});
			//$("#codeBtn").html("重新获取");
            resendSMS_60("codeBtn");
		}
	});
}

function verificationCode(codeNumber , customerId){
	var b =false;
	$.ajax({
		cache: false,
		type: "POST",
		url:"/customerAccount/verificationCode.do",
		data:{"codeNumber":codeNumber ,"customerId":customerId},
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			if(data.state){
				b= true;
			}else{
				layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
	return b;
}

function updateCostomerAccount(){
	if(verificationCode($('#yzm').val() ,$('#customerId').val() ) != true){
		return ;
	}
	$.ajax({
		cache: false,
		type: "POST",
		url:"/customerAccount/update.do",
		data:$('#updateAccount_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#updateAccount').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
				refresh();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function refresh(){
	$.post("/customerAccount/list.do",function(data){
		if(data){
			$("#pagediv").html(data);
		}
	});
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










