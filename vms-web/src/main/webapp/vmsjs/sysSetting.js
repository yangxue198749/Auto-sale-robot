var SysSetting = function() {
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
    };
    var rightSearchEvent = function() {
        var options = {
            type: 'POST',
            beforeSubmit: function() {
                // Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function(data) {
                $("#settings-pagediv").html(data);
                // Metronic.init() ;
            }
        };
        $("#search-setting-form").ajaxSubmit(options);
    };
    var bindTr = function() {
        $('#pagediv table tbody tr').bind('click', function() {
            $('#search-setting-form input[name="pid"]').val($(this).attr('settingId'));
            rightSearchEvent();
        });
    };
    var submitType = function(){


        var validateForm = $("#strategy-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                key: {
                    required: true
                }
            },
            messages: {
                key: {
                    required: "请填写类型key！"
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
                        $('#addType').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        if(data.code=="0"){
                            btn.remove();
                            searchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }

        });
    };
    var submitSetting = function(){


        var validateForm = $("#setting-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                key: {
                    required: true
                },
                value:{
                    required: true
                }
            },
            messages: {
                key: {
                    required: "请填写配置key！"
                },
                value: {
                    required: "请填写配置value！"
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
                var btn = $("#setting-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#addSetting').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        if(data.code=="0"){
                            btn.remove();
                            $('#search-setting-form input[name="pid"]').val($("#pid").val());
                            rightSearchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }

        });
    };
    return {
        init: function() {
            bindSearch();
            reset();
        },
        settingTable: function() {
            bindTr();
        },
        updateType: function () {
            submitType();
        },
        updateSetting: function () {
            submitSetting();
        }

    }
}();
