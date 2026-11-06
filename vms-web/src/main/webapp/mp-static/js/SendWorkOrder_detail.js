$(function(){
    /*//处理TAB导航
    $("#SendDetail .tab2").on("click","li",function(){
        $(this).addClass("on").siblings().removeClass("on");
        $("#SendDetail .detailWrap").hide().eq($("#SendDetail .tab2 li").index(this)).show();
    });*/
    //调整余量
    $(document).on("click", ".adjust_btn", function() {
        var newNum = $(this).parents(".dialogWidget").find(".dialogNum");
        var currNums = $(this).parents(".dialogWidget").find('input[name="currNums"]');
        var regNum = /^[0-9]*[0-9][0-9]*$/;
        //0803 maxCapacity
        var maxCapacityVal = $(this).parents('.dialogWidget').find('input[name="maxCapacity"]').val();
        $.prompt({
            //text: "",
            title: "请输入实际货道余量",
            onOK: function(text) {
                //$.alert("货道的实际余量是" + text)
                var getNum = $("#weui-prompt-input").val();
                if(parseInt(getNum) > parseInt(maxCapacityVal)) {
                    $.toast("实际余量超出限制", "text");
                    return false;
                }
                if(!regNum.test(getNum)) {
                    $.toast("格式有误,请重新输入", "text");
                    return false;
                }
                if(getNum == "") {
                    $.toast("余量不能为空", "text");
                    return false;
                }else{
                    text = parseInt(getNum);
                    newNum.html(text);
                    currNums.val(text);
                }

            },
            onCancel: function() {
                //console.log("取消了");
            },
            input: ''
        });
        //移除原始输入框
        //$("#weui-prompt-input").remove();
        //$(".weui_dialog_bd").append('<input class="weui-prompt-input text_center" id="weui-prompt-input" type="tel" value="">');

        /*
         var regNum = /^[0-9]*[1-9][0-9]*$/;
         var getNum = $("#weui-prompt-input").val();
         var getFullNumVal = $(this).siblings("li").find(".dialogFullNum").text();  // 满量的数值
         if(!regNum.test(getNum)) {
         $.toast("请输入正确的数字格式", "text");
         return false;
         }
         if(getNum >= getFullNumVal) {
         $.toast("实际余量超出限制", "text");
         return false;
         }
         if(getNum == "") {
         $.toast("余量不能为空", "text");
         return false;
         }
         */
    });

    /*
     //调整余量 new
     $(".dialogWidget").on("click",".adjust_btn",function(){
     var newNum = $(this).parents(".dialogWidget").find(".dialogNum");
     $.prompt({
     //text: "",
     title: "调整余量",
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
     var getNumVal = $(this).siblings("li").find(".dialogNum").text();  // 余量的数值
     var getFullNumVal = $(this).siblings("li").find(".dialogFullNum").text();  // 满量的数值
     numVal.val(getNumVal);  //获取余的数值 - 给dialog里的input
     $(document).on("click","#TapAdd",function(){
     if(parseInt(numVal.val()) >= getFullNumVal) {
     $("#TapAdd").attr("disabled","disabled").css("background","#ccc");
     }else{
     numVal.val(parseInt(numVal.val())+1);
     $("#TapMinus").removeAttr("disabled").css("background","#1fc38b");
     }
     })
     $(document).on("click","#TapMinus",function(){
     if(parseInt(numVal.val())>1) {
     numVal.val(parseInt(numVal.val())-1);
     $("#TapAdd").removeAttr("disabled").css("background","#1fc38b");
     }else{
     $("#TapMinus").attr("disabled","disabled").css("background","#ccc");
     }
     })
     });
     */


})