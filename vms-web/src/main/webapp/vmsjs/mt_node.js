var MtNode = function(){
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
    var bindSearchDelivery = function(){
        $("#delivery-search-button").bind("click", function(){
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#setDelivery").html(data);
//		    		Metronic.init() ;
                }
            };
            $("#delivery-form-search").ajaxSubmit(options);
        });
    }
    var bindSearchRepair = function(){
        $("#repair-search-button").bind("click", function(){
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#setRepair").html(data);
//		    		Metronic.init() ;
                }
            };
            $("#repair-form-search").ajaxSubmit(options);
        });
    }
    var submitMtNode = function(){
        var validateForm = $("#strategy-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                nodeName1: {
                    required: true
                }
            },
            messages: {
                nodeName1: {
                    required: "请填写点位名称！"
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
                var btn = $("#strategy-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        $('#addNode').modal('hide');
                        if(data.code=="0"){
                        	btn.remove();
                            searchEvent();
                        }
                    },
                    error:function () {
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }
    var submitDelivery = function(){
        var validateForm = $("#delivery-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            },
            messages: {
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
                var did=$("#delivery-form").find("input[type=checkbox]:checked").val();
                if(did==undefined){
                    layer.msg("请选择配送员",{shade: [0.5, '#fff'],time:1000,});
                }
                else{
                var btn = $("#delivery-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#setDelivery').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        if(data.code=="0"){
                        	btn.remove();
                            searchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }
            }
        });
    }

    var submitRepair = function(){

        var validateForm = $("#repair-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            },
            messages: {
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
                var did=$("#repair-form").find("input[type=checkbox]:checked").val();
                if(did==undefined){
                    layer.msg("请选择维修员",{shade: [0.5, '#fff'],time:1000,});
                }
                else{
                var btn = $("#repair-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#setRepair').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        if(data.code=="0"){
                        	btn.remove();
                            searchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }
            }
        });
    }


    return{
        init:function(){
            bindSearch();
            reset();
        },
        addMtNode:function(){
            submitMtNode();
        },
        addDelivery:function(){
            submitDelivery();
            bindSearchDelivery();
        },
        addRepair:function(){
            submitRepair();
            bindSearchRepair();
        }
    }
}();