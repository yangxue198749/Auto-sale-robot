var Order = function() {
    var searchEvent = function() {
        $("#search-form").attr("action","/order/listPage");
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
                $("#pagediv").html(data);
                // Metronic.init() ;
            }
        };
        $("#search-form").ajaxSubmit(options);
    }

    var exportEvent = function() {
        $("#search-form").attr("action","/order/export");
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
    }
    var reset = function() {
        $('#resetButton').bind('click', function() {
            $("#search-form input:text").val("");
            $('.select2me').val(null).trigger("change");
            $('.select2me').val("请选择").trigger("change");
        });
    }
    var bindSearch = function() {
        $("#searchButton").bind("click", function() {
            searchEvent();
        });
    }
    var bindExport = function() {
        $("#exportButton").bind("click", function() {
            exportEvent();
        });
    }

    var bindRefund = function() {
        //refund
        $("#pagediv").on('click','.btn_refund',function(){
            var $this = $(this);
            var url = $this.attr('href');
            var id = $this.attr('data-sale-id');
            layer.confirm('该笔消费是否确认需要退款？', {
                btn: ['确定','取消'],
                title: '提示',
            }, function(){ //成功后执行
                //这里ajax处理服务端删除
                $.ajax({
                    type : 'post',
                    url :url,
                    data : {detailId:id},
                    async : true,
                    dataType : 'json',
                    cache : false,
                    success : function(data) {
                        if(data.type=="success"){
                            layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                            searchEvent();
                        }else{
                            layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                        }
                    }
                });
            }, function(index){
                layer.close(index);//关闭窗口
            });

        });
    }


    return {
        init: function() {
            bindSearch();
            bindExport();
            bindRefund();
            reset();

        },
    }
}();
