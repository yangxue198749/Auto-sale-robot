$(function(){
	
	$("#searchButton").bind("click", function() {
		searchEvent();
	});
	
	$('#updateModelTable').bind("click",function(){
		var areaArray = new Array();
		$("input:checkbox[name=vmModelCheckbox]:checked").each(function() {
			areaArray.push($(this).val());
		})
		if(areaArray.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		if(areaArray.length != 1){
			layer.msg("请选择一条数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		$.ajax({
			cache: false,
			type: "POST",
			url:"/vmModelChannel/updatePage.do",
			data:{"ids":areaArray[0]},
			async: false,
			error: function(request) {
				
			},
			success: function(data) {
				if(data == null || data == ""){
					layer.msg("此售货机货道已被应用，不能修改！",{shade: [0.5, '#fff'],time:2000,});
				}else{
					$('#editModelTable').modal('show') ;
					$('#editModelTable').html(data);
				}
			}
		});
	});
	
	$('#deleteVmModel').bind("click",function(){
		var areaArray = new Array();
		$("input:checkbox[name=vmModelCheckbox]:checked").each(function() {
			areaArray.push($(this).val());
		})
		if(areaArray.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		if(areaArray.length != 1){
			layer.msg("请选择一条数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		
		$.ajax({
			cache: false,
			type: "POST",
			url:"/vmModel/delete.do",
			data:{"ids":areaArray[0]},
			async: false,
			error: function(request) {
				
			},
			success: function(data) {
				if(data == null || data == ''){
					layer.msg("此售货机型号已被应用，不能删除！",{shade: [0.5, '#fff'],time:2000,});
				}else if(data){
					layer.msg("删除成功！",{shade: [0.5, '#fff'],time:2000,});
					searchEvent();
				}else{
					layer.msg("删除失败！",{shade: [0.5, '#fff'],time:2000,});
				}
			}
		});
	});


	$('#lockedOrUnModel').bind("click",function(){
		var areaArray = new Array();
		$("input:checkbox[name=vmModelCheckbox]:checked").each(function() {
			areaArray.push($(this).val());
		})
		if(areaArray.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000});
			return ;
		}
		if(areaArray.length != 1){
			layer.msg("请选择一条数据",{shade: [0.5, '#fff'],time:1000});
			return ;
		}

		$.ajax({
			cache: false,
			type: "POST",
			url:"/vmModel/lockedOrUnlocked.do",
			data:{"ids":areaArray[0]},
			async: false,
			error: function(request) {

			},
			success: function(data) {
				if("success" == data.type){
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
					searchEvent();
				}else{
					layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
				}
			}
		});
	});

	
})

//查询
function searchEvent() {
	var options = {
		type : 'POST',
		beforeSubmit : function() {},
		success : function(data) {
			var hasLogin = Metronic.hasLogin();
        	if(hasLogin==false){
        		window.location.href="/";
        		return;
        	}
			$("#pagediv").html(data);
		}
	};
	$("#search-form").ajaxSubmit(options);
}

/**
 * 查看其它
 */
function lookOtherInfo( look ){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModel/lookOtherPage.do",
		data:{"id":$(look).attr('name')},
		async: false,
		error: function(request) {
			
		},
		success: function(data) {
			$('#lookOtherInfo').html(data);
//			$('#checkInfo').modal('in') ;
		}
	});
}

/**
 * 新建型号
*/
function addModel(){
	var b = true;
	
	var channelLayerQty = $('#channelLayerQty').val();//货道层数
	var channelPerLayers = $('#channelPerLayer').val().split(',');//货道各层数量
	if(channelPerLayers.length != channelLayerQty){
		layer.msg("货道层数与各货道层数不匹配！",{shade: [0.5, '#fff'],time:2000,});
		return ;
	}
	$.each(channelPerLayers ,function(i,val){
		if(!$.isNumeric(val )){
			layer.msg("各货道层数格式不正确！",{shade: [0.5, '#fff'],time:2000,});
			b = false;
			return false;
		}
	})
	if(!b){
		return ;
	}
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModel/create.do",
		data:$('#addModel_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(vmModelId) {
			$('#addModel').modal('hide') ;
			if(vmModelId != null){
				layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
				getAddModelChannelPage(vmModelId);
				$("#addModelTable").modal('show') ;
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function editModel(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModel/update.do",
		data:$('#editModel_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000});
		},
		success: function(date) {
			if(date != null && date != ''){
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000});
				$('#editModel').modal('hide') ;
				searchEvent();
				setTimeout(function() {
					window.location.reload();
				},1500);
				//window.location.reload();
			}else{
				layer.msg("失败,此售货机已被应用，不能修改！",{shade: [0.5, '#fff'],time:2000});
			}
		}
	});
}

/**
 * 新建型号配置
 */
function getAddModelChannelPage(vmModelId){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModelChannel/createPage.do",
		data:{"vmModelId":vmModelId},
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$("#addModelTable").html(data);
		}
	});
}

/**
 * 得到设置型号配置行属性页面
 */
function getModelChannelRowPage(button){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModelChannel/rowPage.do",
		data:{"args":$(button).attr('name')},
		async: false,
		error: function(request) {
			
		},
		success: function(data) {
			$('#modelTableRow').html(data);
		}
	});
}

/**
 * 设置型号配置行属性
 */
function setModelChannelRow(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModelChannel/setModelChannelRow.do",
		data:$('#modelTableRow_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#modelTableRow').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("设置成功！",{shade: [0.5, '#fff'],time:2000,});
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

/**
 * 得到设置型号配置属性页面
 */
function getModelChannelPage(button){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModelChannel/page.do",
		data:{"args":$(button).attr('name')},
		async: false,
		error: function(request) {
			
		},
		success: function(data) {
			$('#modelTable').html(data);
		}
	});
}

/**
 * 设置型号配置属性
 */
function setModelChannel(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/vmModelChannel/setModelChannel.do",
		data:$('#modelTable_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#modelTable').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("设置成功！",{shade: [0.5, '#fff'],time:2000,});
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function isFixedCapacity(capacityVal){
    if(capacityVal == 0){
    	$("#fixedCapacity").val("").attr("disabled","disabled");
    	$("#fixedCapacity").removeAttr("required");
    	$("#fixedCapacity").removeAttr("digits");
    	$("#fixedCapacity").removeAttr("data-msg-digits");
    }
    if(capacityVal == 1){
    	$("#fixedCapacity").val("").removeAttr("disabled");
    	$("#fixedCapacity").attr("required","true");
    	$("#fixedCapacity").attr("digits","true");
    	$("#fixedCapacity").attr("data-msg-digits","只能输入整数");
    }
}










