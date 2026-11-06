$(function(){
    initTaskDetail();
    taskDialog();
    //toService();
    $("#taskDetailGroup .tabHead li").click(function(){
        var _this = $("#taskDetailGroup .tabHead li");
        $(this).addClass("active").siblings("li").removeClass("active");
        $("#taskDetailList .detailGroup").hide().eq(_this.index(this)).show();
    });
    //完成
    $('#taskDetailGroup').on('click','#taskBtnYes',function(){
        $.confirm("确认补货完成？",function(){
            // 效验生产日期
            var arr = [];
            $('#taskDetailList .hideBaseInfo').each(function(index,item){
                var marginCount = $(this).attr('data-margin');
                var manufactureDate = $(this).siblings('.otherInfo').find('.manufactureDate').val();
                if(manufactureDate == ''){
                    if(marginCount != 0){
                        arr.push(index);
                        $.toast("请输入生产日期", "text");
                        return false;
                    }
                }
            });
            if(arr.length == 0){
                toService();
            }
        }, function() {
            //点击取消后的回调函数
        });
    });
    //一键补满
    $('#taskDetailGroup').on('click','#taskBtnFull',function(){
        $.confirm("确认一键补满？",function(){
            //将补后余填满 fillList  //taskDetailList 全部
            $('#fillList .itemLi').each(function(){
                var _this = $(this);
                var _thisType = _this.attr('data-type');
                if(_thisType == '0'){ //即售商品
                    var hideBaseInfo = _this.find('.hideBaseInfo');
                    var targetNum = hideBaseInfo.attr('data-target-num');
                    hideBaseInfo.siblings('.methodsArea').find('.marginCount').text(targetNum);
                    hideBaseInfo.attr('data-margin',targetNum);
                }
            });
            // 效验生产日期
            /*
            var arr = [];
            $('#fillList .hideBaseInfo').each(function(index,item){
                var marginCount = $(this).attr('data-margin');
                var manufactureDate = $(this).siblings('.otherInfo').find('.manufactureDate').val();
                if(manufactureDate == ''){
                    if(marginCount != 0){
                        arr.push(index);
                        $.toast("请输入生产日期", "text");
                        return false;
                    }
                }
            });
            if(arr.length == 0){
                toService();
            }
            */
        }, function() {
            //点击取消后的回调函数
        });
    });
    //取消-忽略
    $('#taskDetailGroup').on('click','#taskBtnCancel',function(){
        var taskId = $("#taskVmId").val();
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
                    $.toast("网络连接超时", "text");
                }
            });
        }, function() {
            //点击取消后的回调函数
        });
    });
});

var initTaskDetail = function(){
    var taskId = $("#taskVmId").val();
    $.ajax({
        url: "/mp/mpTask/delivery/detail/taskInfo",
        data:{"taskId":taskId},
        async: false,
        type: "POST",
        success: function (data) {
            $('#SendDetail .weui-loadmore').empty();
            //console.log('初始化json:'+JSON.stringify(data));
            var taskStatus = data.taskStatus;
            var fillList = data.fillList;
            var cleanList = data.cleanList;
            var adjustList = data.adjustList;
            if(taskStatus == 1){
                //未开始 :只读，有忽略按钮
                loadTaskData(fillList,cleanList,adjustList,taskStatus);
                $('#taskDetailGroup').append('<div class="footerBtnGroup"><div class="itemLabel full"><div class="taskBtn orange" id="taskBtnCancel">取消</div></div></div>');
            }else if(taskStatus == 2){
                //进行中 :可编辑，有取消和完成按钮
                loadTaskData(fillList,cleanList,adjustList,taskStatus);
                $('#taskDetailGroup').append('<div class="footerBtnGroup v3"><div class="itemLabel"><div class="taskBtn orange" id="taskBtnCancel">取消</div></div><div class="itemLabel"><div class="taskBtn blue" id="taskBtnFull">一键补满</div></div><div class="itemLabel"><div class="taskBtn green" id="taskBtnYes">完成</div></div></div>');
            }else if(taskStatus == 4 || taskStatus == 7){
                //完成 :只读，没有按钮
                loadTaskData(fillList,cleanList,adjustList,taskStatus);
            }
            //测试 打印出服务器给出的data
            //$('#taskDetailList .fillList').append(JSON.stringify(fillList));
            //$('#taskDetailList .cleanList').append(JSON.stringify(cleanList));
            //$('#taskDetailList .adjustList').append(JSON.stringify(adjustList));

        },
        error: function(){
            $.toast("网络连接超时", "text");
        }
    });
};
// 调配操作
var taskDialog = function(){
    $('.detailGroup').on('click','.touchBtn',function(){
        // btnType : 1 清货按钮，2 补后余按钮, 5 生产日期, 6 生产批号
        var btnType = $(this).attr('data-type');
        var clearCount = $(this).parents('.itemLi').find('.hideBaseInfo').attr('data-clear');
        var marginCount = $(this).parents('.itemLi').find('.hideBaseInfo').attr('data-margin');
        var fullCount = $(this).parents('.itemLi').find('.hideBaseInfo').attr('data-full');
        var targetNum = $(this).parents('.itemLi').find('.hideBaseInfo').attr('data-target-num');
        var manufactureDate = $(this).parents('.itemLi').find('.manufactureDate').val();
        var batchNum = $(this).parents('.itemLi').find('.batchNum').val();
        var dialogHtml = '<div class="widgetDialog new"><div class="digoWrap"><div class="topBox pt20">';
        // 1 清货按钮
        if(btnType == 1){
            dialogHtml += '<h4>清出操作</h4><p><span>请输入实际清出货物数量</span></p>'+
                '<section><input type="tel" class="bigText" id="dialogNum" placeholder="请输入整数" maxlength="3" data-type="1" data-full="'+ fullCount +'" data-target-num="'+ targetNum +'" value="'+ clearCount +'"></section>';
        }
        // 2 补后余按钮
        else if(btnType == 2){
            dialogHtml += '<h4>补后余操作</h4><p><span>请输入实际货道余量</span></p>'+
                '<section><input type="tel" class="bigText" id="dialogNum" placeholder="请输入整数" maxlength="3" data-type="2" data-full="'+ fullCount +'" data-target-num="'+ targetNum +'" value="'+ marginCount +'"></section>';
        }
        // 5 生产日期
        else if(btnType == 5){
            dialogHtml += '<h4>生产日期</h4><p><span>格式要求:20180101</span></p>'+
                '<section><input type="tel" class="bigText" id="dialogNum" placeholder="格式要求:20180101" maxlength="8" data-type="5" value="'+manufactureDate+'"></section>';
        }
        // 6 生产批号
        else if(btnType == 6){
            dialogHtml += '<h4>生产批号</h4><p><span>可输入英文数字,2-50个字符(选填)</span></p>'+
                '<section><input type="text" class="bigText" id="dialogNum" placeholder="请输入生产批号(选填)" data-type="6" value="'+batchNum+'"></section>';
        }
        dialogHtml += '</div><div class="bottomBox"><div class="digoBtn" id="dialogBtnCancel">取消</div><div class="digoBtn" id="dialogBtnYes">确定</div></div></div></div>';
        $('.widgetDialog').remove();
        $(this).parents('.itemLabel').append(dialogHtml);
    });
    //确定
    $(document).on('click','#dialogBtnYes',function(){
        // btnType : 1 清货按钮，2 补后余按钮, 5 生产日期, 6 生产批号
        var btnType = $('#dialogNum').attr('data-type');
        var dialogNum = parseInt($.trim($('#dialogNum').val()));
        var batchNum = $('#dialogNum').val(); // 6 生产批号
        //var dialogNum = $('#dialogNum').val();
        var fullCount = $('#dialogNum').attr('data-full');
        var targetNum = $('#dialogNum').attr('data-target-num');
        var reg = /^[0-9]*$/;
        var regYear = /^(?:(?:(?:(?=\d{2}(?:(?:[02468][048])|(?:[13579][26])))\d{4}02(?!00)[0-2]\d))|(?:\d{4}(?:(?:(?:(?:0[469])|(?:11))(?!00)(?:(?:[0-2]\d)|(?:30)))|(?:(?:(?:0[13578])|(?:1[02]))(?!00)(?:(?:[0-2]\d)|(?:3[01])))|(?:02(?!(?:00)|(?:29))[0-2]\d))))$/;
        var regA9 = /^[A-Za-z0-9]{2,50}$/;
        if(btnType == 1 || btnType == 2){
            //1 清货按钮，2 补后余按钮
            if(!reg.test(dialogNum)){
                $.toast('格式不正确','text');
            }else if(dialogNum > targetNum){
                $.toast('不能超过满量值或目标值','text');
            }else{
                if(btnType == 1){
                    $(this).parents('.itemLi').find('.hideBaseInfo').attr('data-clear',dialogNum);
                    $(this).parents('.itemLi').find('.clearCount').text(dialogNum);
                }else if(btnType == 2){
                    $(this).parents('.itemLi').find('.hideBaseInfo').attr('data-margin',dialogNum);
                    $(this).parents('.itemLi').find('.marginCount').text(dialogNum);
                }
                $('.widgetDialog').remove();
            }
        }else if(btnType == 5){
            //5 生产日期
            if(dialogNum == ''){
                $.toast('请输入生产日期','text');
            }else if(!regYear.test(dialogNum)){
                $.toast('生产日期格式不对','text');
            }else{
                $(this).parents('.itemLi').find('.manufactureDate').val(dialogNum);
                $('.widgetDialog').remove();
            }
        }else if(btnType == 6){
            //6 生产批号
            if(!regA9.test(batchNum)){
                $.toast('生产批号格式不对','text');
            }else{
                $(this).parents('.itemLi').find('.batchNum').val(batchNum);
                $('.widgetDialog').remove();
            }
        }
    });
    //取消
    $(document).on('click','#dialogBtnCancel',function(){
        $('.widgetDialog').remove();
    });
};
// toService
var toService = function(){
        var taskId = $("#taskVmId").val();
        var fillData = [];
        var cleanData = [];
        var adjustData = [];
        // 补货
        $('#fillList .hideBaseInfo').each(function(){
            var channelCode = $(this).attr('data-code');
            var channelId = $(this).val();
            var clearCount = $(this).attr('data-clear');
            var currCount = $(this).attr('data-curr');
            var fillCount = $(this).attr('data-fill');
            var fullCount = $(this).attr('data-full');
            var marginCount = $(this).attr('data-margin');
            var skuId = $(this).attr('data-skuid');
            var termInfo = $(this).attr('data-term');
            var manufactureDate = $(this).siblings('.otherInfo').find('.manufactureDate').val();
            var batchNum = $(this).siblings('.otherInfo').find('.batchNum').val();
            var dataMap = {"channelCode":channelCode,"channelId":channelId,"clearCount":clearCount,"currCount":currCount,"fillCount":fillCount,"fullCount":fullCount,"marginCount":marginCount,"skuId":skuId,"termInfo":termInfo,"manufactureDate":manufactureDate,"batchNum":batchNum};
            fillData.push(dataMap);
        });
        // 清货
        $('#cleanList .hideBaseInfo').each(function(){
            var channelCode = $(this).attr('data-code');
            var channelId = $(this).val();
            var clearCount = $(this).attr('data-clear');
            var currCount = $(this).attr('data-curr');
            var fillCount = $(this).attr('data-fill');
            var fullCount = $(this).attr('data-full');
            var marginCount = 0;
            var skuId = $(this).attr('data-skuid');
            var dataMap = {"channelCode":channelCode,"channelId":channelId,"clearCount":clearCount,"currCount":currCount,"fillCount":fillCount,"fullCount":fullCount,"marginCount":marginCount,"skuId":skuId};
            cleanData.push(dataMap);
        });
        // 调量
        $('#adjustList .hideBaseInfo').each(function(){
            var channelCode = $(this).attr('data-code');
            var channelId = $(this).val();
            var clearCount = $(this).attr('data-clear');
            var currCount = $(this).attr('data-curr');
            var fillCount = $(this).attr('data-fill');
            var fullCount = $(this).attr('data-full');
            var marginCount = $(this).attr('data-margin');
            var skuId = $(this).attr('data-skuid');
            var termInfo = $(this).attr('data-term');
            var manufactureDate = $(this).siblings('.otherInfo').find('.manufactureDate').val();
            var batchNum = $(this).siblings('.otherInfo').find('.batchNum').val();
            var dataMap = {"channelCode":channelCode,"channelId":channelId,"clearCount":clearCount,"currCount":currCount,"fillCount":fillCount,"fullCount":fullCount,"marginCount":marginCount,"skuId":skuId,"termInfo":termInfo,"manufactureDate":manufactureDate,"batchNum":batchNum};
            adjustData.push(dataMap);
        });
        var toServiceData = {
            "fillList":fillData,
            "cleanList":cleanData,
            "adjustList":adjustData,
            "taskId":taskId
        };
        console.log('toServiceData:'+JSON.stringify(toServiceData));
        $.ajax({
            url: "/mp/mpTask/delivery/finish",
            data: {"content": JSON.stringify(toServiceData)},
            async: false,
            type: "POST",
            success: function (data) {
                if (data.type == "success") {
                    window.location.href = data.url;
                } else {
                    if(data.message != null && data.message != undefined && data.message != ''){
                        $.toast(data.message, "text");
                    }else{
                        $.toast("完成任务失败", "text");
                    }
                }
            },
            error: function () {
                $.toast("网络连接超时", "text");
            }
        })
};
// 加载数据
var loadTaskData = function(fillList,cleanList,adjustList,taskStatus){
    //补货列表
    if(fillList.length != 0){
        $.each(fillList,function (index,item){
            var _skuType = '';
            if(item.skuType == '1'){
                _skuType = '<strong class="codeFlag red">预定</strong>';
            }
            var html = '<div class="itemLi" data-type="'+ item.skuType +'">'+
            '<div class="topInfo">'+
            '<div class="imgInfo"><span><img src="'+ item.skuImg +'"></span></div>'+
            '<div class="detailInfo"><strong class="codeFlag">'+ item.channelCode +'</strong>'+_skuType+'<span>'+ item.skuName +'</span></div></div>'+
            '<input type="hidden" class="hideBaseInfo" value="'+ item.channelId +'" data-term="'+ item.termInfo +'" data-skuid="'+ item.skuId +'" data-code="'+ item.channelCode +'" data-clear="'+ item.clearCount +'" data-curr="'+ item.currCount +'" data-fill="'+ item.fillCount +'" data-full="'+ item.fullCount +'" data-margin="'+ item.marginCount +'" data-target-num="'+ item.targetNum +'">'+
            '<div class="methodsArea">'+
            '<div class="itemLabel">'+
            '<p><small>满量</small></p>'+
            '<div class="numFlag"><b>'+ item.fullCount +'</b></div>'+
            '</div>'+
            '<div class="itemLabel">'+
            '<p><small>清出</small></p>';
            if(taskStatus == 2){
                html += '<div class="numFlag touchBtn" data-type="1"><b class="clearCount">'+ item.clearCount +'</b></div>';
            }else{
                html += '<div class="numFlag"><b>'+ item.clearCount +'</b></div>';
            }
            html += '</div><div class="itemLabel"><p><small>应补</small></p><div class="numFlag"><b>'+ item.fillCount +'</b></div></div><div class="itemLabel"><p><small>补后余</small></p>';
            if(taskStatus == 2){
                html += '<div class="numFlag touchBtn" data-type="2"><b class="marginCount">'+ item.marginCount +'</b></div>';
            }else{
                html += '<div class="numFlag"><b>'+ item.marginCount +'</b></div>';
            }
            html += '</div></div>';
            if(taskStatus == 2 && item.termInfo == 1){
                //判断是否有默认值
                var _manufactureDate = '';
                var _batchNum = '';
                if(item.manufactureDate == undefined || item.manufactureDate == '' || item.manufactureDate == null){
                    _manufactureDate = '';
                }else{
                    _manufactureDate = item.manufactureDate;
                }
                if(item.batchNum == undefined || item.batchNum == '' || item.batchNum == null){
                    _batchNum = '';
                }else{
                    _batchNum = item.batchNum;
                }
                html += '<div class="otherInfo">'+
                '<div class="itemLabel"><span>生产日期</span><input class="inputBox manufactureDate touchBtn" data-type="5" value="'+_manufactureDate+'" readonly type="tel" placeholder="格式:20180101"></div>'+
                '<div class="itemLabel"><span>生产批号</span><input class="inputBox batchNum touchBtn" data-type="6" value="'+_batchNum+'" readonly type="tel" placeholder="格式:CN2018010100MD"></div>'+
                '</div>';
            }
            html += '</div>';
            $('#taskDetailList .fillList').append(html);
        });
    }else{
        $('#taskDetailList .fillList').html('<div class="emptyHtml">暂无数据</div>');
    }
    //清货列表
    if(cleanList.length != 0){
        $.each(cleanList,function (index,item){
            var _skuType = '';
            if(item.skuType == '1'){
                _skuType = '<strong class="codeFlag red">预定</strong>';
            }
            var html = '<div class="itemLi">'+
                '<div class="topInfo">'+
                    '<div class="imgInfo"><span><img src="'+ item.skuImg +'"></span></div>'+
                    '<div class="detailInfo"><strong class="codeFlag">'+ item.channelCode +'</strong>'+_skuType+'<span>'+ item.skuName +'</span></div>'+
                '</div>'+
                '<input type="hidden" class="hideBaseInfo" value="'+ item.channelId +'" data-term="'+ item.termInfo +'" data-skuid="'+ item.skuId +'" data-code="'+ item.channelCode +'" data-clear="'+ item.clearCount +'" data-curr="'+ item.currCount +'" data-fill="'+ item.fillCount +'" data-full="'+ item.fullCount +'">'+
                '<div class="methodsArea w2"><div class="itemLabel"><p><small>清出</small></p>';
            if(taskStatus == 2){
                html += '<div class="numFlag touchBtn" data-type="1"><b class="clearCount">'+ item.clearCount +'</b></div>';
            }else{
                html += '<div class="numFlag"><b>'+ item.clearCount +'</b></div>';
            }
                html += '</div><div class="itemLabel"><p><small>清后余</small></p><div class="numFlag"><b>0</b></div></div></div></div>';
            $('#taskDetailList .cleanList').append(html);
        });
    }else{
        $('#taskDetailList .cleanList').html('<div class="emptyHtml">暂无数据</div>');
    }
    //调量列表
    if(adjustList.length != 0){
        $.each(adjustList,function (index,item){
            var _skuType = '';
            if(item.skuType == '1'){
                _skuType = '<strong class="codeFlag red">预定</strong>';
            }
            var html = '<div class="itemLi">'+
                '<div class="topInfo">'+
                '<div class="imgInfo"><span><img src="'+ item.skuImg +'"></span></div>'+
                '<div class="detailInfo"><strong class="codeFlag">'+ item.channelCode +'</strong>'+_skuType+'<span>'+ item.skuName +'</span></div>'+
                '</div>'+
                '<input type="hidden" class="hideBaseInfo" value="'+ item.channelId +'" data-term="'+ item.termInfo +'" data-skuid="'+ item.skuId +'" data-code="'+ item.channelCode +'" data-clear="'+ item.clearCount +'" data-curr="'+ item.currCount +'" data-fill="'+ item.fillCount +'" data-full="'+ item.fullCount +'" data-margin="'+ item.marginCount +'" data-target-num="'+ item.targetNum +'">'+
                '<div class="methodsArea">'+
                '<div class="itemLabel">'+
                '<p><small>满量</small></p>'+
                '<div class="numFlag"><b>'+ item.fullCount +'</b></div>'+
                '</div>'+
                '<div class="itemLabel">'+
                '<p><small>清出</small></p>';
            if(taskStatus == 2){
                html += '<div class="numFlag touchBtn" data-type="1"><b class="clearCount">'+ item.clearCount +'</b></div>';
            }else{
                html += '<div class="numFlag"><b>'+ item.clearCount +'</b></div>';
            }
            html += '</div><div class="itemLabel"><p><small>应补</small></p><div class="numFlag"><b>'+ item.fillCount +'</b></div></div><div class="itemLabel"><p><small>补后余</small></p>';
            if(taskStatus == 2){
                html += '<div class="numFlag touchBtn" data-type="2"><b class="marginCount">'+ item.marginCount +'</b></div>';
            }else{
                html += '<div class="numFlag"><b>'+ item.marginCount +'</b></div>';
            }
            html += '</div></div>';
            if(taskStatus == 2 && item.termInfo == 1){
                //判断是否有默认值
                var _manufactureDate = '';
                var _batchNum = '';
                if(item.manufactureDate == undefined || item.manufactureDate == '' || item.manufactureDate == null){
                    _manufactureDate = '';
                }else{
                    _manufactureDate = item.manufactureDate;
                }
                if(item.batchNum == undefined || item.batchNum == '' || item.batchNum == null){
                    _batchNum = '';
                }else{
                    _batchNum = item.batchNum;
                }
                html += '<div class="otherInfo">'+
                    '<div class="itemLabel"><span>生产日期</span><input class="inputBox manufactureDate touchBtn" data-type="5" value="'+_manufactureDate+'" readonly type="tel" placeholder="格式:20180101"></div>'+
                    '<div class="itemLabel"><span>生产批号</span><input class="inputBox batchNum touchBtn" data-type="6" value="'+_batchNum+'" readonly type="tel" placeholder="格式:CN2018010100MD"></div>'+
                    '</div>';
            }
            html += '</div>';
            $('#taskDetailList .adjustList').append(html);
        });
    }else{
        $('#taskDetailList .adjustList').html('<div class="emptyHtml">暂无数据</div>');
    }
};