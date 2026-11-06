var Sim = function() {
    var searchEvent = function() {
        var options = {
            type: 'POST',
            beforeSubmit: function() {
                // Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function(data) {
            	var hasLogin = Metronic.hasLogin();
	        	if(hasLogin==false){
	        		window.location.href="/";
	        		return;
	        	}
                $("#pagediv").html(data);
                // Metronic.init() ;
            }
        };
        $("#search-form").ajaxSubmit(options);
    }
    var reset = function() {
        $('#resetButton').bind('click', function() {
            $("#search-form input:text").val("");
            $('.select2me').val(null).trigger("change");
            $('.select2me').val("请选择").trigger("change");
        });
    }
    var bindSearch = function() {
        $("#searchButton").bind("click", function() {
            searchEvent();
        });
    }
    var submitAddSim = function() {
        var validateForm = $("#addSim-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                addSimPhone: {
                    required: true,
                    digits: true,
                    rangelength: [11, 11]
                },
                addSimNumber: {
                    required: true
                },
                addServiceProvider: {
                    required: true
                }
            },
            messages: {
                addSimPhone: {
                    required: "请填写SIM卡号码！",
                    digits: "SIM卡号码只能输入数字！",
                    rangelength: "SIM卡号码长度11位"
                },
                addSimNumber: {
                    required: "请填写SIM卡编号！"
                },
                addServiceProvider: {
                    required: "请选择运营商！"
                }
            },
            errorPlacement: function(error, element) { // render error placement for each input type
                // error.insertAfter(element); // for other inputs, just perform default behavior
            },
            highlight: function(element) { // hightlight error inputs
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
            },
            unhighlight: function(element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error'); // set error class to the control group
            },
            success: function(label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
            },
            submitHandler: function(form) {
                var btn = $("#addSim-form :submit");
                btn.button('loading');
                var options = {
                    success: function(data) {
                        btn.button('reset');
                        $('#addSIM').modal('hide');
                        layer.msg(data.message, {
                            shade: [0.5, '#fff'],
                            time: 2000,
                        });
                        if (data.type == "success") {
                        	btn.remove();
                            searchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }
		var submitEditSim = function() {
        var validateForm = $("#editSim-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                editSimPhone: {
                    required: true,
                    digits: true,
                    rangelength: [11, 11]
                },
                editSimNumber: {
                    required: true
                },
                editServiceProvider: {
                    required: true
                }
            },
            messages: {
                editSimPhone: {
                    required: "请填写SIM卡号码！",
                    digits: "SIM卡号码只能输入数字！",
                    rangelength: "SIM卡号码长度11位"
                },
                editSimNumber: {
                    required: "请填写SIM卡编号！"
                },
                editServiceProvider: {
                    required: "请选择运营商！"
                }
            },
            errorPlacement: function(error, element) {
            },
            highlight: function(element) {
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
            },
            unhighlight: function(element) {
                $(element).closest('.form-group').removeClass('has-error');
            },
            success: function(label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success');
            },
            submitHandler: function(form) {
                var btn = $("#editSim-form :submit");
                btn.button('loading');
                var options = {
                    success: function(data) {
                        btn.button('reset');
                        $('#editSIM').modal('hide');
                        layer.msg(data.message, {
                            shade: [0.5, '#fff'],
                            time: 2000,
                        });
                        if (data.type == "success") {
                        	btn.remove();
                            searchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }
    return {
        init: function() {
            bindSearch();
            reset();
        },
        addSim: function() {
            submitAddSim();
        },
				editSim: function() {
            submitEditSim();
        }
    }
}();
