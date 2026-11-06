$(function(){
	//处理TAB导航
	$(".SendTabHeader").on("click","li",function(){
        $(this).addClass("on").siblings().removeClass("on");
		$('.SendTabBody .status_wrap').hide().eq($('.SendTabHeader li').index(this)).show(); 
	});
	//选中处理
	$(document).on("click",".checkbox_wp",function(){
		$(this).find(".weui_icon_check").toggleClass("checked");
	});
	$(".touchbtn").on("click",function(){
		var taskId = $("#taskVmId").val();
		//before
		$(this).text('密码生成中').attr({ disabled: "disabled" });
		$.ajax({
			type : 'post',
			data:{"taskId":taskId},
			url :'/mp/mpTask/lock',
			async : false,
			dataType : 'json',
			cache : false,
			success : function(data) {
				// $(this).siblings(".keycode").html(data.text);
				$("#pwd").html(data.text);
				//after
                $('.touchbtn').text('生成开门密码').removeAttr('disabled');
			}
		});

	})

})