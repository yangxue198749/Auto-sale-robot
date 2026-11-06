$(function(){
	
	$("#searchButton").bind("click", function() {
		searchEvent();
	});
	
	
})

/**
 * 查看其它
 */
function lookUniteArea( look ){
	$.ajax({
		cache: false,
		type: "POST",
		url:"/warehouse/lookUniteArea.do",
		data:{"warehouseId":$(look).attr('name')},
		async: false,
		error: function(request) {
			
		},
		success: function(data) {
			$('#lookUniteArea').html(data);
//			$('#checkInfo').modal('in') ;
		}
	});
}

function uniteArea(){
	var areaArray = new Array();
	$("input:checkbox[name=areaCheckbox]:checked").each(function() {
		areaArray.push($(this).val());
	})
//	if(areaArray.length == 0){
//		layer.msg("请选择数据",{shade: [0.5, '#fff'],time:1000,});
//		return ;
//	}
	var warehouseId = $("#uniteArea_form input[name='warehouseId']").val();
	var cityId = $("#citySelect").val();
	$.ajax({
		cache: false,
		type: "POST",
		url:"/warehouse/uniteArea.do",
		data:{"areaArray":areaArray.join(),"warehouseId":warehouseId,"cityId":cityId},
		async: false,
		error: function(request) {
			layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
		},
		success: function(data) {
			$('#uniteArea').modal('hide') ;
			if(data != null && data != ''){
				layer.msg("关联区域成功！",{shade: [0.5, '#fff'],time:2000,});
				searchEvent();
			}else{
				layer.msg("失败",{shade: [0.5, '#fff'],time:2000,});
			}
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

//得到区域
function getCountry(){
	var cityId = $("#citySelect").val();
    $("#country").empty();
    
    var countrySelect = "<option value=''>请选择</option>";
    $("#countrySelect").append(countrySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == cityId){
        	
        	$.each(usedIdList ,function(int ,map){
        		if(map.areaId == areaJson[i].id && map.wareHouseId != warehouseId){
        			return false;
        		}
        		if(int == usedIdList.length - 1 || (map.areaId == areaJson[i].id && map.wareHouseId == warehouseId) ){
        			var input = $("<input/>");
                	input.attr("type","checkbox");
                	input.attr("value",areaJson[i].id);
                	input.attr("name","areaCheckbox");
                	if(map.areaId == areaJson[i].id && map.wareHouseId == warehouseId){
                		input.attr("checked","checked");
                	}
                	
                	var td1 = $("<td></td>").append();
                	td1.attr("class","wh20");
                	td1.append(input);
                	
                	var td2 = $("<td></td>");
                	td2.attr("class","wh80");
                	td2.append(areaJson[i].name);
                	var tr = $("<tr></tr>").append(td1,td2);
                	$("#country").append(tr);
                	return false;
        		}
        	});
        }
    }
}