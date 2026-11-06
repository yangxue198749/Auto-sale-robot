var StVmText = function(){
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
	var submitAddStVmText = function(){
		var validateForm = $("#addStVmText-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	addStVmName: {
                    required: true
                },
                addStVmText:{
                	required: true
                },
            },
            messages: {
            	addStVmName: {
                    required: "请填写策略名称！"
                },
                addStVmText:{
                	required: "请填写文案！"
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
            	var btn = $("#addStVmText-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	$('#addStVmText').modal('hide') ;
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		searchEvent();
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
           
		});
	}
	var submitEditStVmText = function(){
		var validateForm = $("#editStVmText-form");
		validateForm.validate({
			doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
			errorElement: 'span', //default input error message container
			errorClass: 'help-block help-block-error', // default input error message class
			focusInvalid: false, // do not focus the last invalid input
			rules: {
				editStVmName: {
					required: true
				},
				editStVmText:{
					required: true
				},
			},
			messages: {
				editStVmName: {
					required: "请填写策略名称！"
				},
				editStVmText:{
					required: "请填写文案！"
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
				var btn = $("#editStVmText-form :submit");
				btn.button('loading');
				var options = {
						success:function (data){ 
							btn.button('reset');
							$('#editStVmText').modal('hide') ;
							layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
							if(data.type=="success"){
								btn.remove();
								searchEvent();
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
		addStVmText:function(){
			submitAddStVmText();
		},
		editStVmText:function(){
			submitEditStVmText();
		}
	}
}();