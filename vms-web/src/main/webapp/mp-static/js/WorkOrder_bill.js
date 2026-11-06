$(function(){
	//调整余量 new
	$(".dialogWidget").on("click",".dialogBtn",function(){
		var thisBtn = $(this).parents(".dialogWidget");
		$.prompt({
			//text: "",
			title: "调整余量",
			onOK: function(numVal) {
				//newNum.html(numVal);
				thisBtn.find(".dialogNum").html(numVal);
				//$.alert(numVal);
			},
			onCancel: function() {
				//console.log("取消了");
			},
			//input: '1'
		});
		$("#weui-prompt-input").remove();  //移除原始输入框
		$(".weui_dialog_bd").empty().append('<div class="promptInput_wrap"><input id="TapMinus" type="button" class="tapbtn" value="-"><input class="weui_input weui-prompt-input num" id="weui-prompt-input" type="text" value="" readonly><input id="TapAdd" class="tapbtn rt" type="button" value="+"></div>');  //添加新input控件
		//处理 提货控件
		var getNumVal = thisBtn.find(".dialogNum").text();  // 获取余量的数值
		var getFullNumVal = thisBtn.find(".dialogFullNum").text();  // 获取满量的数值值
		var numVal = $("#weui-prompt-input");  //dialog 的 input
		numVal.val(parseInt(getNumVal));  //获取余的数值 - 给dialog里的input  即 当前
		$("#TapAdd").click(function(){   //增量按钮
			if(parseInt(numVal.val()) >= parseInt(getFullNumVal)) {   //如果当前值大于等于满量
				$(this).attr("disabled","disabled").css("background","#ccc");  //销毁增量按钮
				return false;
			}else{
				numVal.val(parseInt(numVal.val())+1);  //增量+1
				$("#TapMinus").removeAttr("disabled").css("background","#1fc38b");  //激活减量按钮
			}
		});
		$("#TapMinus").click(function(){   //减量按钮
			if(parseInt(numVal.val()) > 0) {    //如果当前值大于1
				numVal.val(parseInt(numVal.val())-1);  //减量-1
				$("#TapAdd").removeAttr("disabled").css("background","#1fc38b");  //激活增量按钮
			}else{     //如果当前值小于1
				$(this).attr("disabled","disabled").css("background","#ccc");  //销毁减量按钮
			}
		});
	});

	/*
	  //处理提货量
      $(".dialogWidget").on("click",".dialogBtn",function(){
		var newNum = $(this).parents(".dialogWidget").find(".dialogNum");
        $.prompt({
			//text: "",
			title: "建议提货量",
			onOK: function(numVal) {
				newNum.html(numVal);
				//$.alert(numVal);
			},
			onCancel: function() {
				//console.log("取消了");
			},
			  //input: '1'
         });
		 //移除原始输入框
		 $("#weui-prompt-input").remove();
   		 $(".weui_dialog_bd").append('<div class="promptInput_wrap"><input id="TapMinus" type="button" class="tapbtn" value="-"><input class="weui_input weui-prompt-input num" id="weui-prompt-input" type="text" value="1" readonly><input id="TapAdd" class="tapbtn rt" type="button" value="+"></div>');
		 //处理 提货控件
		 var numVal = $("#weui-prompt-input");  //dialog 的 input
		 var getNumVal = $(this).parents("li").siblings().find(".dialogNum").text();  // 提货量的数值
		 numVal.val(getNumVal);  //获取提货量的数值 - 给dialog里的input
		 $(document).on("click","#TapAdd",function(){  
			numVal.val(parseInt(numVal.val())+1);  
			$("#TapMinus").removeAttr("disabled").css("background","#1fc38b");
		 })  
		 $(document).on("click","#TapMinus",function(){  
		    if(parseInt(numVal.val())>1) {  
				numVal.val(parseInt(numVal.val())-1)  
			 }else{  
				$("#TapMinus").attr("disabled","disabled").css("background","#ccc");  
		    } 
		 }) 
      });
      */
	  
})