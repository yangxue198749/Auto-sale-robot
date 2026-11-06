$(function(){
	

	
})

function add(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/businessDistrict/create.do",
		data:$('#add_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#add').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function del(){
	var ids = new Array();
	$('#pagediv input[type="checkbox"]:checked').each(function(i){
		ids.push($(this).val());
	});
	if(ids.length == 0){
		layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
		return ;
	}
	$.ajax({
		cache: false,
		type: "POST",
		url:"/businessDistrict/delete.do",
		data:{"ids":ids.join()},
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			if(data){
				layer.msg("删除成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function update(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/businessDistrict/update.do",
		data:$('#update_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#update').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

//查询
function searchEvent() {
	$.ajax({
		cache: false,
		type: "POST",
		url:"/businessDistrict/list.do",
		data:{},
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			var hasLogin = Metronic.hasLogin();
        	if(hasLogin==false){
        		window.location.href="/";
        		return;
        	}
			$("#pagediv").html(data);
		}
	});
}