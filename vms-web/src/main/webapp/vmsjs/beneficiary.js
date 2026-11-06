var Beneficiary = function () {
    var submitAliInfo = function () {
        var validateForm = $("#ali-pay-form");
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
                var btn = $("#ali-pay-form :submit");
                btn.button('loading');
                var options = {
                    success: function (data) {
                        btn.button('reset');
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                        $('#addNode').modal('hide');
                        if (data.type == "success") {
                            btn.remove();
                            location.reload();
                        }
                    },
                    error: function () {
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }
    var submitWxInfo = function () {
        var validateForm = $("#wx-pay-form");
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
                var btn = $("#wx-pay-form:submit");
                btn.button('loading');
                var options = {
                    success: function (data) {
                        btn.button('reset');
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                        $('#addNode').modal('hide');
                        if (data.type == "success") {
                            btn.remove();
                            location.reload();
                        }
                    },
                    error: function () {
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                    }
                };
                $(form).ajaxSubmit(options);
            }
        });
    }
    var deleteInfo = function ($this) {
        var url = $this.attr('href');
        var payInfoId = $this.attr('payInfoId');
        layer.confirm('是否确认删除该支付渠道？', {
            btn: ['确定', '取消'],
            title: '提示',
        }, function () { //成功后执行
            //这里ajax处理服务端删除
            $.ajax({
                type: 'post',
                url: url,
                data: {payInfoId: payInfoId},
                async: true,
                dataType: 'json',
                cache: false,
                success: function (data) {
                    if (data.type == "success") {
                        layer.msg('已删除', {shade: [0.5, '#fff'], time: 2000});
                        location.reload();
                    } else {
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                    }
                }
            });
        }, function (index) {
            layer.close(index);//关闭窗口
        });
    }

    var bindDeleteInfo = function () {
        $(".btn_delete_pay").click(function () {
            var $this = $(this);
            deleteInfo($this);
        });
    }


    return {
        addAliInfo: function () {
            submitAliInfo();
        },
        addWxInfo: function () {
            submitWxInfo();
        },
        deletePayInfo: function () {
            bindDeleteInfo();
        }

    }
}();