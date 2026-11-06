var pid;
$(function(){
	
	$("#searchButton").bind("click", function() {
		searchEvent();
	});
	
	$("#dictionary_list_button").bind("click", function() {
		initDictionary(pid);
	});
	
	initSysType();
    
    $('#deleteSysType').click(function(){
    	var sysTypeIds = new Array();
		$('#pagediv1 input[type="checkbox"]:checked').each(function(i){
			sysTypeIds.push($(this).val());
		});
		if(sysTypeIds.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
		layer.confirm('删除可能会影响系统功能', {
			  btn: ['继续','放弃'],
			  title: '警告',
			}, function(){ //成功后执行
			  //这里ajax处理服务端删除
				$.ajax({
			        cache: false,
			        type: "POST",
			        url:"/sysType/delete.do",
			        data:{"ids":sysTypeIds.join()},
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
			}, function(index){
			  layer.close(index);//关闭窗口
			});
    });
    
    $('#deleteDictionary').click(function(){
    	var dictionaryIds = new Array();
		$('#pagediv2 input[type="checkbox"]:checked').each(function(i){
			dictionaryIds.push($(this).val());
		});
		if(dictionaryIds.length == 0){
			layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
			return ;
		}
    	$.ajax({
	        cache: false,
	        type: "POST",
	        url:"/dictionary/delete.do",
	        data:{"ids":dictionaryIds.join()},
	        async: false,
	        error: function(request) {
	        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
	        },
	        success: function(data) {
	        	if(data){
	        		layer.msg("删除成功！",{shade: [0.5, '#fff'],time:2000,});
	        		initDictionary(pid);
	        	}else{
	        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
	        	}
	        }
	    });
    });
    
    
})

function initSysType(){
	$(document).on("click",".sysTypeTable tr",function() {
		pid = $(this).find('input[type="checkbox"]').val();
    	initDictionary(pid);
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
			$("#pagediv1").html(data);
		}
	};
	$("#search-form").ajaxSubmit(options);
}

function initDictionary(id){
	$.ajax({
        cache: false,
        type: "POST",
        url:"/dictionary/list.do",
        data:$('#dictionary_list_form').serialize()+"&tSysTypeInfoId="+id,
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$("#pagediv2").html(data);
        }
    });
}

function addSysType(){
	if(!checkCode($('#addSysType_form input[name="typeCode"]').val(), "sysType")){
		return ;
	}
	$.ajax({
        cache: false,
        type: "POST",
        url:"/sysType/create.do",
        data:$('#addSysType_form').serialize(),
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$('#addSysType').modal('hide') ;
        	if(data != null && data != ''){
        		layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
        		searchEvent();
        		//initDictionary(pid);
        	}else{
        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
}

function updateSysType(){
	$.ajax({
        cache: false,
        type: "POST",
        url:"/sysType/update.do",
        data:$('#updateSysType_form').serialize(),
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$('#updateSysType').modal('hide') ;
        	if(data != null && data != ''){
        		layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
        		searchEvent();
        		//initDictionary(pid);
        	}else{
        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
}


function addDictionary(){
	if(pid == null){
		$('#addType').modal('hide') ;
		layer.msg("请先点击左边的商品类型组！",{shade: [0.5, '#fff'],time:1000,});
		return ;
	}
	if(!checkCode($('#addDictionary_form input[name="dictionaryCode"]').val(), "dictionary")){
		return ;
	}
	$.ajax({
        cache: false,
        type: "POST",
        url:"/dictionary/create.do",
        data:$('#addDictionary_form').serialize()+"&tSysTypeInfoId="+pid,
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$('#addDictionary').modal('hide') ;
        	if(data != null && data != ''){
        		layer.msg("创建成功！",{shade: [0.5, '#fff'],time:2000,});
        		initDictionary(pid);
        	}else{
        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
}

function updateDictionary(){
	$.ajax({
        cache: false,
        type: "POST",
        url:"/dictionary/update.do",
        data:$('#updateDictionary_form').serialize(),
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$('#updateDictionary').modal('hide') ;
        	if(data != null && data != ''){
        		layer.msg("修改成功！",{shade: [0.5, '#fff'],time:2000,});
        		initDictionary(pid);
        	}else{
        		layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
}

function checkCode(val ,url){
	var b = false;
	if(val == null || val == ''){
		return ;
	}
	$.ajax({
        cache: false,
        type: "POST",
        url:"/"+url+"/isNotExist.do",
        data:{"code":val,"typeInfoId":pid},
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	if(data){
        		b=true;
        	}else{
        		layer.msg("该code已存在，请换一个",{shade: [0.5, '#fff'],time:2000,});
        	}
        }
    });
	return b;
}


