var MtVm = function(){
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
	var submitAddMtVm = function(){
		$("#submitAddMtVmBtn").bind('click',function(){
			var vmModelId =$("#add-mtvm-form input[name='vmModelId']").val();
			if('' == vmModelId || null == vmModelId){
				layer.msg("请选择售货机型号",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var nodeId = $("#add-mtvm-form input[name='nodeId']").val();
			if('' == nodeId || null == nodeId){
				layer.msg("请选择点位",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var payType = $("#add-mtvm-form input[name='payType']").val();
			if('' == payType || null == payType){
				layer.msg("请选择支付方式",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var btn = $("#submitAddMtVmBtn");
    	    btn.button('loading');
        	var options = {
			        success:function (data){ 
			        	btn.button('reset');
			        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
			        	if(data.type=="success"){
			        		btn.remove();
			        		$('#addAutoMat').modal('hide') ;
			        		searchEvent();
			        	}
					}
			}; 
        	$('#add-mtvm-form').ajaxSubmit(options);
		});
	}

	var submitExtendMtVm = function(){
		$("#submitAddMtVmBtn").bind('click',function(){
			var vmModelId =$("#add-mtvm-form input[name='vmModelId']").val();
			if('' == vmModelId || null == vmModelId){
				layer.msg("请选择售货机型号",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var btn = $("#submitAddMtVmBtn");
			btn.button('loading');
			var options = {
				success:function (data){
					btn.button('reset');
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
					if(data.type=="success"){
						btn.remove();
						$('#addAutoMat').modal('hide') ;
						searchEvent();
					}
				}
			};
			$('#add-mtvm-form').ajaxSubmit(options);
		});
	}

	var bindSearchNode = function(){
		$('#search-node-btn').bind('click',function(){
			var options = { 
		    		type: 'POST',
		    		beforeSubmit:function(){
//		    			Metronic.blockUI({target: "#pagediv",boxed: true});
		    		},
			        success:function (data)  { 
			    		$("#nodeDiv").html(data);
//			    		Metronic.init() ;
					}   
			    }; 
		    $("#search-node-form").ajaxSubmit(options);
		});
	}
	var selectNodeEvent = function(){
		$('#select-node-btn').bind('click',function(){
			var checks = $('#nodeDiv table tbody input[type="checkbox"]:checked');
			if(checks.length != 1){
				layer.msg("请选择一个点位",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			$('#add-mtvm-form input[name="nodeId"]').val(checks.val());
			$('#add-mtvm-form .nodeName').html(checks.attr("selectNodeName"));
			$('#Child_choiceLocation').modal('hide');
		});
	}
	var bindSearchCopyMtVM = function(){
		$('#srarch-copy-mt-vm-btn').bind('click',function(){
			var options = { 
		    		type: 'POST',
		    		beforeSubmit:function(){
//		    			Metronic.blockUI({target: "#pagediv",boxed: true});
		    		},
			        success:function (data)  { 
			    		$("#copyMtVmDiv").html(data);
//			    		Metronic.init() ;
					}   
			    }; 
		    $("#srarch-copy-mt-vm-form").ajaxSubmit(options);
		});
	}
	var selectCopyMtVm = function(){
		$('#select-copy-mt-vm-btn').bind('click',function(){
			var checks = $('#copyMtVmDiv table tbody input[type="checkbox"]:checked');
			if(checks.length != 1){
				layer.msg("请选择一个售货机",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			$('#add-mtvm-form input[name="copyMtVmId"]').val(checks.val());
			$('#add-mtvm-form .copyInnerCode').html(checks.attr("innerCode"));
			$('#Child_copyMatStrategy').modal('hide');
		});
	}
	var submitEditMtVm = function(){
		$('#submitEditMtVmBtn').bind('click',function(){
			var payType = $("#edit-mtVm-form input[name='payType']:checked").val();
			if('' == payType || null == payType){
				layer.msg("请选择支付方式",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var options = { 
					type: 'POST',
					beforeSubmit:function(){
						
					},
					success:function (data)  { 
						layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
						if(data.type=="success"){
							$('#editMtVm').modal('hide');
							searchEvent();
						}
					}   
			}; 
			$("#edit-mtVm-form").ajaxSubmit(options);
		});
	}
	var changeSale = function(){
		$('#changeSaleBtn').bind('click',function(){
			var dataSet = $("#pagediv table tbody input[type='checkbox']:checked");
			if(dataSet.length != 1){
				layer.msg("请选择一个售货机",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var url = $(this).attr('href');
			$.ajax({
				type : 'post',
				url : url,
				data : {"vmId":dataSet.val()},
				async : true,
				dataType : 'json',
				cache : false,
				success : function(data) {
					layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
					if(data.type=="success"){
						searchEvent();
					}
				}
			});
		});
	}
	var selectVmModelEvent = function () {
		$('#select-vm-model-btn').bind('click',function(){
			var checks = $('#selectVmModel table tbody input[type="checkbox"]:checked');
			if(checks.length != 1){
				layer.msg("请选择一个售货机型号",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			$('#add-mtvm-form input[name="vmModelId"]').val(checks.val());
			$('#add-mtvm-form .vmModelName').html(checks.attr("selectVmModelName"));
			$('#selectVmModel').modal('hide');
		});
	}
	var selectExtendVmModelEvent = function () {
		$('#select-vm-model-btn').bind('click',function(){
			var checks = $('#selectVmModel table tbody input[type="checkbox"]:checked');
			if(checks.length != 1){
				layer.msg("请选择一个售货机型号",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			$('#extend-mtvm-form input[name="vmModelId"]').val(checks.val());
			$('#extend-mtvm-form .vmModelName').html(checks.attr("selectVmModelName"));
			$('#selectVmModel').modal('hide');
		});
	}

	var exportMtVmList = function(){
		$("#exportVmBtn").bind("click",function () {
			window.location = "/mtVm/exportMtVmList";
		});
	};

	var mtVmShortcuts = function(){
		$("#shortcutsBtn").bind("click",function () {
            //var shorts = $("#pagediv tbody input[type='checkbox']:checked").parents('td').siblings().find('.shortcuts').val();
            $("#pagediv td input[type='checkbox']:checked").each(function () {
                var _shorts = $(this).parents('tr').find('.shortcuts').val();
                console.log('_shorts:'+_shorts);
                if(0 == _shorts|| null == _shorts || "" == _shorts || "undefined" == _shorts || undefined == _shorts){
                    console.log("shorts = " +_shorts);
                    layer.msg('该机型无闪购键!',{shade: [0.5, '#fff'],time:2000});
                    return false;
                }
            })
		});
	};

	var energyValidate = function(){
		$("#energyBtn").bind("click",function () {
			//var temperatureHandlerMode = $("#pagediv tbody input[type='checkbox']:checked").parents('td').siblings().find('.temperatureHandlerMode').val();
			//var screenHave = $("#pagediv tbody input[type='checkbox']:checked").parents('td').siblings().find('.screenHave').val();
            var _this = $(this);
            $("#pagediv td input[type='checkbox']:checked").each(function () {
                var _temperatureHandlerMode = $(this).parents('tr').find('.temperatureHandlerMode').val();
                var _screenHave = $(this).parents('tr').find('.screenHave').val();
                console.log('_temperatureHandlerMode:'+_temperatureHandlerMode);
                console.log('_screenHave:'+_screenHave);
                if(
                    (null == _temperatureHandlerMode || "" == _temperatureHandlerMode || "undefined" == _temperatureHandlerMode || undefined == _temperatureHandlerMode || 50 == _temperatureHandlerMode)
                    &&
                    (null == _screenHave || "" == _screenHave || "undefined" == _screenHave || undefined == _screenHave || 2 == _screenHave )
                ){
                    layer.msg('当前机型无法设置!',{shade: [0.5, '#fff'],time:2000});
                    _this.attr("data-toggle","");
                    return false;
                }else{
                    _this.attr("data-toggle","modal");
                }
            });

			/*if(
				(null == temperatureHandlerMode || "" == temperatureHandlerMode || "undefined" == temperatureHandlerMode || undefined == temperatureHandlerMode || 50 == temperatureHandlerMode)
				&&
				(null == screenHave || "" == screenHave || "undefined" == screenHave || undefined == screenHave || 2 == screenHave )
			){
				layer.msg('当前机型无法设置!',{shade: [0.5, '#fff'],time:2000});
				return false;
			}*/
		});
	};

	return{
		init:function(){
			bindSearch();
			reset();
			changeSale();
			exportMtVmList();
			energyValidate();
			mtVmShortcuts();
		},
		search:function(){
			searchEvent();
		},
		addMtVm:function(){
			submitAddMtVm();
		},
		extendMtVm:function(){
			submitExtendMtVm();
		},
		selectNode:function(){
			bindSearchNode();
			selectNodeEvent();
		},
		selectCopyMtVm:function(){
			bindSearchCopyMtVM();
			selectCopyMtVm();
		},
		editMtVm:function(){
			submitEditMtVm();
		},
		selectVmModel:function () {
			selectVmModelEvent();
		},
		selectExtendVmModel:function () {
			selectExtendVmModelEvent();
		}
	}
}();