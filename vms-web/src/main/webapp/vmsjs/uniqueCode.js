var uniqueCode = function(){
    var searchEvent = function(){
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
    };

    var unbind = function($this){
        var dataSet=$this.attr('data-set');
        var url = $this.attr('href');
        var chkBox = $(dataSet);
        var ids="";
        if(chkBox.length == 0){
            layer.msg("请先选中需要解绑的售货机",{shade: [0.5, '#fff'],time:2000});
            return false;
        }else{
            $(dataSet).each(function() {
                ids += $(this).val()+",";
            });
            ids = ids.substring(0,ids.length-1);
            layer.confirm('是否确定解绑所选中的'+ chkBox.length +'台售货机的MAC地址？', {
                btn: ['确定','取消'],
                title: '提示',
            }, function(){ //成功后执行
                //这里ajax处理服务端删除
                $.ajax({
                    type : 'post',
                    url :url,
                    data : {ids:ids},
                    async : true,
                    dataType : 'json',
                    cache : false,
                    success : function(data) {
                        if(data.type=="success"){
                            layer.msg('解绑成功',{shade: [0.5, '#fff'],time:2000});
                            searchEvent();
                        }else{
                            layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        }
                    }
                });
            }, function(index){
                layer.close(index);//关闭窗口
            });
        }
    };

    var reset = function(){
        $('#resetButton').bind('click',function(){
            $("#search-form input:text").val("");
            $('.select2me').val(null).trigger("change");
            $('.select2me').val("请选择").trigger("change");

        });
    };
    var bindSearch = function(){
        $("#searchButton").bind("click", function(){
            searchEvent();
        });
    };

    var bindUnbind = function(){
        $(".btn_unbind").bind("click", function(){
            unbind($(this));
        });
    };

    return{
        init:function(){
            bindSearch();
            reset();
            bindUnbind();
        }
    }
}();
