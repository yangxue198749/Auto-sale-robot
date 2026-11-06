$(function () {
    initWorkOrderData();
    tab();//tab切换以及滑动固定
    adjustBtn();//调整按钮
    productionBtn();//生产日期和生产编号
    CancellationAndCompletion();//取消和完成

    // 忽略操作
   /* $(document).on('click','#ignoreBtn',function(){
        var taskId = $("#lockId").val();
        console.log('taskId:'+taskId);
        $.showLoading();
        $.ajax({
            type: 'post',
            //data:{"taskId":taskId},
            url:'/mp/mpTask/delivery/ignore?taskId='+taskId,
            async : false,
            dataType : 'json',
            cache : false,
            success : function(data) {
                $.hideLoading();
                if(data.type=='error'){
                    $.toast("忽略失败", "text");
                }
                else{
                    window.history.back();
                }
            },
            error: function(){
                $.toast("网络连接超时，请稍后重试", "text");
            }
        });
    });*/
});
//tab切换以及滑动固定
var tab=function () {
    var elm = $('.header');
    var startPos = $(elm).offset().top;
    $.event.add(window, "scroll", function() {
        var p = $(window).scrollTop();
        $(elm).css('position',((p) > startPos) ? 'fixed' : 'static');
        $(elm).css('top',((p) > startPos) ? '0px' : '');
        $(elm).css('width',((p) > startPos) ? '94.66%' : '');
        $(elm).css('margin',((p) > startPos) ? '0 auto' : '');
        $(".section").css('marginTop',((p) > startPos) ? '40px' : '');
    });
    var ind=0,len=$(".header ul li").length,w=$(".header ul li").width();
    $(".header ul li").on("click",function(){
        var ind = $(this).index();
        $(this).addClass("bg").siblings().removeClass("bg");
        $(".section>div").eq(ind).attr("class","show");
        $(".section>div").eq(ind).siblings().attr("class","");
        $(".bottom_line").css("margin-left",w*ind)
        $(".triangle").css("margin-left",w*ind)
    });
};

var initWorkOrderData = function(){
    taskid = $("#lockId").val();
    fillObj = {};
    fillData = [];
    cleanData = [];
    cleanObj = {};
    adjustData = [];
    adjustObj = {};
    $.ajax({
        url: "/mp/mpTask/delivery/detail/taskInfo",
        data:{"taskId":taskid},
        async: false,
        type: "POST",
        success: function (data) {
            $('#SendDetail .weui-loadmore').empty();
            console.log('初始化json:'+JSON.stringify(data));
            var taskStatus = data.taskStatus;
            var fillList = data.fillList;
            var cleanList = data.cleanList;
            var adjustList = data.adjustList;
            loadData(fillList,cleanList,adjustList);
            if(taskStatus == 1){
                //忽略
                //$('#footBtnGroup').html('<section class="fullBtn"><input type="button" value="忽略" class="btn cancel" id="ignoreBtn"></section>');
                $('#footBtnGroup .firstBtn').addClass('fullBtn').siblings('.lastBtn').remove();
                $('.adjust,.production').remove();
                $('.contentBox').css('height','150px');
            }else if(taskStatus == 4 || taskStatus == 7){
                //已完成
                $('.adjust, .production, #footBtnGroup').remove();
                $('.contentBox').css('height','150px');
            }
            /*//补货列表
            $.each(data.fillList,function (index,item) {
                var html = "";
                var productionHtml="";
                    html+='<div class="contentBox">' +
                        '                    <div class="imgBox">' +
                        '                        <img src="'+item.skuImg+'" alt="">' +
                        '                    </div>' +
                        '                    <div class="content">' +
                        '                        <div class="designation">' +
                        '                            <p><span>'+item.channelCode+'</span>'+item.skuName+'</p>' +
                        '                        </div>' +
                        '                        <div class="listing">' +
                        '                            <dl>' +
                        '                                <dt>满量</dt>' +
                        '                                <dd class="fullCount" data-fullCount="'+item.fullCount+'">'+item.fullCount+'</dd>' +
                        '                            </dl>' +
                        '                            <dl>' +
                        '                                <dt>清出</dt>' +
                        '                                <dd class="adjustNum fillClearNumber" data-dataList="'+item.clearCount+'">'+item.clearCount+'</dd>' +
                        '                                <dd>' +
                        '                                    <input type="button" value="调整" class="adjust clear_adjust fillClearAdjust">' +
                        '                                </dd>' +
                        '                            </dl>' +
                        '                            <dl>' +
                        '                                <dt>应补</dt>' +
                        '                                <dd>'+item.fillCount+'</dd>' +
                        '                            </dl>' +
                        '                            <dl>' +
                        '                                <dt>补后余</dt>' +
                        '                                <dd  class="adjustNum adjustStyle fillMarginNumber" data-dataList="'+item.marginCount+'">'+item.marginCount+'</dd>' +
                        '                                <dd>' +
                        '                                    <input type="button" value="调整" class="adjust AfterRepair_adjust fillSurpAdjust">' +
                        '                                </dd>' +
                        '                            </dl>' +
                        '                        </div>' +
                        '                        <div class="clearBox"></div>' +
                        '                    </div>'+
                        '                </div>';
                $('.fillList').append(html);
                if(item.termInfo){
                    productionHtml +='<div class="production fillProduction">' +
                        '<div class="productionNumber" id="productionNumber">' +
                        '     <p>生产日期：<span class="producedDate" data-manufactureDate="'+item.manufactureDate+'"></span></p>' +
                        '     <p>生产批号：<span class="batchNumber" data-batchNum="'+item.batchNum+'"></span></p>' +
                        '          </div>' +
                        '             <div class="productionBtn">' +
                        '                  <img src="/mp-static/images/arrows.png" alt="">' +
                        '       </div>' +
                        '  </div>';

                    $(".clearBox").each(function (i) {
                        if(i == index){
                            $(".contentBox").eq(index).css("height","200px");
                            $(this).before(productionHtml);
                        }
                    });
                }
                fillObj = {
                    "channelCode":item.channelCode,
                    "channelId":item.channelId,
                    "currCount":item.currCount,
                    "fillCount":item.fillCount,
                    "fullCount":item.fullCount,
                    "skuId":item.skuId,
                    "termInfo":item.termInfo
                };
                fillData.push(fillObj);
            });
            //清货列表
            $.each(data.cleanList,function (index,item) {
                var html2="";
                var productionHtml2="";
                html2+='<div class="contentBox contentBox2">' +
                    '                    <div class="imgBox">' +
                    '                        <img src="'+item.skuImg+'" alt="">' +
                    '                    </div>' +
                    '                    <div class="content">' +
                    '                        <div class="designation">' +
                    '                            <span>'+item.channelCode+'</span>' +
                    '                            <p>'+item.skuName+'</p>' +
                    '                        </div>' +
                    '                        <div class="listing">' +
                    '                            <dl class="clean_dl">' +
                    '                                <dt>清出</dt>' +
                    '                                <dd  class="adjustNum adjustNumber cleanClearNumber fullCount" data-dataList="'+item.clearCount+'" data-fullCount="'+item.fullCount+'">'+item.clearCount+'</dd>' +
                    '                                <dd>' +
                    '                                    <input type="button" value="调整" class="adjust AfterRepair_adjust cleanClearAdjust">' +
                    '                                </dd>' +
                    '                            </dl>' +
                    '                            <dl class="clean_dl">' +
                    '                                <dt>补后余</dt>' +
                    '                                <dd>'+item.marginCount+'</dd>' +
                    '                            </dl>' +
                    '                        </div>' +
                    '                        <div class="clearBox"></div>' +
                    '                    </div>' +
                    '                </div>';
                $('.cleanList').append(html2);
                if(item.termInfo){
                    productionHtml2 +='<div class="production cleanProduction">' +
                        '<div class="productionNumber cleanProduction" id="productionNumber">' +
                        '     <p>生产日期：<span class="producedDate" data-manufactureDate="'+item.manufactureDate+'"></span></p>' +
                        '     <p>生产批号：<span class="batchNumber" data-batchNum="'+item.batchNum+'"></span></p>' +
                        '          </div>' +
                        '             <div class="productionBtn">' +
                        '                  <img src="/mp-static/images/arrows.png" alt="">' +
                        '       </div>' +
                        '  </div>';

                    $(".clearBox").each(function (i) {
                        if(i == index){
                            $(".contentBox").eq(index).css("height","200px");
                            $(this).before(productionHtml2);
                        }
                    });
                }
                cleanObj = {
                    "channelCode":item.channelCode,
                    "channelId":item.channelId,
                    "currCount":item.currCount,
                    "fillCount":item.fillCount,
                    "fullCount":item.fullCount,
                    "marginCount":item.marginCount,
                    "skuId":item.skuId,
                    "termInfo":item.termInfo
                };
                cleanData.push(cleanObj);
            });
            //调量列表
            $.each(data.adjustList,function (index,item) {
                var html3='';
                var productionHtml3='';
                html3+='<div class="contentBox contentBox2">' +
                    '                    <div class="imgBox">' +
                    '                        <img src="'+item.skuImg+'" alt="">' +
                    '                    </div>' +
                    '                    <div class="content">' +
                    '                        <div class="designation">' +
                    '                            <span>'+item.channelCode+'</span>' +
                    '                            <p>'+item.skuName+'</p>' +
                    '                        </div>' +
                    '                        <div class="listing">' +
                    '                            <dl>' +
                    '                                <dt>满量</dt>' +
                    '                                <dd  class="fullCount" data-fullCount="'+item.fullCount+'">'+item.fullCount+'</dd>' +
                    '                            </dl>' +
                    '                            <dl>' +
                    '                                <dt>清出</dt>' +
                    '                                <dd class="adjustNum adjustNumber overshootClearNumber" data-dataList="'+item.clearCount+'">'+item.clearCount+'</dd>' +
                    '                                <dd>' +
                    '                                    <input type="button" value="调整" class="adjust clear_adjust overshootClearAdjust">' +
                    '                                </dd>' +
                    '                            </dl>' +
                    '                            <dl>' +
                    '                                <dt>应补</dt>' +
                    '                                <dd>'+item.fillCount+'</dd>' +
                    '                            </dl>' +
                    '                            <dl>' +
                    '                                <dt>补后余</dt>' +
                    '                                <dd  class="adjustNum adjustStyle overshootMarginNumber" data-dataList="'+item.marginCount+'">'+item.marginCount+'</dd>' +
                    '                                <dd>' +
                    '                                    <input type="button" value="调整" class="adjust AfterRepair_adjust overshootSurpAdjust">' +
                    '                                </dd>' +
                    '                            </dl>' +
                    '                        </div>' +
                    ' <div class="production overshootProduction">' +
                    '                        <div class="clearBox"></div>' +
                    '                    </div>'+
                '                    </div>'+
                '                    </div>';
                $(".adjustList").append(html3);
                if(item.termInfo){
                    productionHtml3 +='<div class="production overshootProduction">' +
                        '<div class="productionNumber" id="productionNumber">' +
                        '     <p>生产日期：<span class="producedDate" data-manufactureDate="'+item.manufactureDate+'"></span></p>' +
                        '     <p>生产批号：<span class="batchNumber" data-batchNum="'+item.batchNum+'"></span></p>' +
                        '          </div>' +
                        '             <div class="productionBtn">' +
                        '                  <img src="/mp-static/images/arrows.png" alt="">' +
                        '       </div>' +
                        '  </div>';

                    $(".clearBox").each(function (i) {
                        if(i == index){
                            $(".contentBox").eq(index).css("height","200px");
                            $(this).before(productionHtml3);
                        }
                    });
                }
                adjustObj = {
                    "channelCode":item.channelCode,
                    "channelId":item.channelId,
                    "currCount":item.currCount,
                    "fillCount":item.fillCount,
                    "fullCount":item.fullCount,
                    "skuId":item.skuId,
                    "termInfo":item.termInfo
                };

                adjustData.push(adjustObj);
            });
           objData = {
                "fillList":fillData,
                "cleanList":cleanData,
                "adjustList":adjustData,
                "taskId": taskid
            };*/
        },
        error: function(){
            $.toast("网络连接超时，请稍后重试", "text");
        }
    });
};

function loadData(fillList,cleanList,adjustList){
    //补货列表
    $.each(fillList,function (index,item) {
        var html = "";
        var productionHtml="";
        html+='<div class="contentBox">' +
            '<div class="imgBox">' +
            '<img src="'+item.skuImg+'" alt="">' +
            '</div>' +
            '<div class="content">' +
            '<div class="designation">' +
            '<p><span>'+item.channelCode+'</span>'+item.skuName+'</p>' +
            '</div>' +
            '<div class="listing">' +
            '<dl>' +
            '<dt>满量</dt>' +
            '<dd class="fullCount" data-fullCount="'+item.fullCount+'">'+item.fullCount+'</dd>' +
            '</dl>' +
            '<dl>' +
            '<dt>清出</dt>' +
            '<dd class="adjustNum fillClearNumber" data-dataList="'+item.clearCount+'">'+item.clearCount+'</dd>' +
            '<dd>' +
            '<input type="button" value="调整" class="adjust clear_adjust fillClearAdjust">' +
            '</dd>' +
            '</dl>' +
            '<dl>' +
            '<dt>应补</dt>' +
            '<dd>'+item.fillCount+'</dd>' +
            '</dl>' +
            '<dl>' +
            '<dt>补后余</dt>' +
            '<dd class="adjustNum adjustStyle fillMarginNumber" data-dataList="'+item.marginCount+'">'+item.marginCount+'</dd>' +
            '<dd>' +
            '<input type="button" value="调整" class="adjust AfterRepair_adjust fillSurpAdjust">' +
            '</dd>' +
            '</dl>' +
            '</div>' +
            '<div class="clearBox"></div>' +
            '</div>'+
            '</div>';
        $('.fillList').append(html);
        if(item.termInfo){
            productionHtml +='<div class="production fillProduction">' +
                '<div class="productionNumber" id="productionNumber">' +
                '<p>生产日期：<span class="producedDate" data-manufactureDate="'+item.manufactureDate+'"></span></p>' +
                '<p>生产批号：<span class="batchNumber" data-batchNum="'+item.batchNum+'"></span></p>' +
                '</div>' +
                '<div class="productionBtn">' +
                '<img src="/mp-static/images/arrows.png" alt="">' +
                '</div>' +
                '</div>';
            $(".clearBox").each(function (i) {
                if(i == index){
                    $(".contentBox").eq(index).css("height","200px");
                    $(this).before(productionHtml);
                }
            });
        }
        fillObj = {
            "channelCode":item.channelCode,
            "channelId":item.channelId,
            "currCount":item.currCount,
            "fillCount":item.fillCount,
            "fullCount":item.fullCount,
            "skuId":item.skuId,
            "termInfo":item.termInfo
        };
        fillData.push(fillObj);
    });
    //清货列表
    $.each(cleanList,function (index,item) {
        var html2="";
        var productionHtml2="";
        html2+='<div class="contentBox contentBox2">' +
            '<div class="imgBox">' +
            '<img src="'+item.skuImg+'" alt="">' +
            '</div>' +
            '<div class="content">' +
            '<div class="designation">' +
            '<span>'+item.channelCode+'</span>' +
            '<p>'+item.skuName+'</p>' +
            '</div>' +
            '<div class="listing">' +
            '<dl class="clean_dl">' +
            '<dt>清出</dt>' +
            '<dd  class="adjustNum adjustNumber cleanClearNumber fullCount" data-dataList="'+item.clearCount+'" data-fullCount="'+item.fullCount+'">'+item.clearCount+'</dd>' +
            '<dd>' +
            '<input type="button" value="调整" class="adjust AfterRepair_adjust cleanClearAdjust">' +
            '</dd>' +
            '</dl>' +
            '<dl class="clean_dl">' +
            '<dt>清后余</dt>' +
            //'<dd>'+item.marginCount+'</dd>' + //bak
            '<dd>0</dd>' +   //20171228 14:39  此处固定为0，我也不知道为啥为0，后端说必须为0.
            '</dl>' +
            '</div>' +
            '<div class="clearBox"></div>' +
            '</div>' +
            '</div>';
        $('.cleanList').append(html2);
        if(item.termInfo){
            productionHtml2 +='<div class="production cleanProduction">' +
                '<div class="productionNumber cleanProduction" id="productionNumber">' +
                '     <p>生产日期：<span class="producedDate" data-manufactureDate="'+item.manufactureDate+'"></span></p>' +
                '     <p>生产批号：<span class="batchNumber" data-batchNum="'+item.batchNum+'"></span></p>' +
                '          </div>' +
                '             <div class="productionBtn">' +
                '                  <img src="/mp-static/images/arrows.png" alt="">' +
                '       </div>' +
                '  </div>';

            $(".clearBox").each(function (i) {
                if(i == index){
                    $(".contentBox").eq(index).css("height","200px");
                    $(this).before(productionHtml2);
                }
            });
        }
        cleanObj = {
            "channelCode":item.channelCode,
            "channelId":item.channelId,
            "currCount":item.currCount,
            "fillCount":item.fillCount,
            "fullCount":item.fullCount,
            "marginCount":item.marginCount,
            "skuId":item.skuId,
            "termInfo":item.termInfo
        };
        cleanData.push(cleanObj);
    });
    //调量列表
    $.each(adjustList,function (index,item) {
        var html3='';
        var productionHtml3='';
        html3+='<div class="contentBox contentBox2">' +
            '<div class="imgBox">' +
            '<img src="'+item.skuImg+'" alt="">' +
            '</div>' +
            '<div class="content">' +
            '<div class="designation">' +
            '<span>'+item.channelCode+'</span>' +
            '<p>'+item.skuName+'</p>' +
            '</div>' +
            '<div class="listing">' +
            '<dl>' +
            '<dt>满量</dt>' +
            '<dd  class="fullCount" data-fullCount="'+item.fullCount+'">'+item.fullCount+'</dd>' +
            '</dl>' +
            '<dl>' +
            '<dt>清出</dt>' +
            '<dd class="adjustNum adjustNumber overshootClearNumber" data-dataList="'+item.clearCount+'">'+item.clearCount+'</dd>' +
            '<dd>' +
            '<input type="button" value="调整" class="adjust clear_adjust overshootClearAdjust">' +
            '</dd>' +
            '</dl>' +
            '<dl>' +
            '<dt>应补</dt>' +
            '<dd>'+item.fillCount+'</dd>' +
            '</dl>' +
            '<dl>' +
            '<dt>补后余</dt>' +
            '<dd class="adjustNum adjustStyle overshootMarginNumber" data-dataList="'+item.marginCount+'">'+item.marginCount+'</dd>' +
            '<dd>' +
            '<input type="button" value="调整" class="adjust AfterRepair_adjust overshootSurpAdjust">' +
            '</dd>' +
            '</dl>' +
            '</div>' +
            '<div class="production overshootProduction">' +
            '<div class="clearBox"></div>' +
            '</div>'+
            '</div>'+
            '</div>';
        $(".adjustList").append(html3);
        if(item.termInfo){
            productionHtml3 +='<div class="production overshootProduction">' +
                '<div class="productionNumber" id="productionNumber">' +
                '<p>生产日期：<span class="producedDate" data-manufactureDate="'+item.manufactureDate+'"></span></p>' +
                '<p>生产批号：<span class="batchNumber" data-batchNum="'+item.batchNum+'"></span></p>' +
                '</div>' +
                '<div class="productionBtn">' +
                '<img src="/mp-static/images/arrows.png" alt="">' +
                '</div>' +
                '</div>';
            $(".clearBox").each(function (i) {
                if(i == index){
                    $(".contentBox").eq(index).css("height","200px");
                    $(this).before(productionHtml3);
                }
            });
        }
        adjustObj = {
            "channelCode":item.channelCode,
            "channelId":item.channelId,
            "currCount":item.currCount,
            "fillCount":item.fillCount,
            "fullCount":item.fullCount,
            "skuId":item.skuId,
            "termInfo":item.termInfo
        };

        adjustData.push(adjustObj);
    });
    objData = {
        "fillList":fillData,
        "cleanList":cleanData,
        "adjustList":adjustData,
        "taskId": taskid
    };
}
//调整按钮
var adjustBtn = function(){
    //补货清出调整
    $(".fillClearAdjust").on("click",function () {
        publicClearBtn($(this));
    });
    //补货补后余调整
    $(".fillSurpAdjust").on("click",function () {
        publicSurpBtn($(this));
    });
    //清货清出调整
    $(".cleanClearAdjust").on("click",function () {
        publicClearBtn($(this));
    });
    //调量清出调整
    $(".overshootClearAdjust").on("click",function () {
        publicClearBtn($(this));
    });
    //调量补后余调整
    $(".overshootSurpAdjust").on("click",function () {
        publicSurpBtn($(this));
    });
};
//公共清出调整按钮
var publicClearBtn = function (ele) {
    var adjustNum = ele.parents("dl").find(".adjustNum").attr("data-datalist");
    var adjust = ele.parents("dl").find(".adjustNum");
    $.dialog=function(options){
        var mh_mask=$("<div class='mh_mask'></div>").appendTo($("body"));
        var prompt_content=$("<div class='prompt_content'><div class='prompt_bd'><p class='title'>请输入实际清出货物数量</p><input type='tel' class='adjustInp' value='"+adjustNum+"'/></div></div>").appendTo(mh_mask);
        var prompt_button=$("<div class='prompt_button'></div>").appendTo(prompt_content);
        $.each(options.arr,function(i,val){
            $("<span class='prompt_btn'>"+val+"</span>").appendTo(prompt_button);
        });
        $(".prompt_btn").eq(0).on(options.type,function(){options.No()});
        $(".prompt_btn").eq(1).on(options.type,function(){options.Yes()});
    };
    var maxNum=ele.parents(".listing").children().eq(0).find(".fullCount").attr("data-fullCount");
    $.dialog({
        arr:["取消","确认"],
        type:"click",
        No:function(ele){
            $(".mh_mask").remove();
        },
        Yes:function(){
            var val=$.trim($(".adjustInp").val());
            var parVal=parseInt($.trim($(".adjustInp").val()));
            var reg=/^[0-9]*$/;
            if(val != ''){
                if(reg.test(val)){
                    if(parVal < 0){
                        $('.adjustInp').val('');
                        $.toast('超出限制，请重新输入','text');
                    }else if(parVal > maxNum){
                        $('.adjustInp').val('');
                        $.toast('超出限制，请重新输入','text');
                    }
                    else{
                        adjust.text(val).attr("data-dataList",val);
                        console.log("data-dataList"+val);
                        $(".mh_mask").remove();
                    }
                }else{
                    $('.adjustInp').val('');
                    $.toast('请输入正确的数字','text');
                }
            }else{
                $.toast('不能为空','text');
                return false;
            }
        }
    });
};
//公共补后余调整按钮
var publicSurpBtn = function (ele) {
    var adjustNum = ele.parents("dl").find(".adjustNum").attr("data-datalist");
    var adjust = ele.parents("dl").find(".adjustNum");
    $.dialog = function(options){
        var mh_mask=$("<div class='mh_mask'></div>").appendTo($("body"));
        var prompt_content=$("<div class='prompt_content'><div class='prompt_bd'><p class='title'>请输入实际货道余量</p><input type='tel' class='adjustInp' value='"+adjustNum+"'/></div></div>").appendTo(mh_mask);
        var prompt_button=$("<div class='prompt_button'></div>").appendTo(prompt_content);
        $.each(options.arr,function(i,val){
            $("<span class='prompt_btn'>"+val+"</span>").appendTo(prompt_button);
        });
        $(".prompt_btn").eq(0).on(options.type,function(){options.No()});
        $(".prompt_btn").eq(1).on(options.type,function(){options.Yes()});
    };
    var maxNum=ele.parents(".listing").children().eq(0).find(".fullCount").attr("data-fullCount");
    $.dialog({
        arr:["取消","确认"],
        type:"click",
        No:function(ele){
            $(".mh_mask").remove();
        },
        Yes:function(){
            var val=$.trim($(".adjustInp").val());
            var parVal=parseInt($.trim($(".adjustInp").val()));
            var reg=/^[0-9]*$/;
            if(val != ''){
                if(reg.test(val)){
                        if(parVal < 0){
                            $('.adjustInp').val('');
                            $.toast('超出限制，请重新输入','text');
                        }else if(parVal > maxNum){
                            $('.adjustInp').val('');
                            $.toast('超出限制，请重新输入','text');
                        }
                        else{
                            adjust.text(val).attr("data-dataList",val);
                            console.log("data-dataList"+val);
                            $(".mh_mask").remove();
                        }
                }else{
                    $('.adjustInp').val('');
                    $.toast('请输入正确的数字','text');
                }

            }else{
                $.toast('不能为空','text');
                return false;
            }
        }
    });
};
//生产日期和生产编号
var productionBtn = function () {
    //封装生产日期和生产批号的弹出框
    $.prompt = function(options){
        var mh_mask = $("<div class='mh_mask'></div>").appendTo($("body"));
        var prompt_content = $("<div class='prompt_content'><div class='prompt_bd'><span>"+options.inp1+"</span><input type='text' class='manufactureDate' placeholder='请输入8位纯数字的生产日期' maxlength='8'' /><span>"+options.inp2+"</span><input type='text' class='batchNum' placeholder='请输入2-50位数字或字母' maxlength='50' /></div></div>").appendTo(mh_mask);
        var prompt_button = $("<div class='prompt_button'></div>").appendTo(prompt_content);
        $.each(options.arr,function(i,val){
            $("<span class='prompt_btn'>"+val+"</span>").appendTo(prompt_button);
        });
        $(".prompt_btn").eq(0).on(options.type,function(){options.No()});
        $(".prompt_btn").eq(1).on(options.type,function(){options.Yes()});
    };
    //补货生产日期和生产批号按钮
    $(".fillProduction").on("click",function () {
        publicProductionBtn($(this));
    });
    //清货生产日期和生产批号按钮
    $(".cleanProduction").on("click",function () {
        publicProductionBtn($(this));
    });
    //调量生产日期和生产批号按钮
    $(".overshootProduction").on("click",function () {
        publicProductionBtn($(this));
    });
}
//公共生产日期和生产批号按钮
var publicProductionBtn = function (ele) {
    var reg = /^(?:(?:(?:(?=\d{2}(?:(?:[02468][048])|(?:[13579][26])))\d{4}02(?!00)[0-2]\d))|(?:\d{4}(?:(?:(?:(?:0[469])|(?:11))(?!00)(?:(?:[0-2]\d)|(?:30)))|(?:(?:(?:0[13578])|(?:1[02]))(?!00)(?:(?:[0-2]\d)|(?:3[01])))|(?:02(?!(?:00)|(?:29))[0-2]\d))))$/;
    var reg2 = /^[A-Za-z0-9]{2,50}$/;
    producedDate = ele.find(".producedDate");
    batchNumber = ele.find(".batchNumber");
    $.prompt({
        inp1:"生产日期：",
        inp2:"生产批号：",
        arr:["取消","确认"],
        type:"click",
        No:function(ele){
            $(".mh_mask").remove();
        },
        Yes:function(){
            if($(".manufactureDate").val() !== ""){
                if(reg.test($(".manufactureDate").val())){
                    if(reg2.test($(".batchNum").val() || $(".batchNum").val() == "")){
                        var val1 = $.trim($(".manufactureDate").val());
                        var val2 = $.trim($(".batchNum").val());
                        batchNumber.attr("data-batchnum",val2);
                        producedDate.attr("data-manufacturedate",val1);
                        producedDate.text(val1);
                        batchNumber.text(val2);
                        $(".mh_mask").remove();
                    }else{
                        $.toast('请输入正确的生产批号','text');
                    }
                }else{
                    $.toast('请输入正确的生产日期','text');
                }
            }else{
                $.toast('请输入生产日期','text');
            }
        }
    });
};
//取消和完成
var CancellationAndCompletion = function () {
    /*$("#cancel").on("click",function () {
        $.confirm("是否取消补货？",function() {
            //点击确认后的回调函数
            window.history.go(-1);
        }, function() {
            //点击取消后的回调函数
        });
    });*/
    // 忽略操作
    $('#ignoreBtn').click(function(){
        var taskId = $("#lockId").val();
        console.log('taskId:'+taskId);
        $.confirm("是否取消补货？",function() {
            $.ajax({
                type: 'post',
                //data:{"taskId":taskId},
                url:'/mp/mpTask/delivery/ignore?taskId='+taskId,
                async : false,
                dataType : 'json',
                cache : false,
                success : function(data) {
                    if(data.type=='error'){
                        $.toast("忽略失败", "text");
                    }else{
                        window.history.back();
                    }
                },
                error: function(){
                    $.toast("网络连接超时，请稍后重试", "text");
                }
            });
        }, function() {
            //点击取消后的回调函数
        });

    });
    $("#complete").click(function () {
        $.confirm("确认补货完成？",function() {
            //点击确认后的回调函数
            var arr = [];
            $('.fillProduction').each(function(ind,item){
               var _thisData = $(this).siblings('.listing').find('.fillMarginNumber').attr('data-datalist');
               var _thisDate = $(this).find('.producedDate').attr('data-manufacturedate');
                if(_thisDate == undefined || _thisDate == 'undefined' || _thisDate == ''){
                    if(_thisData != 0){
                        arr.push(ind);
                        $.toast("请输入生产日期", "text");
                    }
                    //传参
                    $(this).find('.producedDate').attr('data-manufacturedate','');
                    $(this).siblings('.listing').find('.fillMarginNumber').attr('data-datalist','');
                }
            });
            if(arr.length == 0){
                toService();
            }
        }, function() {
            //点击取消后的回调函数
        });
    });
    function toService() {
        $(".fillClearNumber").each(function (i, e) {
            var val = $(this).attr("data-dataList");
            console.log('fillClearNumber:'+val);
            objData.fillList[i].clearCount = parseInt(val);
        });
        $(".fillMarginNumber").each(function (i, e) {
            //var val = $(this).attr("data-dataList");
            var val = $(this).text();
            console.log('fillMarginNumber:'+val);
            objData.fillList[i].marginCount = parseInt(val);
        });

        $(".cleanClearNumber").each(function (i, e) {
            var val = $(this).attr("data-dataList");
            console.log('cleanClearNumber:'+val);
            objData.cleanList[i].clearCount = parseInt(val);
        });

        $(".overshootClearNumber").each(function (i, e) {
            var val = $(this).attr("data-dataList");
            console.log('overshootClearNumber:'+val);
            objData.adjustList[i].clearCount = parseInt(val);
        });
        $(".overshootMarginNumber").each(function (i, e) {
            var val = $(this).attr("data-dataList");
            console.log('overshootMarginNumber:'+val);
            objData.adjustList [i].marginCount = parseInt(val);
        });
        $(".clearBox").each(function (i,e) {
            $(".fillClearNumber").each(function (ind,item) {
                if(i==ind){
                    var val1 = $(this).parents(".listing").next(".fillProduction").find(".producedDate").attr("data-manufacturedate");
                    if(val1 == undefined || val1 == 'undefined' || val1 == NaN || val1 == 'NaN'){
                        val1 = '';
                    }
                    objData.fillList [i].manufactureDate = val1;
                    var val2 = $(this).parents(".listing").next(".fillProduction").find(".batchNumber").attr("data-batchnum");
                    if(val2 == undefined || val2 == 'undefined' || val2 == NaN || val2 == 'NaN'){
                        val2 = '';
                    }
                    objData.fillList [i].batchNum = val2;
                }
            })
        });
        $(".clearBox").each(function (i,e) {
            $(".cleanClearNumber").each(function (ind,item) {
                if(i==ind){
                    var val1 = $(this).parents(".listing").next(".fillProduction").find(".producedDate").attr("data-manufacturedate");
                    if(val1 == undefined || val1 == 'undefined' || val1 == NaN || val1 == 'NaN'){
                        val1 = '';
                    }
                    objData.cleanList [i].manufactureDate = val1;
                    var val2 = $(this).parents(".listing").next(".fillProduction").find(".batchNumber").attr("data-batchnum");
                    if(val2 == undefined || val2 == 'undefined' || val2 == NaN || val2 == 'NaN'){
                        val2 = '';
                    }
                    objData.cleanList [i].batchNum = val2;
                }
            })
        });
        $(".clearBox").each(function (i,e) {
            $(".overshootClearNumber").each(function (ind,item) {
                if(i==ind){
                    var val1 = $(this).parents(".listing").next(".fillProduction").find(".producedDate").attr("data-manufacturedate");
                    if(val1 == undefined || val1 == 'undefined' || val1 == NaN || val1 == 'NaN'){
                        val1 = '';
                    }
                    objData.adjustList [i].manufactureDate = val1;
                    var val2 = $(this).parents(".listing").next(".fillProduction").find(".batchNumber").attr("data-batchnum");
                    if(val2 == undefined || val2 == 'undefined' || val2 == NaN || val2 == 'NaN'){
                        val2 = '';
                    }
                    objData.adjustList [i].batchNum = val2;
                }
            })
        });
        console.log('返回服务器objData:'+JSON.stringify(objData));
        $.ajax({
            url: "/mp/mpTask/delivery/finish",
            data: {"content": JSON.stringify(objData)},
            async: false,
            type: "POST",
            success: function (data) {
                if (data.type == "success") {
                    window.location.href = data.url;
                } else {
                    $.toast("完成任务失败，请稍后重试", "text");
                }
            },
            error: function () {
                $.toast("网络连接超时，请稍后重试", "text");
            }
        })
    }
}




