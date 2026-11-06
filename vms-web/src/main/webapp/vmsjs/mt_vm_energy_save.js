var MtVmEnergySave = function(){
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
		    		$("#energyStDiv").html(data);
//		    		Metronic.init() ;
				}   
		    }; 
	    $("#searchStEnergySave-form").ajaxSubmit(options);
	}
	var bindSearch = function(){
		$("#searchStEnergySave-btn").bind("click", function(){
			searchEvent();
		});
	}
	var submitSelectSt = function(){
		$('#selectStEnergySaveBtn').bind('click',function(){
			var checks = $("#energyStDiv table tbody input[type=checkbox]:checked");
			if(checks.length !=1){
				layer.msg("请选择一个策略",{shade: [0.5, '#fff'],time:1000,});
				return;
			}
			var strategyId = checks.val();
			var mtVmIds = $('#mtVmEnergySaveMtVmIds').val();
			$.ajax({
				type : 'post',
				url :'/mtVmEnergySave/selectSt',
				data : {"strategyId":strategyId,"mtVmIds":mtVmIds},
				async : true,
				dataType : 'json',
				cache : false,
				success : function(data) {
					layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
					if(data.type=="success"){
						$('#CtrlTheEnergy').modal('hide');
					}
				}
			});
		});
	}
	var submitAdd = function(){
		var validateForm = $("#addTheEnergyForm");
		validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
            	energySaveType: {
                    required: true
                },
                startTime:{
                	required: true
                },
                endTime:{
                	required: true
                },
            },
            messages: {
            	addEnergySaveType: {
                    required: "请选择节能类型！"
                },
                addStartTime:{
                	required: "请选择开始时间！"
                },
                addEndTime:{
                	required: "请选择结束时间！"
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
            	var btn = $("#addTheEnergyForm :submit");
        	    btn.button('loading');
            	var options = {
				        success:function (data){ 
				        	btn.button('reset');
				        	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
				        	if(data.type=="success"){
				        		btn.remove();
								MtVm.search();
				        		$('#Child_addTheEnergy').modal('hide') ;
				        		$('#mtVmEnergySaveDiv table tbody').html(data.html);
				        	}
						}
				}; 
            	$(form).ajaxSubmit(options);
            }
		});
	}
	var submitEdit = function(){
		var validateForm = $("#editTheEnergyForm");
		validateForm.validate({
			doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
			errorElement: 'span', //default input error message container
			errorClass: 'help-block help-block-error', // default input error message class
			focusInvalid: false, // do not focus the last invalid input
			rules: {
				energySaveType: {
					required: true
				},
				startTime:{
					required: true
				},
				endTime:{
					required: true
				},
			},
			messages: {
				addEnergySaveType: {
					required: "请选择节能类型！"
				},
				addStartTime:{
					required: "请选择开始时间！"
				},
				addEndTime:{
					required: "请选择结束时间！"
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
				var btn = $("#editTheEnergyForm :submit");
				btn.button('loading');
				var options = {
						success:function (data){ 
							btn.button('reset');
							layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
							if(data.type=="success"){
								btn.remove();
								$('#Child_editTheEnergy').modal('hide') ;
								$('#mtVmEnergySaveDiv table tbody').html(data.html);
							}
						}
				}; 
				$(form).ajaxSubmit(options);
			}
		});
	}
	var deleteEnergySave = function(){
		$("#deleteEnergySaveBtn").click(function(){
			var $this = $(this);
			var dataSet=$this.attr('data-set');
			var url = $this.attr('href');
			var chkBox = $(dataSet);
			var ids="";
			if(chkBox.length == 0){
				layer.msg("请先选中需要删除的内容",{shade: [0.5, '#fff'],time:2000,});
				return false;
			}else{
				$(dataSet).each(function() {
					ids += $(this).val()+",";
				});
				ids = ids.substring(0,ids.length-1);
				layer.confirm('您确定要删除这'+ chkBox.length +'条内容？', {
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
					    	if(data.type=="success"){
					    		chkBox.parents("tr").remove();
					    		layer.msg('已删除',{shade: [0.5, '#fff'],time:2000,});
					    	}
							tablePlaceholder();
					    }
					});
				}, function(index){
				  layer.close(index);//关闭窗口
				});
			}
		});  
	}
	var bindSaveEnergy = function(){
		$('#saveEnergyBtn').bind('click',function(){
			var mtVmIds = $('#mtVmEnergySaveMtVmIds').val();
			if(mtVmIds == null ){
				layer.msg('请选择售货机',{shade: [0.5, '#fff'],time:2000,});
				return ;
			}
			if(mtVmIds.split(',').length!=1){
				layer.msg('直接设置节能只能选择一个售货机',{shade: [0.5, '#fff'],time:2000,});
				return ;
			}
			var btn = $(this);
			btn.button('loading');
			$.ajax({
			    type : 'post',
			    url :"/mtVmEnergySave/pushToMCS",
			    data : {vmId:mtVmIds},
			    async : true,
			    dataType : 'json',
			    cache : false,
			    success : function(data) {
			    	btn.button('reset');
			    	layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
			    	if(data.type=="success"){
			    		$('#CtrlTheEnergy').modal('hide');
			    		btn.remove();
			    	}
			    }
			});
		});
	}
	return{
		init:function(){
			bindSearch();
			submitSelectSt();
			deleteEnergySave();
			bindSaveEnergy();
		},
		add:function(){
			submitAdd();
		},
		edit:function(){
			submitEdit();
		}
	}
}();