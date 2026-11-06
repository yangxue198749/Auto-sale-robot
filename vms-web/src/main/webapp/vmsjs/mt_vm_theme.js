var MtVmTheme = function(){
    var theme_searchEvent = function(){
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
                $("#st_vm_theme_div").html(data);
//		    		Metronic.init() ;
            }
        };
        $("#st_vm_theme_search_form").ajaxSubmit(options);
    }
    var bindSearch = function(){
        $("#st_vm_theme_search_btn").bind("click", function(){
            theme_searchEvent();
        });
    }
    var submitMtVmThemeBySt = function(){
        var validateForm = $("#mt_vm_theme_form_st");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {},
            messages: {},
            errorPlacement: function (error, element) {},
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
                var btn = $("#mt_vm_theme_form_st:submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#CtrlTheTheme').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        if(data.code=="0"){
                            btn.remove();
                            MtVm.search();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }

        });
    };
    var bindFormat= function(){
        $('#CtrlTheTheme .tabheader').on('click','.slt',function(){
            $(this).addClass('on').siblings().removeClass('on');
            $('#CtrlTheTheme .modal_commonbody').hide().eq($('#CtrlTheTheme .tabheader .slt').index(this)).show();
        });
        //版式选择
        $('#themeFormat').on('click','.diy_radio',function(){
            $(this).addClass('checked').siblings().removeClass('checked');
            var themeVal = $('#themeFormat').find('.diy_radio.checked input[type=hidden]').val();
            $('#formatCode').val(themeVal);
        });
    };
    var submitMtVmThemeFormat = function(){
        var validateForm = $("#mt_vm_theme_format_form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {},
            messages: {},
            errorPlacement: function (error, element) {},
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
                var btn = $("#mt_vm_theme_format_form");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#CtrlTheTheme').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        if(data.code=="0"){
                            btn.remove();
                            MtVm.search();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }

        });
    };
    // 入口配置
    var validateThemeForm = function(){
        var validateForm = $("#theme-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {},
            messages: {},
            errorPlacement: function (error, element) {},
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
                //效验条件
                var arr = [];
                $('#theme-form tbody tr').each(function(index,item){
                    var _resourceId = $(this).find('.resourceId').val();
                    var _fileInput = $(this).find('.fileInput').val();
                    var _imgUrl = $(this).find('.imgUrl').val();
                    if(_resourceId == ''){
                        if(_imgUrl != ''){
                            arr.push(index);
                            layer.msg("请完成入口配置！",{shade: [0.5, '#fff'],time:2000});
                            return false;
                        }
                        if(_fileInput != ''){
                            arr.push(index);
                            layer.msg("请完成入口配置！",{shade: [0.5, '#fff'],time:2000});
                            return false;
                        }
                        /*if(_fileInput != ''){
                            arr.push(index);
                            layer.msg("请完成入口配置！",{shade: [0.5, '#fff'],time:2000});
                            return false;
                        }*/
                    }else {
                        if(_imgUrl == ''){
                            if(_fileInput == ''){
                                arr.push(index);
                                layer.msg("请完成入口配置！",{shade: [0.5, '#fff'],time:2000});
                                return false;
                            }
                        }
                    }
                    /*else if(_fileInput == ''){
                        if(_resourceId != ''){
                            arr.push(index);
                            layer.msg("请完成入口配置！",{shade: [0.5, '#fff'],time:2000});
                            return false;
                        }
                    }*/
                });
                if(arr.length == 0){
                    var options = {
                        type : 'POST',
                        beforeSubmit : function() {},
                        success : function(data) {
                            $('#CtrlTheTheme').modal('hide') ;
                            if(data.code =="0"){
                                layer.msg("设置成功！",{shade: [0.5, '#fff'],time:2000});
                                theme_searchEvent();
                            } else {
                                layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                            }
                        }
                    };
                    $(form).ajaxSubmit(options);
                }

            }
        });
    };
    return{
        init:function(){
            bindSearch();
            submitMtVmThemeBySt();
            bindFormat();
            submitMtVmThemeFormat();
            validateThemeForm();
        }
    }
}();