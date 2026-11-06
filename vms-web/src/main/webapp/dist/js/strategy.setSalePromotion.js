$(function(){

    //新建价格策略 setPrice

    //新增点击下一步
    $("#addSalePromotion").on("click",".step_info .nextbtn",function(){
        var strategyName=$("#strategyName");
        if(strategyName.val().trim()==''){
            strategyName.closest('.form-group').removeClass('has-success').addClass('has-error');
        }
        else{
            strategyName.closest('.form-group').removeClass('has-error'); // set error class to the control group
            $("#addSalePromotion .step_info, #addSalePromotion .step_relation").hide().siblings("#addSalePromotion .step_choice").show();
        }

    });

    //添加商品
    $("#addSalePromotion").on("click",".step_choice .addbtn",function(){
        var skuIds=$("input[name='skuIds']").map(function(){ return $(this).val(); }).get();
        $(this).parents(".step_choice").hide();
        $(this).parents(".step_choice").siblings("#addSalePromotion .step_relation").load(path+"/stSkuEvent/sku?skuIds="+skuIds);
        $(this).parents(".step_choice").siblings("#addSalePromotion .step_relation").show();
    });
    //筛选商品
    $("#addSalePromotion").on("click"," .step_relation .sku_search",function(){
        var skuIds=$(".step_choice input[name='skuIds']").map(function(){ return $(this).val(); }).get();
        var skuName=$("#skuName").val();
        var skuCode=$("#skuCode").val();
        var skuGroupTypeId=$("#typeGroupAdd").val();
        var skuTypeId=$("#typeAdd").val();
        $("#addSalePromotion .step_relation").load(encodeURI(encodeURI(path+"/stSkuEvent/sku?skuIds="+skuIds+"&skuName="+skuName+"&skuCode="+skuCode + "&skuGroupTypeId="+skuGroupTypeId+"&skuTypeId="+skuTypeId)));
    });
    //选择商品 确定
    $("#addSalePromotion").on("click",".step_relation .confirmbtn",function(){
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
                    '<td class="wh20"><select class="form-control input-sm" name="discountTypes"><option value="1">百分比</option><option value="2" >绝对值</option></select></td>'+
                    '<td class="wh20"><input class="form-control input-sm w100 auto" type="text" placeholder="请输入售价" value="100"  name="discountValues" required min="0" digits="true"></td>'+
                    '<td class="wh20"><button type="button" class="btn btn-danger f12 btn-sm btn_sku_delete">删除</button></td>'+
                    '</tr>';
                $("#skuEventDiv tbody").prepend(skuInfoHtml);
            });
            $(this).parents(".step_relation").hide().siblings("#addSalePromotion .step_choice").show();
        }
       /* var skuId=$("#skuInfo").find(".on input[name=skuId]").val();
        if(null==skuId){
            layer.msg("请先选择商品",{shade: [0.5, '#fff'],time:2000});
            return false;
        }
        var skuName=$("#skuInfo").find(".on input[name=skuName]").val();
        $("#skuEventDiv").find("tbody").prepend('<tr id="'+skuId+'">'+
            '<input type="hidden" value="'+skuId+'" id="skuId${vs.index}" name="skuIds"/>'+
            '<td class="wh40">'+skuName+'</td>'+
            '<td class="wh20"><select class="form-control input-sm" name="discountTypes"><option value="1">百分比</option><option value="2" >绝对值</option></select></td>'+
            '<td class="wh20"><input class="form-control input-sm w100 auto" type="text" placeholder="请输入售价" value="100"  name="discountValues" required min="0" digits="true"></td>'+
            '<td class="wh20"><button type="button" class="btn btn-danger f12 btn-sm btn_sku_delete">删除</button></td>'+
            '</tr>');
        $(this).parents(".step_relation").hide().siblings("#addSalePromotion .step_choice").show();*/
    });
    //选择商品 关闭
    $("#addSalePromotion").on("click",".step_relation .closebtn",function(){
        $(this).parents(".step_relation").hide().siblings("#addSalePromotion .step_choice").show();
    });
    //删除商品
    $("#addSalePromotion").on("click",".btn_sku_delete",function(){
        $(this).parents("td").parents("tr").remove();
    });
    //选择商品 第三步选择商品
    $("#addSalePromotion").on("click",".choice_relation li",function(){
        //$(this).addClass("on").siblings().removeClass("on");
        //多选
        $(this).toggleClass('on');
    });
    //重置窗口
    $("#addSalePromotion").on("click",".restbtn",function(){
        $("#addSalePromotion").find(".step_info").show().siblings("#addSalePromotion .step_choice, #addSalePromotion .step_relation").hide();
    });


});