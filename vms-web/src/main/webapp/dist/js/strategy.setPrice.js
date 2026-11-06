$(function(){
  
  //新建价格策略 setPrice

    //新增点击下一步
  $("#addPrice").on("click",".step_info .nextbtn",function(){
      var strategyName=$("#strategyName");
      if(strategyName.val().trim()==''){
          strategyName.closest('.form-group').removeClass('has-success').addClass('has-error');
      }
      else{
          strategyName.closest('.form-group').removeClass('has-error'); // set error class to the control group
          $("#addPrice .step_info, #addPrice .step_relation").hide().siblings("#addPrice .step_choice").show();
      }

  });

  //添加商品
  $("#addPrice").on("click",".step_choice .addbtn",function(){
      var skuIds=$("input[name='skuIds']").map(function(){ return $(this).val(); }).get();
      $(this).parents(".step_choice").hide();
      $(this).parents(".step_choice").siblings("#addPrice .step_relation").load(path+"/stSkuPrice/sku?skuIds="+skuIds);
      $(this).parents(".step_choice").siblings("#addPrice .step_relation").show();
  });
    //筛选商品
    $("#addPrice").on("click"," .step_relation .sku_search",function(){
        var skuIds=$(".step_choice input[name='skuIds']").map(function(){ return $(this).val(); }).get();
        var skuName=$("#skuName").val();
        var skuCode=$("#skuCode").val();
        var skuGroupTypeId=$("#typeGroupAdd").val();
        var skuTypeId=$("#typeAdd").val();
        $("#addPrice .step_relation").load(encodeURI(encodeURI(path+"/stSkuPrice/sku?skuIds="+skuIds+"&skuName="+skuName+ "&skuCode="+ skuCode +"&skuGroupTypeId="+skuGroupTypeId+"&skuTypeId="+skuTypeId)));
    });
  //选择商品 确定
  $("#addPrice").on("click",".step_relation .confirmbtn",function(){
      /*var skuId=$("#skuInfo").find(".on input[name=skuId]").val();
      if(null==skuId){
          layer.msg("请先选择商品",{shade: [0.5, '#fff'],time:2000});
          return false;
      }
      var offerPrice=$("#skuInfo").find(".on input[name=offerPrice]").val();
      var skuName=$("#skuInfo").find(".on input[name=skuName]").val();
      console.log("skuId =" +skuId);
      console.log("offerPrice =" +offerPrice);
      console.log("skuName =" +skuName);*/
      var checkedSkuId = $("#skuInfo li.on").find("input[name=skuId]");
      if(checkedSkuId.length == 0 || checkedSkuId == null){
          layer.msg("请先选择商品",{shade: [0.5, '#fff'],time:2000});
          return false;
      }else{
          $('#skuInfo li.on').each(function () {
              var skuId = $(this).find("input[name=skuId]").val();
              var offerPrice = $(this).find("input[name=offerPrice]").val();
              var skuName = $(this).find("input[name=skuName]").val();
              var skuInfoHtml = '<tr id="'+ skuId +'">'+
                  '<input type="hidden" value="'+ skuId +'" id="skuId${vs.index}" name="skuIds"/>'+
                  '<td class="wh40">'+ skuName +'</td>'+
                  '<td class="wh20">'+ offerPrice +'</td>'+
                  '<td class="wh20"><input class="form-control input-sm w100 auto" type="text" placeholder="请输入售价" value="'+ offerPrice +'"  name="realPrices" required min="1" digits="true"></td>'+
                  '<td class="wh20"><button type="button" class="btn btn-danger f12 btn-sm btn_sku_delete">删除</button></td>'+
                  '</tr>';
              $("#skuPriceDiv tbody").prepend(skuInfoHtml);
          });
          $(this).parents(".step_relation").hide().siblings("#addPrice .step_choice").show();
      }

      /*$("#skuPriceDiv").find("tbody").prepend('<tr id="'+skuId+'">'+
          '<input type="hidden" value="'+skuId+'" id="skuId${vs.index}" name="skuIds"/>'+
          '<td class="wh40">'+skuName+'</td>'+
          '<td class="wh20">'+offerPrice+'</td>'+
          '<td class="wh20"><input class="form-control input-sm w100 auto" type="text" placeholder="请输入售价" value="'+offerPrice+'"  name="realPrices" required min="1" digits="true"></td>'+
          '<td class="wh20"><button type="button" class="btn btn-danger f12 btn-sm btn_sku_delete">删除</button></td>'+
          '</tr>');
      $(this).parents(".step_relation").hide().siblings("#addPrice .step_choice").show(); */
  });
  //选择商品 关闭
  $("#addPrice").on("click",".step_relation .closebtn",function(){
      $(this).parents(".step_relation").hide().siblings("#addPrice .step_choice").show();
  });
    //删除商品
    $("#addPrice").on("click",".btn_sku_delete",function(){
        $(this).parents("td").parents("tr").remove();
    });
  //选择商品 第三步选择商品
  $("#addPrice").on("click",".choice_relation li",function(){
      //$(this).addClass("on").siblings().removeClass("on");
      //多选
      $(this).toggleClass('on');
  });
  //重置窗口
  $("#addPrice").on("click",".restbtn",function(){
      $("#addPrice").find(".step_info").show().siblings("#addPrice .step_choice, #addPrice .step_relation").hide();
  });
  
  
});