$(function(){
	
	$("#searchButton").bind("click", function() {
		searchEvent();
	});
	
})

/**
 * 新建商品
*/
function addSku(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/sku/create.do",
		data:$('#addSku_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#addSku').modal('hide') ;
			$('#addSku').empty();
			if(data != null && data != ''){
				layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

/**
 * 修改商品
*/
function updateSku(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/sku/update.do",
		data:$('#updateSku_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#updateSku').modal('hide') ;
			$('#updateSku').empty();
			if(data != null && data != ''){
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

/**
 * 编辑商品
 */
function changeSku(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/sku/change.do",
		data:$('#changeSku_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#changeSku').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("编辑成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

/**
 * 查看其它
 */
function lookOther( look ){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/sku/lookOtherPage.do",
		data:{"ids":$(look).attr('name')},
		async: false,
		error: function(request) {
			
		},
		success: function(data) {
			$('#checkInfo').html(data);
//			$('#checkInfo').modal('in') ;
		}
	});
}

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

//渲染大类
function renderSkuTypeGroup(){
 var provinceOption = "<option value=''>请选择</option>";
 $("#skuTypeGroup").append(provinceOption);
 var checked=$("#skuTypeGroup").attr('value');
 for(var i=0; i<skuJson.length; i++){

     if(skuJson[i].pid == 0){
         var provinceOption = "<option value="+skuJson[i].id+">"+skuJson[i].name+"</option>";
         if(skuJson[i].id == checked){
             provinceOption = "<option value="+skuJson[i].id+" selected>"+skuJson[i].name+"</option>";
         }
         $("#skuTypeGroup").append(provinceOption);
     }
 }

}

//渲染小类
function renderSkuType(groupId){
 var checked=$("#skuType").attr('value');
 $("#skuType").empty();
 var citySelect = "<option value=''>请选择</option>";
 $("#skuType").append(citySelect);
 for(var i=0; i<skuJson.length; i++){

     if(skuJson[i].pid == groupId&&skuJson[i].pid!=0){
         var cityOption = "<option value="+skuJson[i].id+">"+skuJson[i].name+"</option>";
         if(skuJson[i].id == checked){
             cityOption = "<option value="+skuJson[i].id+" selected>"+skuJson[i].name+"</option>";
         }
         $("#skuType").append(cityOption);
     }
 }
}


//刷新小类
function refreshRenderSkuType(){

 var selectedProvinceId = $("#skuTypeGroup").val();

 renderSkuType(selectedProvinceId);
}

function empty(div){
	$(div).modal('hide') ;
	$(div).empty();
}