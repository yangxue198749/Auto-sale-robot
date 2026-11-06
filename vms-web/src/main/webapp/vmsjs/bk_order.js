var BkOrder = function () {
    var searchEvent = function () {
        $("#search-form").attr("action", "/bk/order/page");
        var options = {
            type: 'POST',
            beforeSubmit: function () {
                // Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function (data) {
                var hasLogin = Metronic.hasLogin();
                if (hasLogin == false) {
                    window.location.href = "/";
                    return;
                }
                $("#pagediv").html(data);
                // Metronic.init() ;
            }
        };
        $("#search-form").ajaxSubmit(options);
    }

    var reset = function () {
        $('#resetButton').bind('click', function () {
            $("#search-form input:text").val("");
            $('.select2me').val(null).trigger("change");
            $('.select2me').val("请选择").trigger("change");
        });
    }
    var bindSearch = function () {
        $("#searchButton").bind("click", function () {
            searchEvent();
        });
    }

    var bindExport = function () {
        $("#exportButton").bind("click", function () {
            exportEvent();
        });
    }

    var exportEvent = function () {
        var begin = $("#DateStartOne").val();
        var end = $("#DateEndOne").val();
        if (begin != '' && end != '' && tolDateNum(begin, end) < 31) {
            $("#search-form").attr("action", "/bk/order/export");
            $("#search-form").submit();
        } else {
            layer.msg('暂只支持导出最多30天数据 ，请重新选择！', {shade: [0.5, '#fff'], time: 2000,});
        }

    }

    var bindRefund = function () {
        //refund
        $("#pagediv").on('click', '.refundBtn', function () {
            var $this = $(this);
            var url = $this.attr('href');
            var goodId = $this.attr('goodId');
            layer.confirm('该笔消费是否确认需要退款？', {
                btn: ['确定', '取消'],
                title: '提示',
            }, function () { //成功后执行
                //这里ajax处理服务端删除
                $.ajax({
                    type: 'post',
                    url: url,
                    data: {goodId: goodId},
                    async: true,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        if (data.type == "success") {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000,});
                            searchEvent();
                        } else {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000,});
                        }
                    }
                });
            }, function (index) {
                layer.close(index);//关闭窗口
            });

        });
    }

    function tolDateNum(sDate1, sDate2) {
        var aDate, bDate, oDate1, oDate2, iDays;
        aDate = sDate1.split("-");
        oDate1 = new Date(aDate[0] + '-' + aDate[1] + '-' + aDate[2]);  //转换为yyyy-MM-dd格式
        bDate = sDate2.split("-");
        oDate2 = new Date(bDate[0] + '-' + bDate[1] + '-' + bDate[2]);
        iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24); //把相差的毫秒数转换为天数
        return parseInt(iDays + 1);
    }


    return {
        init: function () {
            bindSearch();
            bindRefund();
            reset();
            bindExport();

        },
    }
}();
