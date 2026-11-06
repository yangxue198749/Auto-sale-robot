$(function(){
  
  //新建价格策略 setFlashBuy
  //基础信息 下一步
  $("#setFlashBuy .step_info .nextbtn").click(function(){
      $("#setFlashBuy .step_info, #setFlashBuy .step_relation").hide().siblings("#setFlashBuy .step_choice").show();
  });
  //配置商品 第二步
  $("#setFlashBuy .step_choice .addbtn").click(function(){
      $(this).parents(".step_choice").hide().siblings("#setFlashBuy .step_relation").show();
  });
  //选择商品 第三步
  $("#setFlashBuy .step_relation .confirmbtn").click(function(){
      $(this).parents(".step_relation").hide().siblings("#setFlashBuy .step_choice").show(); 
  });
  //选择商品 第三步选择商品
  $("#setFlashBuy .choice_relation li").click(function(){
      $(this).addClass("on").siblings().removeClass("on"); 
  });
  //重置窗口
  $("#setFlashBuy .restbtn").click(function(){
      $("#setFlashBuy .step_info").show().siblings("#setFlashBuy .step_choice, #setFlashBuy .step_relation").hide();
  });

  
  
});