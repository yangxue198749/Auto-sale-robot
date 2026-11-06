var MtCompany = function(){
	var searchEvent = function(){
		var options = { 
	    		type: 'POST',
	    		beforeSubmit:function(){
	    			
	    		},
		        success:function (data)  { 
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
	var reset = function(){
		$('#resetButton').bind('click',function(){
			$("#search-form input:text").val("");
			$('.select2me').val(null).trigger("change");
			$('.select2me').val("请选择").trigger("change");
				
		});
	}
	var bindSearch = function(){
		$("#searchButton").bind("click", function(){
			searchEvent();
		});
	}
	
	
	
	var submitCompany = function(){
		if(!verifyAccount()){
			return ;
		}
		$.ajax({
			cache: false,
			type: "POST",
			url:"/company/create.do",
			data:$('#addCompanyForm').serialize(),
			async: false,
			error: function(request) {
				alert("Connection error");
			},
			success: function(data) {
				$('#addCompany').modal('hide') ;
				if(data != null && data.state){
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
					searchEvent();
				}else{
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				}
			}
		});
	}

	
	
	
	var submitEditCompany = function(){
		$.ajax({
			cache: false,
			type: "POST",
			url:"/company/update.do",
			data:$('#changeCompanyForm').serialize(),
			async: false,
			error: function(request) {
				alert("Connection error");
			},
			success: function(data) {
				$('#changeCompany').modal('hide') ;
				if(data != null && data != '' ){
					layer.msg("修改成功",{shade: [0.5, '#fff'],time:2000,});
					searchEvent();
				}else{
					layer.msg("修改失败",{shade: [0.5, '#fff'],time:2000,});
				}
			}
		});
	}
	
	var upgradeCompany = function(){
		$.ajax({
			cache: false,
			type: "POST",
			url:"/company/upgrade",
			data:$('#upgradeCompanyForm').serialize(),
			async: false,
			error: function(request) {
				alert("Connection error");
			},
			success: function(data) {
				$('#upgradeCompany').modal('hide') ;
				if(data != null && data.state){
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
					searchEvent();
					$("#upgradeCompanyButton").css("display","none");
				}else{
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				}
			}
		});
	}

	var addCapacity = function() {
		$.ajax({
			cache : false,
			type : "POST",
			url : "/company/addCapacity",
			data : $('#addCapacityCompanyForm').serialize(),
			async : false,
			error : function(request) {
				alert("Connection error");
			},
			success : function(data) {
				$('#addCapacity').modal('hide') ;
				if (data) {
					layer.msg("分配售货机成功！", {
						shade : [ 0.5, '#fff' ],
						time : 2000,
					});
					searchEvent();
				} else {
					layer.msg("失败", {
						shade : [ 0.5, '#fff' ],
						time : 2000,
					});
				}
			}
		});
	}

	var exportOrUnStock = function(){
		$('#exportOrUnStock').bind("click",function(){
			var areaArray = new Array();
			$("input:checkbox[name=companyCheckbox]:checked").each(function() {
				areaArray.push($(this).val());
			})
			if(areaArray.length == 0){
				layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(areaArray.length != 1){
				layer.msg("请选择一条数据",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}

			$.ajax({
				cache: false,
				type: "POST",
				url:"/company/exportOrUnStock.do",
				data:{"ids":areaArray[0]},
				async: false,
				error: function(request) {

				},
				success: function(data) {
					if("success" == data.type){
						layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
						searchEvent();
					}else{
						layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
					}
				}
			});
		});
	}

	return{
		init:function(){
			bindSearch();
			reset();
			exportOrUnStock();
		},
		addCompany:function(){
			submitCompany();
		},
		editCompany:function(){
			submitEditCompany();
		},
		upgradeCompany:function(){
			upgradeCompany();
		},
		addCapacity:function(){
			addCapacity();
		}
	}
}();


function verifyAccount(){
	var b = false;
	var val = $('#adminAccount').val();
	$.ajax({
		cache: false,
		type: "POST",
		url:"/company/verifyAccount.do",
		data:{"adminAccount":val},
		async: false,
		error: function(request) {
			alert("Connection error");
		},
		success: function(data) {
			if(data){
				b = true;
			}else{
				layer.msg("登录账号已被注册",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
	return b;
}