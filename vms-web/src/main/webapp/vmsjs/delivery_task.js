var DeliveryTask = function(){
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
                }
            },
            messages: {
            	ownerId: {
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
			var mtVmDivCheck = $('#mtVmDiv input[type="checkbox"]:checked').length;
			if(mtVmDivCheck == 0){
				layer.msg("请选择售货机",{shade: [0.5, '#fff'],time:2000});
				return false;
			}
			//确认选择
			var checks = $("#mtVmDiv table tbody input[type='checkbox']:checked");
			var innerCodes = "";
			var vmIds = "";
			$("#mtVmDiv table tbody input[type='checkbox']:checked").each(function () {
				innerCodes +=  "," + $(this).attr("innerCode");
				vmIds += "," + $(this).val();
			});
			innerCodes = innerCodes.substring(1);
			vmIds = vmIds.substring(1);
			var deliveryManId = checks.attr('userId');
			if(1 == checks.length){
				$('#vmVal').val(1);
			}else if(1 < checks.length){
				$('#vmVal').val(2);
			}
			$('#addTaskVmIds').val(vmIds);
			$('#selectMtVm').modal('hide');
			$('#innerCodeP').html(innerCodes);
			getDeliveryMan(deliveryManId);
			
		});
	}
	var getDeliveryMan = function(deliveryManId){
		$.ajax({
			type : 'post',
			url :'/user/getUserByLevel',
			data : {"defaultUserId":deliveryManId,"level":4},
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

    function getFormJson(){ //frm：form表单的id
        var channelIds = $("input[name='channelId']");
        var realClears =  $("input[name='realClear']");
        var realCounts =  $("input[name='realCount']");
        var skuIds =  $("input[name='skuId']");
        var maxNums =  $("input[name='maxNum']");
        var currNums =  $("input[name='currNum']");
        var flag =  $("input[name='flag']");

        var jsonArray = new Array();
        var size = channelIds.length;
        for(var i = 0;i<size;i++){
            var jsonObj = {"channelId":channelIds[i].value,"realClear":realClears[i].value,"realCount":realCounts[i].value,"skuId":skuIds[i].value,"maxNum":maxNums[i].value,"currNum":currNums[i].value,"flag":flag[i].value};

			jsonArray.push(jsonObj);
        }
        for(var i=0;i<jsonArray.length;i++){
			var reg = /^[0-9]*$/;
			if(!reg.test(jsonArray[i].realClear)){
				layer.msg("清量只能输入正整数",{shade: [0.5, '#fff'],time:2000});
				return null;
			}
			if(!reg.test(jsonArray[i].realCount)){
				layer.msg("补货量只能输入正整数",{shade: [0.5, '#fff'],time:2000});
				return null;
			}
            if(jsonArray[i].realClear - jsonArray[i].maxNum > 0){
                layer.msg("清量不能大于满量",{shade: [0.5, '#fff'],time:2000});
                return null;
            }
            if(jsonArray[i].realCount - jsonArray[i].maxNum > 0){
                layer.msg("补货量不能大于满量",{shade: [0.5, '#fff'],time:2000});
                return null;
            }
			if("" == jsonArray[i].realCount){
				layer.msg("补货量不能为空",{shade: [0.5, '#fff'],time:2000});
				return null;
			}
			if("" == jsonArray[i].realClear){
				layer.msg("清货量不能为空",{shade: [0.5, '#fff'],time:2000});
				return null;
			}
			if(jsonArray[i].realClear - jsonArray[i].currNum > 0){
				layer.msg("清量不能大于当前余量",{shade: [0.5, '#fff'],time:2000});
				return null;
			}
            if(jsonArray[i].maxNum < parseInt(parseInt(jsonArray[i].currNum - jsonArray[i].realClear) + parseInt(jsonArray[i].realCount))){
                layer.msg("补后余量不能大于满量",{shade: [0.5, '#fff'],time:2000});
                return null;
            }
        }
        return JSON.stringify(jsonArray);
    }
	var adjust = function () {
		$("#adJustModel .btn-success").bind("click",function () {
			//success
			if(null != getFormJson()){
				$('#adjustData').val(getFormJson());
				$('#adJustModel').modal('hide');
			}
		});
	};

	var checkedAddTask = function(){
		$('input[name="deliveryPattern"]').click(function(){
			var addTaskVmIds = $('#addTaskVmIds').val();
			var ownerId = $('#ownerId option:selected').val();
			if(addTaskVmIds == ""){
				layer.msg("请选择售货机",{shade: [0.5, '#fff'],time:2000});
				$('#handBtnGroup .val0 input[name="deliveryPattern"]').prop('checked',true).parents('#handBtnGroup').find('.val1 input[name="deliveryPattern"]').prop('checked',false);
			}
			if(ownerId == ""){
				layer.msg("请选择配送员",{shade: [0.5, '#fff'],time:2000});
			}
			if($(this).val() == '1'){
				var deliveryPattern = $('input[name="deliveryPattern"]:checked').val();
				if(addTaskVmIds != null && ownerId != null && deliveryPattern !=null){
					var justHref = '/deliveryTask/adJustTask?vmIds='+addTaskVmIds+'&ownerId='+ownerId+'&deliveryPattern='+deliveryPattern;
					$('#adJustModelBtn').attr('href',justHref);
				}
			}
		});
	}
	var exportDelivery = function(){
		$("#exportDeliveryBtn").bind("click",function () {
			var id_array=new Array();
			$('#pagediv table tbody input[type="checkbox"]:checked').each(function(){
				id_array.push($(this).val());//向数组中添加元素
			});
			if(id_array.length == 0){
				layer.msg("请选择售货机之后再进行导出.",{shade: [0.5, '#fff'],time:2000});
				return;
			}
			window.location = "/deliveryTask/exportDelivery?taskVmIds="+id_array.join(",");
		});
	};

	var otherInfo = function(){
		$('#otherInfoBtn').click(function(){
			var ids =$("#vmVal").val();
			if(2 == ids){
				$("#adjustment").attr("disabled", true);
			}else{
				$("#adjustment").attr("disabled", false);
			}
			$('#otherInfoWrap').toggle();
		});
	}
	var handBtnGroup = function(){
		$('#handBtnGroup label').click(function(){
			var inputVal = $(this).find('input[type=radio]:checked').val();
			if(inputVal == '1'){
				$(this).siblings('.handBtn').show();
			}else{
				$(this).siblings('.handBtn').hide();
			}
		});
	}

	var selectVm = function(){
		$("#selectVm").bind("click",function () {
			if($("#otherInfoWrap").show()){
				$("#otherInfoWrap").hide();
			}
		});
	}

	var checkAdJustModel = function () {
		//blur
		$('#adJustModel').on('blur','.realClearVal',function () {
			var currNum = $(this).parents('tr').find('input[name="currNum"]');
			if(eval($(this).val()) > eval(currNum.val())){
				layer.msg("清货量不能大于当前余量",{shade: [0.5, '#fff'],time:2000});
				$(this).val('0');
			}
		})
	}

	return{
		init:function(){
			bindSearch();
			reset();
			calcelEvent();
			exportDelivery();
			checkAdJustModel();
		},
		addTask:function(){
			submitAddTask();
		},
		selectMtVm:function(){
			bindSearchMtVm();
		},
		editTaskVm:function(){
			submitEditTaskVm();
		},
		adJustModel:function(){
			adjust();
		},
		cheaddTask:function(){
			checkedAddTask();
		},
		otherClick:function(){
			otherInfo();
			handBtnGroup();
			selectVm();
		},
	}
}();