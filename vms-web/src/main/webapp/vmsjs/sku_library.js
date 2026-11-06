var SkuLibrary = function(){
    var searchEvent = function(){
        var options = {
            type: 'POST',
            beforeSubmit:function(){
            },
            success:function (data)  {
                var hasLogin = Metronic.hasLogin();
                if(hasLogin==false){
                    window.location.href="/";
                    return;
                }
                $("#pagediv").html(data);
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
    var addSkuLibraryEvent = function(){
        $('#addToSkuLibraryBtn').bind('click',function(){
            var checks = $("#addStock .choiceTheGoods li.on input[name='skuId']");
            if(!checks.length>=1){
                layer.msg("请至少选择一个商品",{shade: [0.5, '#fff'],time:1000,});
                return ;
            }else{
                var skuIds = "";
                checks.each(function(){
                    skuIds += $(this).val()+",";
                });
                skuIds = skuIds.substring(0,skuIds.length-1);
                $.ajax({
                    type : 'post',
                    url : '/skuLibrary/addToSkuLibrary',
                    data : {"ids":skuIds},
                    async : true,
                    dataType : 'json',
                    success : function(data) {
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
                        if(data.type=="success"){
                            $('#addStock').modal('hide');
                            searchEvent();
                        }
                    }
                });
            }
        });
    }


    return{
        init:function(){
            bindSearch();
            reset();
        },
        addSkuLibrary:function(){
            addSkuLibraryEvent();
        }

    }
}();