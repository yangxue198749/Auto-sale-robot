var MtVmShortcuts = function(){
    var shortcuts_searchEvent = function(){
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
                $("#st_vm_shortcuts_div").html(data);
//		    		Metronic.init() ;
            }
        };
        $("#st_vm_shortcuts_search_form").ajaxSubmit(options);
    }
    var shortcuts_sku_searchEvent = function(){
        var options = {
            type: 'POST',
            beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success:function (data)  {
                $("#st_vm_shortcuts_sku_div").html(data);
//		    		Metronic.init() ;
            }
        };
        $("#st_vm_shortcuts_sku_search_form").ajaxSubmit(options);
    }
    var bindSearch = function(){
        $("#st_vm_shortcuts_search_btn").bind("click", function(){
            shortcuts_searchEvent();
        });
    }
    var bindSkuSearch = function(){
        $("#st_vm_shortcuts_sku_search_btn").bind("click", function(){
            shortcuts_sku_searchEvent();
        });
    }
        var submitMtVmShortcutsBySt = function(){

        var validateForm = $("#mt_vm_shortcuts_form_st");
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
                var btn = $("#mt_vm_shortcuts_form_st:submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#CtrlTheFlashBuy').modal('hide') ;
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

    var submitMtVmShortcuts = function(){


        var validateForm = $("#mt_vm_shortcuts_form");
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
                var btn = $("#mt_vm_shortcuts_form:submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#CtrlTheFlashBuy').modal('hide') ;
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
    return{
        init:function(){
            bindSearch();
            submitMtVmShortcutsBySt();
            submitMtVmShortcuts();
        },
        searchSku:function(){
            bindSkuSearch();
        },
    }
}();