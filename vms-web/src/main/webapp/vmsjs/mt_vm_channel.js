var MtVmChannel = function () {
    var channel_searchEvent = function () {
        var options = {
            type: 'POST',
            beforeSubmit: function () {
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function (data) {
                var hasLogin = Metronic.hasLogin();
                if (hasLogin == false) {
                    window.location.href = "/";
                    return;
                }
                $("#st_vm_channel_div").html(data);
//		    		Metronic.init() ;
            }
        };
        $("#st_vm_channel_search_form").ajaxSubmit(options);
    }
    var channel_sku_searchEvent = function () {
        var options = {
            type: 'POST',
            beforeSubmit: function () {
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function (data) {
                $("#st_vm_channel_sku_div").html(data);
//		    		Metronic.init() ;
            }
        };
        $("#st_vm_channel_sku_search_form").ajaxSubmit(options);
    }
    var bindSearch = function () {
        $("#st_vm_channel_search_btn").bind("click", function () {
            channel_searchEvent();
        });
    }
    var bindSkuSearch = function () {
        $("#st_vm_channel_sku_search_btn").bind("click", function () {
            channel_sku_searchEvent();
        });
    }
    var getChannelDetail = function (ele) {
        var mtVm = $('#' + ele + " input[name='vmId']");
        if ('undefined' != mtVm) {
            $.ajax({
                type: 'post',
                url: path + '/mtVmChannel/getChannelDetail',
                data: {"vmId": mtVm.val()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    if (data.type == "success") {
                        $("#" + ele).html(data.html);
                        Metronic.imgEmpty(ele);
                    }
                }
            });
        }
    }
    var submitSelectSku = function () {
        $('#relateButton').bind('click', function () {
            var mtVm = $("#editChannelDetailDiv").find("input[name='vmId']");
            var checks = $("#st_vm_channel_sku_div li.on input[name='skuId']");
            if (checks.length != 1) {
                layer.msg("请选择一个商品", {shade: [0.5, '#fff'], time: 1000,});
                return;
            } else {
                var skuId = checks.val();
                $.ajax({
                    type: 'post',
                    url: path + '/mtVmChannel/relateSku',
                    data: {"channelIds": $('#relateChannelIds').val(), "skuId": skuId, "vmId": mtVm.val()},
                    async: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.type == "success") {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 1000,});
                            getChannelDetail('editChannelDetailDiv');
                            $('#Child_choiceTheGoods').modal('hide');
                            $('#Child_choiceTheGoods').html("");

                        } else {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 1000,});
                        }
                    }
                });
            }
        });
    }
    var submitMtVmChannelBySt = function () {


        var validateForm = $("#mt_vm_channel_form_st");
        validateForm.validate({
            doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {},
            messages: {},
            errorPlacement: function (error, element) { // render error placement for each input type
                // error.insertAfter(element); // for other inputs, just perform default behavior
            },
            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
            },
            unhighlight: function (element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error'); // set error class to the control group
            },
            success: function (label) {
                label.addClass('valid').closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
            },
            submitHandler: function (form) {
                var btn = $("#mt_vm_channel_form_st:submit");
                btn.button('loading');
                var options = {
                    success: function (data) {
                        btn.button('reset');
                        $('#CtrlTheGoods').modal('hide');
                        layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000,});
                        if (data.code == "0") {
                            btn.remove();
                            MtVm.search();
                        }
                    }
                };
                $(form).ajaxSubmit(options);
            }

        });
    }

    var submitChanges = function () {
        $('#submitChangesBtn').bind('click', function () {
            var btn = $("#mt_vm_channel_form:submit");
            var mtVm = $("#editChannelDetailDiv").find("input[name='vmId']");
            $.ajax({
                type: 'post',
                url: path + '/mtVmChannel/save',
                data: {"vmId": mtVm.val()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    btn.button('reset');
                    $('#CtrlTheGoods').modal('hide');
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000,});
                    if (data.code == "0") {
                        btn.remove();
                        MtVm.search();
                    }
                }
            });

        });
    }

    var submitRrelationSku = function () {
        $('#relationSku').bind('click', function () {
            var checks = $('#editChannelDetailDiv li.on input.channelCode');
            if (checks.length == 0) {
                layer.msg("请选择货道", {shade: [0.5, '#fff'], time: 1000});
                return false;
            } else {
                var status = true;
                $('#editChannelDetailDiv li.on').each(function () {
                    var reserve = $(this).find('.skuReserve').val();
                    if (reserve == 1) {
                        var statu = confirm("请确定该商品无未配送订单");
                        status = !!statu;
                        return false;
                    }
                });
                if (!status) {
                    return false;
                }
            }
        });
    }

    var submitEditCancelRelate = function () {
        $('#editCancelRelate').bind('click', function () {
            var url = $(this).attr("href");
            var checks = $('#editChannelDetailDiv li.on input.channelCode');
            if (checks.length == 0) {
                layer.msg("请选择货道", {shade: [0.5, '#fff'], time: 1000});
                return false;
            } else {
                var status = true;
                $('#editChannelDetailDiv li.on').each(function () {
                    var reserve = $(this).find('.skuReserve').val();
                    if (reserve == 1) {
                        var statu = confirm("请确定该商品无未配送订单");
                        status = !!statu;
                        return false;
                    }
                });
                if (status) {
                    var channelIds = "";
                    checks.each(function () {
                        if ('null' != $(this).val()) {
                            channelIds += $(this).val() + ",";
                        }
                    });

                    channelIds = channelIds.substring(0, channelIds.length - 1);
                    var mtVm = $("#editChannelDetailDiv").find("input[name='vmId']");
                    $.ajax({
                        type: 'post',
                        url: url,
                        data: {"channelIds": channelIds, "vmId": mtVm.val()},
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            if (data.type == "success") {
                                $("#editChannelDetailDiv").html(data.html);
                                Metronic.imgEmpty('editChannelDetailDiv');
                            }
                        }
                    });
                }
            }
        });
    }



    /*var submitRrelationSku = function () {
        $('#relationSku').bind('click', function () {
            var checks = $('#editChannelDetailDiv li.on input.channelCode');
            if (checks.length == 0) {
                layer.msg("请选择货道", {shade: [0.5, '#fff'], time: 1000});
                return false;
            } else {
                //var status = true;
                $('#editChannelDetailDiv li.on').each(function () {
                    var reserve = $(this).find('.skuReserve').val();
                    if (reserve == 1) {
                        /!*!//var statu = confirm("请确定该商品无未配送订单");
                        layer.confirm('请确定该商品无未配送订单？', {
                            btn: ['确定','取消']
                        }, function(){
                            console.log('确定');
                            status = !!statu;
                            return false;
                        }, function(){
                            //layer.msg("请选择货道", {shade: [0.5, '#fff'], time: 1000});
                        });*!/
                        layer.alert('请确定该商品无未配送订单？');
                    }
                });
            }
        });
    }

    var submitEditCancelRelate = function () {
        $('#editCancelRelate').bind('click', function () {
            var url = $(this).attr("href");
            var checks = $('#editChannelDetailDiv li.on input.channelCode');
            if (checks.length == 0) {
                layer.msg("请选择货道", {shade: [0.5, '#fff'], time: 1000});
                return false;
            } else {
                var status = true;
                $('#editChannelDetailDiv li.on').each(function () {
                    var reserve = $(this).find('.skuReserve').val();
                    if (reserve == 1) {
                        //var statu = confirm("请确定该商品无未配送订单");
                        layer.alert('请确定该商品无未配送订单？');
                    }
                });
                if (status) {
                    var channelIds = "";
                    checks.each(function () {
                        if ('null' != $(this).val()) {
                            channelIds += $(this).val() + ",";
                        }
                    });

                    channelIds = channelIds.substring(0, channelIds.length - 1);
                    var mtVm = $("#editChannelDetailDiv").find("input[name='vmId']");
                    $.ajax({
                        type: 'post',
                        url: url,
                        data: {"channelIds": channelIds, "vmId": mtVm.val()},
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            if (data.type == "success") {
                                $("#editChannelDetailDiv").html(data.html);
                                Metronic.imgEmpty('editChannelDetailDiv');
                            }
                        }
                    });
                }
            }
        });
    }*/



    var submitEditCancelMerge = function () {
        $("#editCancelMerge").bind('click', function () {
            var url = $(this).attr('href');
            var checks = $('#editChannelDetailDiv li.on input.channelCode');
            if (checks.length != 1) {
                layer.msg("请选择一个货道", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }
            var mtVm = $("#editChannelDetailDiv").find("input[name='vmId']");
            if (checks.length == 1) {
                $.ajax({
                    type: 'post',
                    url: url,
                    data: {"channelId": checks.val(), "vmId": mtVm.val()},
                    async: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.type == "success") {
                            $('#editChannelDetailDiv').html(data.html);
                            Metronic.imgEmpty('editChannelDetailDiv');
                        } else {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 1000,});
                        }
                    }
                });

            }
        });
    }

    var submitEditMergeChannel = function () {
        $('#editMergeButton').bind('click', function () {
            var url = $(this).attr("href");
            var checks = $('#editChannelDetailDiv li.on input.channelCode');
            if (checks.length == 0) {
                layer.msg("请选择两个货道", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }
            if (checks.length == 1) {
                layer.msg("请选择两个货道", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }
            if (checks.length > 2) {
                layer.msg("请选择两个货道", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }
            if (checks.length == 2) {
                var rols = "";
                var indexs = "";
                var channelIds = "";
                checks.each(function () {
                    rols += $(this).attr('rol') + ",";
                    indexs += $(this).attr('index') + ",";
                    channelIds += $(this).val() + ",";
                });
                channelIds = channelIds.substring(0, channelIds.length - 1);
                if (rols.split(",")[0] != rols.split(",")[1]) {
                    layer.msg("请选择同一行货道进行合并", {shade: [0.5, '#fff'], time: 1000,});
                    return;
                }
                if (indexs.split(",")[1] - indexs.split(",")[0] != 1) {
                    layer.msg("请选择相邻货道进行合并", {shade: [0.5, '#fff'], time: 1000,});
                    return;
                }
                var firstChannelId = channelIds.split(",")[0];
                var secondChannelId = channelIds.split(",")[1];
                var mtVm = $("#editChannelDetailDiv").find("input[name='vmId']");
                $.ajax({
                    type: 'post',
                    url: url,
                    data: {"firstChannelId": firstChannelId, "secondChannelId": secondChannelId, "vmId": mtVm.val()},
                    async: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.type == "success") {
                            $('#editChannelDetailDiv').html(data.html);
                            Metronic.imgEmpty('editChannelDetailDiv');
                        } else {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 1000,});
                        }
                    }
                });
            }
        });
    }
    var submitChangeSort = function () {
        $('#changeSortBtn').bind('click', function () {
            var url = $(this).attr("href");
            var checks = $('#showSortDiv li.on input');
            if (checks.length != 2) {
                layer.msg("请选择两个列表页", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }

            var sortIds = "";
            checks.each(function () {
                sortIds += $(this).val() + ",";
            });
            sortIds = sortIds.substring(0, sortIds.length - 1);
            var firstSortId = sortIds.split(",")[0];
            var secondSortId = sortIds.split(",")[1];
            var mtVm = $("#showSortDiv").find("input[name='vmId']");
            $.ajax({
                type: 'post',
                url: url,
                data: {"firstNumber": firstSortId, "secondNumber": secondSortId, "vmId": mtVm.val()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 500,});
                    if (data.type == "success") {
                        $("#showSortDiv").html(data.html);
                    }
                }
            });

        });
    }
    var submitCopySort = function () {
        $("#copySortBtn").bind("click", function () {
            var url = $(this).attr("href");
            var checks = $('#showSortDiv li.on input');
            if (checks.length != 1) {
                layer.msg("请选择一个货架", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }
            var mtVm = $("#showSortDiv").find("input[name='vmId']");
            var sortIds = "";
            checks.each(function () {
                sortIds += $(this).val() + ",";
            });
            sortIds = sortIds.substring(0, sortIds.length - 1);
            var sortNumber = sortIds.split(",")[0];
            $.ajax({
                type: 'post',
                url: url,
                data: {"sortNumber": sortNumber, "vmId": mtVm.val()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 500,});
                    if (data.type == "success") {
                        $("#showSortDiv").html(data.html);
                    }
                }
            });

        });
    }

    var submitShelf = function () {
        $('#submitShelfBtn').bind('click', function () {
            var url = $(this).attr("href");
            var mtVm = $("#showSortDiv").find("input[name='vmId']");
            var btn = $("#submitShelfBtn");
            $.ajax({
                type: 'post',
                url: url,
                data: {"vmId": mtVm.val()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    btn.button('reset');
                    $('#CtrlTheGoods').modal('hide');
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000,});
                    if (data.type == "success") {
                        btn.remove();
                        MtVm.search();
                    }
                }
            });

        });
    }

    var submitDeleteSort = function () {
        $("#deleteSortBtn").bind("click", function () {
            var url = $(this).attr("href");
            var checks = $('#showSortDiv li.on input');
            if (checks.length != 1) {
                layer.msg("请选择一个货架", {shade: [0.5, '#fff'], time: 1000,});
                return;
            }
            var mtVm = $("#showSortDiv").find("input[name='vmId']");
            var sortIds = "";
            checks.each(function () {
                sortIds += $(this).val() + ",";
            });
            sortIds = sortIds.substring(0, sortIds.length - 1);
            var sortNumber = sortIds.split(",")[0];
            $.ajax({
                type: 'post',
                url: url,
                data: {"sortNumber": sortNumber, "vmId": mtVm.val()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    layer.msg(data.message, {shade: [0.5, '#fff'], time: 500,});
                    if (data.type == "success") {
                        $("#showSortDiv").html(data.html);
                    }
                }
            });

        });
    }
    var bindEditFullSku = function () {
        $(".submitEdit").bind('click', function () {
            var value = $("#editchannelForm input[name='fullval']").val();
            if (value == "") {
                layer.msg("请输入数字", {shade: [0.5, '#fff'], time: 1000,});
                return false;
            } else {
                if (isNaN(value)) {
                    layer.msg("请输入数字", {shade: [0.5, '#fff'], time: 1000,});
                    value = "";
                } else {
                    var r = /^([1-9]\d{0,1}|100)$/;　　//0-100的正整数
                    if (!r.test(value)) {
                        layer.msg("请填写1-100的正整数", {shade: [0.5, '#fff'], time: 1000,});
                        return false;
                    }
                    var channelId = $(".modal-body [name='channelId']").val();
                    var vmId = $(".modal-body [name='vmId']").val();
                    $.ajax({
                        type: 'post',
                        url: '/mtVmChannel/adjustCapacity',
                        data: {
                            "channelId": channelId,
                            "vmId": vmId,
                            "adjustNum": value
                        },
                        async: true,
                        dataType: 'json',
                        cache: false,
                        success: function (data) {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 1000,});
                            if (data.type == "success") {
                                getChannelDetail('editChannelDetailDiv');
                                $('#setFullSku').modal('hide');
                                $('#setFullSku').html("");
                            }
                        }
                    });
                }
                return true;
            }

        });
    }
    return {
        init: function () {
            bindSearch();
            submitMtVmChannelBySt();
        },
        searchSku: function () {
            bindSkuSearch();
            submitSelectSku();
        },
        editMergeChannel: function () {
            submitEditMergeChannel();
        },
        editCancelMerge: function () {
            submitEditCancelMerge();
        },
        editCancelRelate: function () {
            submitEditCancelRelate();
            submitRrelationSku();
        },
        changeSort: function () {
            submitChangeSort();
        },
        editSubmit: function () {
            submitChanges();
        },
        copySort: function () {
            submitCopySort();
        },
        deleteSort: function () {
            submitDeleteSort();
        },
        submitShelf: function () {
            submitShelf();
        },
        editFullSku: function () {
            bindEditFullSku();
        }
    }
}();