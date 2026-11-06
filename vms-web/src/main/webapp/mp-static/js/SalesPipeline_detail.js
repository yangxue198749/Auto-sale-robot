$(function(){
	//$("#dateToday").val(todayDate());
})

//设置日期默认值
function todayDate(){
	var mydate = new Date();
	var str = "" + mydate.getFullYear() + "-";
	str += (mydate.getMonth()<9?"0"+(mydate.getMonth()+1):(mydate.getMonth()+1)) + "-";
	str += mydate.getDate()<10?"0"+mydate.getDate():mydate.getDate();
	return str;
}

function setDate( day ){
	var time = addDate($("#dateToday").val(),day);
	orderList(time);
//	$("#dateToday").val(time);
}

function addDate(date,days){ 
	var d=new Date(date); 
	d.setDate(d.getDate()+days); 
	var month=d.getMonth()+1; 
	var day = d.getDate(); 
	if(month<10){ 
		month = "0"+month; 
	} 
	if(day<10){ 
		day = "0"+day; 
	} 
	var val = d.getFullYear()+"-"+month+"-"+day; 
	return val; 
}

function orderList(time){
	$.ajax({
        cache: false,
        type: "POST",
        url:"/mp/slOrder/getOrderDetailList",
        data:{"time":time ,"vmId":$('#vmId').val()},
        async: false,
        error: function(request) {
        	layer.msg("系统异常，失败",{shade: [0.5, '#fff'],time:2000,});
        },
        success: function(data) {
        	$("#pagediv").html(data);
//        	$("#dateToday").val(time);
        }
    });
}