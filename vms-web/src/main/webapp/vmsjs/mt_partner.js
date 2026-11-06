$(function(){
	$("#searchButton").bind("click", function() {
		searchEvent();
	});
	
	$("#deleteButton").bind("click", function() {
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
            url:"/partner/delete.do",
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
	})
	
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

function addPartner(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/partner/create.do",
		data:$('#addPartner_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#addPartner').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function updatePartner(){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/partner/update.do",
		data:$('#updatePartner_form').serialize(),
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#updatePartner').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("编辑成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
		}
	});
}

function reset(){
//	$('#search-form input').val("");
	$('#search-form').reset();
}