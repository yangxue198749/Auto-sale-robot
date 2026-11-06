var MtVmPrice = function(){
    var price_searchEvent = function(){
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
                $("#st_vm_price_div").html(data);
//		    		Metronic.init() ;
            }
        };
        $("#st_vm_price_search_form").ajaxSubmit(options);
    }
    var bindSearch = function(){
        $("#st_vm_price_search_btn").bind("click", function(){
            price_searchEvent();
        });
    }
    var submitMtVmPriceBySt = function(){

        var validateForm = $("#mt_vm_price_form_st");
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
                var btn = $("#mt_vm_price_form_st:submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#CtrlThePrice').modal('hide') ;
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        if(data.code=="0"){
                        	btn.remove();
                            MtVm.search();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }

        });
    }
    var bindReset = function(){
        $("#originalPrice").bind("click", function(){
            var ids = $("#mt_vm_price_form_st input[name='ids']").val();
            $.ajax({
                type : 'post',
                url : '/mtVmPrice/originalPrice',
                data : {"ids":ids},
                async : true,
                dataType : 'json',
                cache : false,
                success : function(data) {
                    $('#CtrlThePrice').modal('hide') ;
                    layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
                    if(data.type=="success"){
                        MtVm.search();
                    }
                }
            });
        });
    }
    return{
        init:function(){
            bindSearch();
            submitMtVmPriceBySt();
            bindReset();
        },
    }
}();