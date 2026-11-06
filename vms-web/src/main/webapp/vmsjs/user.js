var User = function() {
    var searchEvent = function() {
        var options = {
            type: 'POST',
            beforeSubmit: function() {
            },
            success: function(data) {
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
    var submitEditUser = function() {
        var validateForm = $("#edit-user-form");
        validateForm.validate({
            doNotHideMessage: true,
            errorElement: 'span',
            errorClass: 'help-block help-block-error',
            focusInvalid: false,
            rules: {
                userAccount: {
                    required: true,
                    A9: true,
                    rangelength: [4, 20]
                },
                userName: {
                    required: true,
                    Au9: true,
                    rangelength: [2, 10]
                },
                userPhone: {
                    required: true,
                    digits: true,
                    mobile: true
                    //rangelength: [11, 11]
                },
                companyId: {
                    required: true
                }
            },
            messages: {
                userAccount: {
                    required: "请输入登录账号",
                    A9: "只允许输入英文或数字（含），4-20位以内",
                    rangelength: "账号长度4-20"
                },
                userName: {
                    required: "请输入用户姓名",
                    Au9: "只允许输入中文、英文或数字（含），2-10位以内",
                    rangelength: "2-10字符（含）以内"
                },
                userPhone: {
                    required: "请输入用户手机号",
                    digits: "手机号必须时数字",
                    mobile: "请输入正确的手机号码"
                    //rangelength: "手机号长度11位"
                },
                companyId: {
                    required: "请选择公司"
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
                var btn = $("#edit-user-form :submit");
                btn.button('loading');
                var options = {
                    success: function(data) {
                        btn.button('reset');
                        $('#edit-user-div').modal('hide');
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
				editUser: function() {
            submitEditUser();
        }
    }
}();
