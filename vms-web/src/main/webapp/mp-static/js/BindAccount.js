$(function(){  
  //绑定账号
  $("#BindAccount").on("click",".submitbtn",function(){
	  var userAccount = $("#BindAccount .userAccount").val();
	  var userPassword = $("#BindAccount .userPassword").val();
	  if(userAccount == ""){
		  $.toast("请输入魔核账号", "text");
		  return false;
	  }
	  if(userPassword == ""){
		  $.toast("请输入密码", "text");
		  return false;
	  }
	  else{
		  $.showLoading(); //开启loading
		  var agree = true;
		  var errMsg = "";
		  $.ajax({
			  type : 'post',
			  data:{"username":userAccount,"password":userPassword},
			  url :'/mp/auth/bind',
			  async : false,
			  dataType : 'json',
			  cache : false,
			  success : function(data) {
				  if(data.type == "error"){
					  agree = false;
					  errMsg = data.message;
					  $.hideLoading();
					  $.toast(errMsg, "text");
				  }
				  else{
					  $.hideLoading();
					  $("#BindAccount .accountbox").empty().append('<div class="bindstatus"><p class="mb10"><i class="weui_icon_msg weui_icon_success"></i></p><p>恭喜，账号绑定成功！</p></div><a class="weui_btn weui_btn_default2" href="javascript:" id="closeWindow">返回</a>');
				  }
			  }
		  });

	  }
  
  });
	//关闭
	$(document).on("click","#closeWindow",function(){
		WeixinJSBridge.call('closeWindow');
	});


})