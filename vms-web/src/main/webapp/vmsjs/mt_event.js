var MtEvent = function(){
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
            $('.select2').val(null).trigger("change");
            // $('.select2').val("请选择").trigger("change");

        });
    }
    var bindSearch = function(){
        $("#searchButton").bind("click", function(){
            searchEvent();
        });
    }
    var bindSearchVm = function(){
        $("#searchButtonVm").bind("click", function(){
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#selectVmbox").html(data);
//		    		Metronic.init() ;
                }
            };
            $("#search-form-vm").ajaxSubmit(options);
        });
    }
    var bindSearchSku = function(){
        $("#searchButtonSku").bind("click", function(){
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#selectSkubox").html(data);
//		    		Metronic.init() ;
                }
            };
            $("#search-form-sku").ajaxSubmit(options);
        });
    }

    var bindSearchStVm = function(){
        $("#search-st-vm").bind("click", function(){
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#selectVmboxSales").html(data);
//		    		Metronic.init() ;
                }
            };
            $("#search-st-vm-form").ajaxSubmit(options);
        });
    }
    var bindSearchStSku = function(){
        $("#search-st-sku").bind("click", function(){
            var options = {
                type: 'POST',
                beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    $("#selectShop").html(data);
//		    		Metronic.init() ;
                }
            };
            $("#search-st-sku-form").ajaxSubmit(options);
        });
    }

    var skuEventConfirm = function(){
        $("#sku_next_btn").bind("click", function(){
            var $this = $(this);
            var dataSet=$this.attr('data-set');
            var chkBox = $(dataSet);
            $(dataSet).each(function() {
                $("#add_mtVm_sku").append('<span class="mtVmSpan"><input type="hidden" value="'+$(this).val()+'" name="vmIds"><div class="btn btn-default btn-sm">'+$(this).attr("innerCode")+'<i class="iconfont icon-shanchu deleteBtn"></i></div></span>');
            });
            // $(this).parents("#selectVmbox").hide();
            $(this).parents("#selectSkubox").modal('hide');
        });
    }

    var vmEventConfirm = function(){
        $("#vm_next_btn").bind("click", function(){
            var $this = $(this);
            var dataSet=$this.attr('data-set');
            var chkBox = $(dataSet);
            $(dataSet).each(function() {
                $("#add_mtVm_vm").append('<span class="mtVmSpan"><input type="hidden" value="'+$(this).val()+'" name="vmIds"><div class="btn btn-default btn-sm">'+$(this).attr("innerCode")+'<i class="iconfont icon-shanchu deleteBtn"></i></div></span>');
            });
            $(this).parents("#selectVmbox").modal('hide');
        });
    }

    var skuStConfirm = function(){
        $("#sku_st_next_btn").bind("click", function(){
            var $this = $(this);
            var dataSet=$this.attr('data-set');
            var chkBox = $(dataSet);

            $(dataSet).each(function() {
                $("#sku_st_input").val($(this).val());
                $("#sku_st_span").html($(this).attr("strategyName")); });
            // $(this).parents("#selectVmbox").hide();
            $(this).parents("#selectShop").modal('hide');
        });
    }

    var vmStConfirm = function(){
        $("#vm_st_next_btn").bind("click", function(){
            var $this = $(this);
            var dataSet=$this.attr('data-set');
            var chkBox = $(dataSet);
            $(dataSet).each(function() {
                $("#vm_st_input").val($(this).val());
                $("#vm_st_span").html($(this).attr("strategyName"));
            });
            $(this).parents("#selectVmboxSales").modal('hide');
        });
    }



    var submitSkuEvent = function(){

        var validateForm = $("#set-sku-event-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                beginDate: {
                    required: true
                },
                endDate: {
                    required: true
                },
                startTime: {
                    required: true
                },
                endTime: {
                    required: true
                }
            },
            messages: {
                beginDate: {
                    required: "请选择开始日期！"
                },
                endDate: {
                    required: "请选择结束日期！"
                },
                startTime: {
                    required: "请选择开始时间！"
                },
                endTime: {
                    required: "请选择结束时间！"
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
                var btn = $("#set-sku-event-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#applyShopSales').modal('hide') ;
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
    }

    var submitVmEvent = function(){

        var validateForm = $("#set-vm-event-form");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                nodeName1: {
                    required: true
                },
            },
            messages: {
                nodeName1: {
                    required: "请填写点位名称！"
                },
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
                var btn = $("#set-vm-event-form :submit");
                btn.button('loading');
                var options = {
                    success:function (data){
                        btn.button('reset');
                        $('#applyVmboxSales').modal('hide') ;
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
    }



    return{
        init:function(){
            bindSearch();
            reset();
        },
        addSkuEvent:function(){
            submitSkuEvent();
        },
        addVmEvent:function(){
            submitVmEvent();
        },
        bindNext:function(){
            skuEventConfirm();
            vmEventConfirm();
            skuStConfirm();
            vmStConfirm();
            bindSearchSku();
            bindSearchVm();
            bindSearchStSku();
            bindSearchStVm();
        }
    }
}();