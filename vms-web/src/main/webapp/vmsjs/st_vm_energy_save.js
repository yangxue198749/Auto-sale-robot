var StVmEnergySave = function(){
	var searchEvent = function(){
		var options = { 
	    		type: 'POST',
	    		beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
	    		},
		        success:function (data)  { 
		        	var hasLogin = Metronic.hasLogin();
		        	if(hasLogin==false){
		        		window.location.href="/";
		        		return;
		        	}
		    		$("#pagediv").html(data);
//		    		Metronic.init() ;
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
	var submitNextBtn = function(){
		$('#submitAddStVmBtn').bind('click',function(){
			var temperatureHandlerMode = $("#addVmModelId option:selected").attr("data-temperature-handler-mode");
			var screenHave = $("#addVmModelId option:selected").attr("data-screen-have");
			if(
				(null == temperatureHandlerMode || "" == temperatureHandlerMode || "undefined" == temperatureHandlerMode || undefined == temperatureHandlerMode || 50 == temperatureHandlerMode)
				&&
				(null == screenHave || "" == screenHave || "undefined" == screenHave || undefined == screenHave || 2 == screenHave )
			){
				layer.msg('当前机型无法设置!',{shade: [0.5, '#fff'],time:2000});
				return false;
			}
			var url = $(this).attr('href');
			var addStVmName = $('#addStVmName').val();
			var addStVmDesc = $('#addStVmDesc').val();
			var addVmModelId = $('#addVmModelId').val();
			$.ajax({
			    type : 'post',
			    url : '/stVmEnergySave/saveAndNext',
			    data : {"addStVmName":addStVmName, "addVmModelId":addVmModelId,"addStVmDesc":addStVmDesc},
			    async : true,
			    dataType : 'json',
			    success : function(data) {
			    	layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
			    	$('#addEnergySaveDetailDiv').html('');
			    	if(data.type=='success'){
			    		$('#addenergy').modal('hide');
			    		searchEvent();
			    		$(".addEnergyDialogBtn").attr("href","/stVmEnergySave/addEnergyDialog"+"?strategyId="+data.strategyId);
			    	}else{
			    		$('#addNextEnergy').modal('hide');
			    	}
			    }
			});
		});
	}
	var submitAddEnergySave = function(){
		var validateForm = $("#addEnergySave-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	addEnergySaveType: {
                    required: true
                },
                addStartTime:{
                	required: true
                },
                addEndTime:{
                	required: true
                },
				addTargetValue:{
					digits:true
				}
            },
            messages: {
            	addEnergySaveType: {
                    required: "请选择节能类型！"
                },
                addStartTime:{
                	required: "请选择开始时间！"
                },
                addEndTime:{
                	required: "请选择结束时间！"
                },
			},
            errorPlacement: function (error, element) { // render error placement for each input type
                // error.insertAfter(element); // for other inputs, just perform default behavior
            },
            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
            },
            unhighlight: function (element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error'); // set error class to the control group
            },
            success: function (label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
            },
            submitHandler: function (form) {
            	var btn = $("#addEnergySave-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	$('#energy').modal('hide') ;
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		$('#addEnergySaveDetailDiv').html(data.html);
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
           
		});
	}
	var submitAddEditEnergySave = function(){
		var validateForm = $("#addEditEnergySave-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	addEnergySaveType: {
                    required: true
                },
                addStartTime:{
                	required: true
                },
                addEndTime:{
                	required: true
                },
				addTargetValue:{
					digits:true
				}
            },
            messages: {
            	addEnergySaveType: {
                    required: "请选择节能类型！"
                },
                addStartTime:{
                	required: "请选择开始时间！"
                },
                addEndTime:{
                	required: "请选择结束时间！"
                },
			},
            errorPlacement: function (error, element) { // render error placement for each input type
                // error.insertAfter(element); // for other inputs, just perform default behavior
            },
            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
            },
            unhighlight: function (element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error'); // set error class to the control group
            },
            success: function (label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
            },
            submitHandler: function (form) {
            	var btn = $("#addEditEnergySave-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	$('#add-editEnergySave').modal('hide') ;
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		$('#addEnergySaveDetailDiv').html(data.html);
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
           
		});
	} 
	var submitEditAddEnergySave = function(){
		var validateForm = $("#editAddEnergySave-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	addEnergySaveType: {
                    required: true
                },
                addStartTime:{
                	required: true
                },
                addEndTime:{
                	required: true
                },
				addTargetValue:{
					digits:true
				}
            },
            messages: {
            	addEnergySaveType: {
                    required: "请选择节能类型！"
                },
                addStartTime:{
                	required: "请选择开始时间！"
                },
                addEndTime:{
                	required: "请选择结束时间！"
                },
				addTargetValue:{
					digits:"必须为整数"
				}
			},
            errorPlacement: function (error, element) { // render error placement for each input type
                // error.insertAfter(element); // for other inputs, just perform default behavior
            },
            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
            },
            unhighlight: function (element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error'); // set error class to the control group
            },
            success: function (label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
            },
            submitHandler: function (form) {
            	var btn = $("#editAddEnergySave-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		$('#editAddEnergySave').modal('hide');
				        		$('#editEnergySaveDetailDiv').html(data.html);
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
		});
	}
	var submitEditEditEnergySave = function(){
		var validateForm = $("#editEditEnergySave-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	addEnergySaveType: {
                    required: true
                },
                addStartTime:{
                	required: true
                },
                addEndTime:{
                	required: true
                },
				addTargetValue:{
					digits:true
				}
            },
            messages: {
            	addEnergySaveType: {
                    required: "请选择节能类型！"
                },
                addStartTime:{
                	required: "请选择开始时间！"
                },
                addEndTime:{
                	required: "请选择结束时间！"
                },
			},
            errorPlacement: function (error, element) { // render error placement for each input type
                // error.insertAfter(element); // for other inputs, just perform default behavior
            },
            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
            },
            unhighlight: function (element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error'); // set error class to the control group
            },
            success: function (label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
            },
            submitHandler: function (form) {
            	var btn = $("#editEditEnergySave-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		$('#editEditEnergySave').modal('hide');
				        		$('#editEnergySaveDetailDiv').html(data.html);
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
           
		});
	}
	return{
		init:function(){
			bindSearch();
			reset();
		},
		nextBtn:function(){
			submitNextBtn();
		},
		addEnergySave:function(){
			submitAddEnergySave();
		},
		addEditEnergySave:function(){
			submitAddEditEnergySave();
		},
		editAddEnergySave:function(){
			submitEditAddEnergySave();
		},
		editEditEnergySave:function(){
			submitEditEditEnergySave();
		}
	}
}();