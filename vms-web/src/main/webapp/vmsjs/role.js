var Role = function() {
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
    var submitAddRole = function() {
        var validateForm = $("#add-role-form");
        validateForm.validate({
            doNotHideMessage: true,
            errorElement: 'span',
            errorClass: 'help-block help-block-error',
            focusInvalid: false,
            rules: {
                roleName: {
                    required: true,
                    Au9: true
                }
            },
            messages: {
                roleName: {
                    required: "请填写角色名称！",
                    Au9: "只允许输入中文、英文或数字（含）"
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
                var btn = $("#add-role-form :submit");
                btn.button('loading');
                var options = {
                    success: function(data) {
                        btn.button('reset');
                        $('#add-role-div').modal('hide');
                       // layer.msg(data.desc, {shade: [0.5, '#fff'], time: 2000});
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
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
		var submitEditRole = function() {
        var validateForm = $("#edit-role-form");
        validateForm.validate({
            doNotHideMessage: true,
            errorElement: 'span',
            errorClass: 'help-block help-block-error',
            focusInvalid: false,
            rules: {
                roleName: {
                    required: true
                }
            },
            messages: {
                roleName: {
                    required: "请填写角色名称！"
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
                var btn = $("#edit-role-form :submit");
                btn.button('loading');
                var options = {
                    success: function(data) {
                        btn.button('reset');
                        $('#edit-role-div').modal('hide');
                        // layer.msg(data.desc, {shade: [0.5, '#fff'], time: 2000});
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
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
        addRole: function() {
            submitAddRole();
        },
		editRole: function() {
            submitEditRole();
        }
    }
}();
