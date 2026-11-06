<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">告警订阅</h4>
        </div>
        <form href="/vmStatus/saveAlertSysUser" method="post">
        <div class="modal-body">
            <!-- moveSkuMethod -->
            <label>当前支持温度异常告警通知</label>
            <div class="arrMidGrid mb10 main_commontable" style="border: 0;text-align: left;">
                <style>
                    .arrMidGrid{ position: relative; padding-top: 10px;}
                    .arrMidGrid .skuGroup{ border: 1px solid #e6e6e6; height: 360px; background: #f6f6f6; padding: 5px; overflow: auto;}
                    .arrMidGrid .arrMidBox{ position: absolute; left: 50%; top:50%; margin-top: -15px; margin-left: -25px; width: 50px; height: 70px; text-align: center;}
                    .arrMidGrid .arrBtn{ opacity: .9; width: 50px; margin: 0 auto 5px; background: #04ac71; color: #fff; font-size: 12px; padding: 0 5px; height: 30px; line-height: 30px; text-align: center; -moz-border-radius: 4px; border-radius: 4px;}
                    .arrMidGrid .arrBtn.delete{ background: #fd7d1d; margin-top: 10px;}
                    .userItem{ height: 36px; cursor: pointer; position: relative; text-align: left;-moz-border-radius: 4px; border-radius: 4px; margin-bottom: 5px; font-size: 12px; color:#666; line-height: 34px; background: #fff; border: 1px solid #eee; padding-right: 30px; padding-left: 15px; text-overflow: ellipsis;white-space: nowrap; overflow: hidden;}
                    .userItem span{ color: #666;}
                    .userItem:hover{ background: #f9f9f9;}
                    .userItem.checked:after{ content: ""; display: block; position: absolute; right: 8px; top:10px; width: 16px; height: 16px; background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABIElEQVQ4T6WTQXKCQBREu4F9PEJykpgbYJWsjTeAk8AN1LVWwQ2CJ9EjJHuYTg3UEAQ0pMIO5vfr6f8/xD8fjvR5vPBqbjwoBLhsz1UasDC+Dlhln33NDcA/xSGAHcHF1MUEWfG2XmeFO+8AVkwwn5NI0MpBWkAeL/wal3vOQ6i9Se3jxcZpAN4xjj0ynePuaoyUmCjLGkBwTEoQr48AorYQlwQ2bV9xrqLUvgPBKdGvYgAUd/26ap1yBBBwAFW64sZ5Qmy//QAGEZyoG9XAeRRhqokWQoMryI+peDdNbMZY8Uriac4kJHzVgZ67MVrRXxYJ0lsVZWXTm75js8ri/t5NrDOh0IlHALeVXoV3Dwy73RDOBipMgP3Dn2lO/mHNN99aiBFWGZNVAAAAAElFTkSuQmCC"); text-align: center; line-height: 16px;}
                </style>
                <div class="arrMidBox">
                    <div class="arrBtn add btnAddSku" id="addUserBtn"><span>></span></div>
                    <div class="arrBtn delete btnAddSku" id="deleteUserBtn"><span><</span></div>
                    <%--<button type="button" id="multiselect_rightSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-right"></i></button>
                    <button type="button" id="multiselect_leftSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-left"></i></button>--%>
                </div>
                <!-- waitSkuGrid -->
                <div class="w240 pull-left">
                    <p class="titleTips">用户列表:</p>
                    <div class="skuGroup mt10 waitSkuGrid" id="waitSkuGrid">
                        <%--<div class="userItem"><span class="mr5">樊中扬</span><span>fanZhongYang</span></div>
                        <div class="userItem checked"><span class="mr5">樊中扬</span><span>fanZhongYang</span></div>
                        <div class="userItem"><span class="mr5">樊中扬</span><span>fanZhongYang</span></div>--%>
                    </div>
                </div>
                <!-- / waitSkuGrid -->
                <!-- readySkuGrid -->
                <div class="w240 pull-right">
                    <p class="titleTips">订阅用户:</p>
                    <div class="skuGroup mt10 readySkuGrid" id="readySkuGrid">

                    </div>
                </div>
                <div class="clearfix"></div>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="button" id="toSaveUserBtn" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </div>
        </form>
    </div>
</div>
<%--======================================--%>
<script type="text/javascript">
    $(function(){
        /*$('#alarmsubscription').on('shown.bs.modal', function(){
            console.log('....');
            loadWaitData();
        });*/
        loadWaitData();

        //添加
        $('#addUserBtn').click(function(){
            skuChecked();
            readSkuList();
        });
        //移除
        $('#deleteUserBtn').click(function(){
            removeReadySkuList();
        });

        //提交
        $('#toSaveUserBtn').click(function(){
            var thisBtn = $(this);
            thisBtn.text('提交中').attr('disabled',true);
            var userData = [];
            $('#readySkuGrid .userItem').each(function(){
                var userId = $(this).attr('data-id');
                userData.push(userId);
            });
            $.ajax({
                url: "/vmStatus/saveAlertSysUser",
                type: "POST",
                data: {"userList":userData},
                async: false,
                success: function(data){
                    console.log(data);
                    if(data.type == 'ok'){
                        $('#alarmsubscription').modal('hide');
                    }else{
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                        thisBtn.text('确定').attr('disabled',false);
                    }
                },
                error:function(){
                    thisBtn.text('确定').attr('disabled',false);
                    layer.msg('连接超时，请稍后重试', {shade: [0.5, '#fff'], time: 2000});
                }
            });
        });

        /*//选择用户 - 订阅用户
        $('#readySkuGrid').on('click','.userItem',function () {
            $(this).toggleClass('checked');
        });*/
    });

    //选择用户 - 用户列表 订阅用户
    $('#waitSkuGrid').on('click','.userItem',function(){
        $(this).toggleClass('checked');
    });
    $('#readySkuGrid').on('click','.userItem',function(){
        $(this).toggleClass('checked');
    });
    // skuChecked
    function skuChecked(){
        var userItem = {};
        var list = [];
        $('#waitSkuGrid .userItem.checked').each(function(){
            var userId = $(this).attr('data-id');
            var userName = $(this).attr('data-name');
            var map = {'id':userId,'name':userName};
            list.push(map);
        });
        userItem.userArray = list;
        console.log("userItem:"+JSON.stringify(userItem));
        $('#readySkuGrid').empty();
        $.each(userItem.userArray,function (index,item){
            var userHtml = '<div class="userItem" data-id="'+item.id+'" data-name="'+item.name+'"><span>'+item.name+'</span></div>';
            $('#readySkuGrid').append(userHtml);
        });
    }

    // 已选-订阅用户
    function readSkuList(){
        var _readySkuId,waitSkuId;
        $('#readySkuGrid .userItem').each(function(){
            var _thisReady = $(this);
            _readySkuId = _thisReady.attr('data-id');
            $('#waitSkuGrid .userItem').each(function(){
                waitSkuId = $(this).attr('data-id');
                if(_readySkuId == waitSkuId){
                    //_thisReady.remove();
                    $(this).addClass('checked').hide();
                }
            });
        });
    }

    // 已选-订阅用户-移除
    function removeReadySkuList(){
        var _readySkuId,waitSkuId;
        $('#readySkuGrid .userItem.checked').each(function(){
            var _thisReady = $(this);
            _readySkuId = _thisReady.attr('data-id');
            $('#waitSkuGrid .userItem').each(function(){
                waitSkuId = $(this).attr('data-id');
                if(_readySkuId == waitSkuId){
                    _thisReady.remove();
                    $(this).removeClass('checked').show();
                }
            });
        });
    }

    //初始化
    function loadWaitData(){
        $.ajax({
            url: "/vmStatus/companyUserListData",
            type: "POST",
            async: false,
            success: function(data){
                console.log(data);
                // 用户列表
                var subscribeUser = data.data.subscribeUser;
                if(subscribeUser.length > 0){
                    $.each(subscribeUser,function(index,item){
                        var subHtml = '<div class="userItem" data-id="'+item.id+'" data-name="'+item.name+'"><span>'+item.name+'</span></div>';
                        $('#waitSkuGrid').append(subHtml);
                    });
                }
                // 订阅用户
                var userList = data.data.userList;
                if(userList.length > 0){
                    $.each(userList,function(index,item){
                        var userHtml = '<div class="userItem" data-id="'+item.id+'" data-name="'+item.name+'"><span>'+item.name+'</span></div>';
                        $('#readySkuGrid').append(userHtml);
                    });
                }
                readSkuList();
            },
            error:function(){
                layer.msg('连接超时，请稍后重试', {shade: [0.5, '#fff'], time: 2000});
            }
        });
    }



    //提交
    /*function warning() {
        layer.confirm('确定要修改告警订阅信息吗？', {
            btn: ['确定', '取消'],
            title: '提示',
        }, function () {
            var arr = new Array(); //数组定义标准形式，不要写成Array arr = new Array();
            $("#multiselect_to option").each(function () {
                // var txt = $(this).text(); //获取单个text
                var val = $(this).val(); //获取单个value
                var node = val;
                arr.push(node);
            });

            $.ajax({
                type : 'post',
                url : "/vmStatus/saveAlertSysUser",
                data : {userIds:arr.toString(),vmIds:"1"},
                async : true,
                dataType : 'json',
                cache : false,
                success : function(data) {
                    layer.msg(data.msg, {shade: [0.8, '#fff'], time: 2000});
                    setTimeout("window.location.href=\"/vmStatus/list\"",1000);
                }
            });
        });
    }*/



</script>