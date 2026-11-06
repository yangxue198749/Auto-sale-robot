var StVmEvent = function(){
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

    var submitStVmEvent = function(){

        var validateForm = $("#addAutoMatSaleForm");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                strategyName: {
                    required: true
                },
                discountValue:{
                    required: true,
                    digits:true,
                    min:1,
                    max:1000000
                }
            },
            messages: {
                strategyName: {
                    required: "请填写策略名称！"
                },
                discountValue:{
                    required: "请填写折扣额",
                    digits:"必须填写正整数",
                    min:"不可小于0",
                    max:"数字过大"
                }
            },
            errorPlacement: function (error, element) { // render error placement for each input type
                if(""!=error.html()){
                    layer.msg(error.html(),{shade: [0.5, '#fff'],time:800,});
                }
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
                var btn = $("#addAutoMatSaleForm:submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#addAutoMatSale').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        if(data.code=="0"){
                        	btn.remove();
                            StVmEvent.search();
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
        search:function () {
            searchEvent();
        },
        addStVmEvent:function () {
            submitStVmEvent();
        }
    }
}();