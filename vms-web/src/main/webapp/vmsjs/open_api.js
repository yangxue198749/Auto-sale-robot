var openApi = function(){
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

    var submitAddOpenApi = function(){
        var validateForm = $("#add-open-api-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
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
                var btn = $("#add-open-api-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        if(data.type=="success"){
                            btn.remove();
                            $('#addOpenInterface').modal('hide') ;
                            //searchEvent();
                            location.reload();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }


    var submitEditOpenApi = function(){
        var validateForm = $("#edit-open-api-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
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
                var btn = $("#edit-open-api-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        if(data.type=="success"){
                            btn.remove();
                            $('#editOpenInterface').modal('hide') ;
                            //searchEvent();
                            location.reload();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }
    var bindSearchOpenApi = function(){

        $("#search-company-btn").bind("click", function(){
            //查询公司
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//		    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#openApiDiv").html(data);
//			    		Metronic.init() ;
                }
            };
            $("#search-openApi-form").ajaxSubmit(options);

        });
        $('#search-openApi-btn').bind('click',function(){
            var openApiCheck = $('#openApiDiv input[type="checkbox"]:checked');
            var companyId = openApiCheck.attr('data-company-id');
            if(openApiCheck.length == 0){
                layer.msg("请选择公司",{shade: [0.5, '#fff'],time:2000});
                return false;
            }else{
                $('#add-open-api-form .getCompanyName').html(openApiCheck.val()).attr('data-company-id',companyId);
                $('#companyId').val(companyId);
                $('#choiceCompany').modal('hide');
            }
        });
    }

    return{
        addOpenApi:function(){
            submitAddOpenApi();
        },
        selectCompany:function(){
            bindSearchOpenApi();
        },
        editOpenApi:function(){
            submitEditOpenApi();
        }
    }
}();