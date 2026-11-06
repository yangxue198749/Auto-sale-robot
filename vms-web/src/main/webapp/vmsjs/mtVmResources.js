var MtVmResources = function() {
    var searchEvent = function() {
        var options = {
            type: 'POST',
            beforeSubmit: function() {
            },
            success: function(data) {
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
    var addMtVmResourcesEvent = function () {
        $("#add-mt-vm-resources-form").validate({
            submitHandler:function(form){
                //form.submit();
                var themeName = $("#addMtVmResourcesName").val();
                var zipFile = $("#mtVmResourcesZip").val();
                if(themeName == ""){
                    layer.msg("请输入皮肤名称",{shade: [0.5, '#fff'],time:2000,});
                    $("#addMtVmResourcesName").focus();
                    return false;
                } else if(zipFile == ""){
                    layer.msg("请选择要上传的皮肤包",{shade: [0.5, '#fff'],time:2000,});
                    return false;
                }  else {
                    var type = zipFile.substring(zipFile.lastIndexOf(".") + 1);
                    if(type.toLowerCase() != "zip") {
                        layer.msg("皮肤包请选择zip文件！",{shade: [0.5, '#fff'],time:2000,});
                        return false;
                    }  else {
                        var options = {
                            type : 'POST',
                            beforeSubmit : function() {},
                            success : function(data) {
                                $('#addMtVmResourcesDiv').modal('hide') ;
                                if(data.code == "0"){
                                    layer.msg("新建成功！",{shade: [0.5, '#fff'],time:2000,});
                                    searchEvent();
                                } else {
                                    layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                                }
                            }
                        };
                        $(form).ajaxSubmit(options);
                    }
                }
            }
        });
        //激活tooltip
        $(function () { $("[data-toggle='tooltip']").tooltip(); });
    }
    return {
        init: function() {
            bindSearch();
            reset();
        },
        addMtVmResources:function(){
            addMtVmResourcesEvent();
        }
    }
}();
