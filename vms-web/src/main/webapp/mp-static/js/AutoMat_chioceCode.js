$(function(){
  //选择编号
  $("#chioceCode").on("click", ".codeplace", function(){
	   $("#AutoMatlist").removeClass("hide");
	   //选择编号列表操作
	   $("#AutoMatlist").on("click", ".AutoMatlist_Submit", function(){
	       var AutoMatChecked = $("#AutoMatlist input[type='radio']:checked");
		   var childCode = AutoMatChecked.parents("label").find(".child_code").text();
		   var childLocation = AutoMatChecked.parents("label").find(".child_location").text();
		   var vmId = AutoMatChecked.parents("label").find(".vmId").val();
		   var taskId = AutoMatChecked.parents("label").find(".taskId").val();
		   $("#AutoMatlist").removeClass("hide");
		   if(AutoMatChecked.length == 0){
			   $.toast("请选择一个编号", "text");
			}else{
			   $("#chioceCode .codeplace").empty().append('<p><b class="f16">'+ childCode +'</b></p><p class="f12 cf9">'+ childLocation +'</p><input type="hidden" value="'+taskId+'"/>');
			   $("#AutoMatlist").addClass("hide");
			}
	   })
  });
  
  //激活售货机
  $(document).on("click","#activAutoMat_btn",function(){
	  var codePlace = $("#chioceCode .codeplace input").val();
	  var HardwareCode = $("#HardwareCode").val();
	  var uniqueCode = $("#uniqueCode").val();
	  if(codePlace == undefined){
		  $.toast("请选择售货机编号", "text");
		  return false;
	  }
	  if(HardwareCode == ""){
		  $.toast("请输入硬件编号", "text");
		  return false;
	  }
	  else{
		  var agree = true;
		  var errMsg = "";
		  $.ajax({
			  type : 'post',
			  data:{"taskId":codePlace,"hardwareCode":HardwareCode,"uniqueCode":uniqueCode},
			  url :'/mp/activate/active',
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
					  window.location.href="/mp/activate/result?taskId="+codePlace;
				  }
			  }
		  });
	     // window.location.href="AutoMat_chioceCode_article.html";
	  }
  });
  
  
})