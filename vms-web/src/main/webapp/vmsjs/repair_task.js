var RepairTask = function(){
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
	var submitAddTask = function(){
		var validateForm = $("#add-task-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	ownerId: {
                    required: true
                },
                taskTime: {
                	required: true
                },
                
            },
            messages: {
            	ownerId: {
                    required: "请选择负责人！"
                },
                taskTime: {
	            	required: "请选择任务时间！"
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
            	var btn = $("#add-task-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		$('#addWorkOrder').modal('hide') ;
				        		searchEvent();
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
		});
	}
	var bindSearchMtVm = function(){
		$("#search-mtVm-btn").bind("click", function(){
			//查询售货机
			var options = { 
		    		type: 'POST',
		    		beforeSubmit:function(){
//		    			Metronic.blockUI({target: "#pagediv",boxed: true});
		    		},
			        success:function (data)  { 
			    		$("#mtVmDiv").html(data);
//			    		Metronic.init() ;
					}   
			    }; 
		    $("#search-mtVm-form").ajaxSubmit(options);
			
		});
		$('#select-mtVm-btn').bind('click',function(){
			//确认选择
			var checks = $("#mtVmDiv table tbody input[type='checkbox']:checked");
			if(checks.length !=1){
				layer.msg("请选择一个售货机",{shade: [0.5, '#fff'],time:2000,});
				return ;
			}
			var repairManId = checks.attr('userId');
			$('#addTaskVmId').val(checks.val());
			$('#selectMtVm').modal('hide');
			$('#innerCodeP').html(checks.attr('innerCode'));
			getRepairMan(repairManId);
			
		});
	}
	var getRepairMan = function(repairManId){
		$.ajax({
			type : 'post',
			url :'/user/getUserByLevel',
			data : {"defaultUserId":repairManId,"level":5},
			async : true,
			dataType : 'json',
			cache : false,
			success : function(data) {
				$('#ownerId').html(data.html);
			}
		});
	}
	var calcelEvent = function(){
		//取消
		$(".btn_tableCancel").click(function(){
			var $this = $(this);
			var dataSet=$this.attr('data-set');
			var url = $this.attr('href');
			var chkBox = $(dataSet);
			var ids="";
			if(chkBox.length == 0){
				layer.msg("请先选中需要取消的内容",{shade: [0.5, '#fff'],time:2000,});
				return false;
			}else{
				$(dataSet).each(function() {
					ids += $(this).val()+",";
				});
				ids = ids.substring(0,ids.length-1);
				layer.confirm('您确定要取消这'+ chkBox.length +'条内容？', {
					btn: ['确定','取消'],
					title: '提示',
				}, function(){ //成功后执行
					//这里ajax处理服务端删除
					$.ajax({
						type : 'post',
						url :url,
						data : {ids:ids},
						async : true,
						dataType : 'json',
						cache : false,
						success : function(data) {
							searchEvent();
							layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
						}
					});
				}, function(index){
					layer.close(index);//关闭窗口
				});
			}
		});  
	}
	var submitEditTaskVm = function(){
		var validateForm = $("#edit-task-form");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	editOwnerId: {
                    required: true
                }
            },
            messages: {
            	editOwnerId: {
                    required: "请选择负责人！"
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
            	var btn = $("#edit-task-form :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
				        		$('#changeWorkOrder').modal('hide') ;
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
			calcelEvent();
		},
		addTask:function(){
			submitAddTask();
		},
		selectMtVm:function(){
			bindSearchMtVm();
		},
		editTaskVm:function(){
			submitEditTaskVm();
		}
		
	}
}();