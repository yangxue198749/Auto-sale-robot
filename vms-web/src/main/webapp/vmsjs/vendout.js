var Vendout = function(){
        var searchEvent = function(){
            $("#search-form").attr("action","/vendout/table");
            var options = {
                type: 'POST',
                beforeSubmit:function(){
        //	    			Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success:function (data)  {
                    var hasLogin = Metronic.hasLogin();
                    if(hasLogin==false){
                        window.location.href="/";
                        return;
                    }
                    $("#pagediv").html(data);
        //		    		Metronic.init() ;
                }
            };
            $("#search-form").ajaxSubmit(options);
        }
        var reset = function(){
            $('#resetButton').bind('click',function(){
                $("#search-form input:text").val("");
                $('.select2me').val(null).trigger("change");
                $('.select2me').val("请选择").trigger("change");

            });
        }
        var bindSearch = function(){
            $("#searchButton").bind("click", function(){
                searchEvent();
            });
        }

        var exportMtVmList = function(){
            $("#exportButton").bind("click",function () {
                $("#search-form").attr("action","/vendout/exportVendout");
                var options = {
                    type: 'POST',
                    beforeSubmit: function() {
                        // Metronic.blockUI({target: "#pagediv",boxed: true});
                    },
                    success: function(data) {
                        var hasLogin = Metronic.hasLogin();
                        if(hasLogin==false){
                            window.location.href="/";
                            return;
                        }
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                    }
                };
                $("#search-form").ajaxSubmit(options);
            });
        };
    var refund = function(){
        $("#pagediv").on("click",".btn_refund",function () {
        //$(".btn_refund").bind("click",function () {
            //var thisBtn = $(this);
            var saleId = $(this).attr('data-sale-id');
            layer.confirm('该笔消费是否确认需要退款？', {
                title: '提示',
                btn: ['确定','取消']
            }, function(){
                //href="/vendout/refund" saleId="" + getId() + ""
                $.post("/vendout/refund",{detailId:saleId},function(data){
                    console.log(data);
                    if(data.code == '0'){
                        layer.msg(data.message,{shade:[0.6,'#fff'],time:1000});
                    }else{
                        layer.msg('操作失败，请稍后重试',{shade:[0.6,'#fff'],time:1000});
                    }
                    searchEvent();
                })
            }, function(){
                //取消操作
            });
        });
    };
        var addMemo = function () {
            $(".btn-success").bind("click",function () {
                var btn = $("#add-memo-form :submit");
                btn.button('loading');
                var options = {
                    success: function (data) {
                        btn.button('reset');
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000,});
                        if (data.type == "success") {
                            btn.remove();
                            $('#addRemarkModel').modal('hide');
                            searchEvent();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            });
        }

        return{
            init:function(){
                bindSearch();
                reset();
                exportMtVmList();
                refund();
            },
            search:function(){
                searchEvent();
            },
            addRemarkModel:function(){
                addMemo();
            },

        }
}();