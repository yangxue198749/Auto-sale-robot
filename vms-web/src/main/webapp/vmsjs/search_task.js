var SearchTask = function(){
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
	return{
		init:function(){
			bindSearch();
			reset();
		},
	}
}();
var exportWorkOrder = function(){
	var id_array=new Array();
	$('#pagediv table tbody input[type="checkbox"]:checked').each(function(){
	    id_array.push($(this).val());//向数组中添加元素  
	});
	if(id_array.length == 0){
		layer.msg("请选择工单之后再进行导出.",{shade: [0.5, '#fff'],time:2000});
		return;
	}
	window.location = "../searchTask/exportWorkOrder?taskVmIds="+id_array.join(",");
}