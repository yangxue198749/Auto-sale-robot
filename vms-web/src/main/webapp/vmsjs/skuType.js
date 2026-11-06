var pid;
$(function(){
	init();
	
	//创建类型组
	$('#addTypeGroup_form_button').bind("click", function() {
		var skuTypeName = $('#addTypeGroupName').val();
		if(skuTypeName == null || skuTypeName == ""){
			layer.msg("名字不能为空！",{shade: [0.5, '#fff'],time:1000,});
			return;
		}
		$.ajax({
            cache: false,
            type: "POST",
            url:"/skuType/create.do",
            data:$('#addTypeGroup_form').serialize(),
            async: false,
            error: function(request) {
            	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
            },
            success: function(data) {
            	$('#addTypeGroup').modal('hide') ;
            	if(data != null && data != '' ){
            		layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
            		$("#pagediv1").html(initType(null));
            		init();
            	}else{
            		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
            	}
            }
        });
	});
	
	//创建类型
	$('#addType_form_button').bind("click", function() {
		var skuTypeName = $('#addTypeName').val();
		if(skuTypeName == null || skuTypeName == ""){
			layer.msg("名字不能为空！",{shade: [0.5, '#fff'],time:1000,});
			return;
		}
		if(pid == null){
			$('#addType').modal('hide') ;
			layer.msg("请先点击左边的商品类型组！",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		$.ajax({
            cache: false,
            type: "POST",
            url:"/skuType/createType.do",
            data:$('#addType_form').serialize()+"&pid="+pid,
            async: false,
            error: function(request) {
            	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
            },
            success: function(data) {
            	$('#addType').modal('hide') ;
            	if(data != null && data != '' ){
            		layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
            		$("#pagediv2").html(initType(pid));
            	}else{
            		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
            	}
            }
        });
	});
	
	//删除商品类型组
	$("#deleteTypeGroupButton").bind("click", function() {
		var skuTypeGroupIds = new Array();
		$('input:checkbox[name=skuTypeGroupIds]:checked').each(function(i){
			skuTypeGroupIds.push($(this).val());
		});
		if(skuTypeGroupIds.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		if(deleteSkuType(skuTypeGroupIds ,"/skuType/delete.do")){
			$("#pagediv1").html(initType(null));
			init();
		}
	});
	
	//删除商品类型
	$("#deleteTypeButton").bind("click", function() {
		var skuTypeIds = new Array();
		$('input:checkbox[name=skuTypeIds]:checked').each(function(i){
			skuTypeIds.push($(this).val());
		});
		if(skuTypeIds.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		if(deleteSkuType(skuTypeIds ,"/skuType/deleteType.do")){
			$("#pagediv2").html(initType(pid));
		}
	});
	
	//修改商品类型组
	$("#updateTypeGroup_form_button").bind("click", function() {
		var skuTypeName = $('#updateTypeGroup_name').val();
		if(skuTypeName == null || skuTypeName == ""){
			layer.msg("名字不能为空！",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		var skuTypeGroup = $('input:checkbox[name=skuTypeGroupIds]:checked');
		if(skuTypeGroup == null){
			return ;
		}
		if(updateSkuType($(skuTypeGroup).val() , skuTypeName , $('#updateTypeGroup') ,"/skuType/update.do")){
			$("#pagediv1").html(initType(null));
			init();
		}
	});
	
	//修改商品类型
	$("#updateType_form_button").bind("click", function() {
		var skuTypeName = $('#updateType_name').val();
		if(skuTypeName == null || skuTypeName == ""){
			layer.msg("名字不能为空！",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		var skuTypeGroup = $('input:checkbox[name=skuTypeIds]:checked');
		if(skuTypeGroup == null){
			return ;
		}
		if(updateSkuType($(skuTypeGroup).val() , skuTypeName , $('#updateType') ,"/skuType/updateType.do")){
			$("#pagediv2").html(initType(pid));
		}
	});
	
})

function init(){
	$(document).on("click","tr.type_group",function() {
		pid = $(this).find('input[type="checkbox"]').val();
		$("#pagediv2").html(initType(pid));
	});
}

function updateSkuType(skuTypeId ,skuTypeName , model ,url){
	var b = false;
	$.ajax({
        cache: false,
        type: "POST",
        url:url,
        data:{"skuTypeId":skuTypeId ,"skuTypeName":skuTypeName},
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$(model).modal('hide') ;
        	if(data != null && data != '' ){
        		b = true;
        		layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
        	}else{
        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
	return b;
}

function deleteSkuType(skuTypeIds ,url){
	var b = false;
	$.ajax({
        cache: false,
        type: "POST",
        url:url,
        data:{"skuTypeIds":skuTypeIds.join()},
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	if(data){
        		b = true;
        		layer.msg("删除成功！",{shade: [0.5, '#fff'],time:2000,});
        	}else{
        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
	return b;
}

function initType(pid){
	var returnDate;
	$.ajax({
        cache: false,
        type: "POST",
        url:"/skuType/list.do",
        data:{"pid":pid},
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	returnDate =  data;
        }
    });
	return returnDate;
}

//重置
function reset( form ){
	$('#'+form+' input').val("");
}



// 渲染大类
function renderTypeGroup(){
    var provinceOption = "<option value=''>请选择</option>";
    $("#typeGroupAdd").append(provinceOption);
    var checked=$("#typeGroupAdd").attr('value');
    for(var i=0; i<skuJson.length; i++){

        if(skuJson[i].pid == 0){
            var provinceOption = "<option value="+skuJson[i].id+">"+skuJson[i].name+"</option>";
            if(skuJson[i].id == checked){
                provinceOption = "<option value="+skuJson[i].id+" selected>"+skuJson[i].name+"</option>";
            }
            $("#typeGroupAdd").append(provinceOption);
        }
    }

}

// 渲染小类
function renderType(groupId){
    var checked=$("#typeAdd").attr('value');
    $("#typeAdd").empty();
    var citySelect = "<option value=''>请选择</option>";
    $("#typeAdd").append(citySelect);
    for(var i=0; i<skuJson.length; i++){

        if(skuJson[i].pid == groupId&&skuJson[i].pid!=0){
            var cityOption = "<option value="+skuJson[i].id+">"+skuJson[i].name+"</option>";
            if(skuJson[i].id == checked){
                cityOption = "<option value="+skuJson[i].id+" selected>"+skuJson[i].name+"</option>";
            }
            $("#typeAdd").append(cityOption);
        }
    }
}


// 刷新小类
function refreshRenderType(){

    var selectedProvinceId = $("#typeGroupAdd").val();

    renderType(selectedProvinceId);
}

