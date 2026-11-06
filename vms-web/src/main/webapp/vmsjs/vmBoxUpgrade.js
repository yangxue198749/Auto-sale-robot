var VmBoxUpgrade = function() {
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
        });
    }
    var bindSearch = function() {
        $("#searchButton").bind("click", function() {
            searchEvent();
        });
    }
    var submitSelectVmModel = function () {
        $("#selectVmModelBtn").bind("click",function () {
            var model = $("#selectVmModelDiv").find("input[name='vmModel']:checked");
            if(null == model || model.length == 0){
                layer.msg('请选择售货机型号',{shade: [0.5, '#fff'],time:1000});
                return false;
            }
            $("#selectVmModelId").val(model.val());
            $("#vmModelName").html(model.attr("modelName"));
            $('#chooseMat').modal('hide') ;
        })
    }
    var submitSelectVersion = function () {
        $("#selectVersionBtn").bind("click",function () {
            var version = $("#selectVersionDiv").find("input[name='version']:checked");
            if(null == version || version.length == 0){
                layer.msg('请选择版本',{shade: [0.5, '#fff'],time:1000});
                return false;
            }
            var versionType = $("#selectVersionType").val();
            if(1 == versionType){
                $("#vmboxVersionId").val(version.val());
                $("#vmboxVersionNo").html(version.attr("versionNo"));
                $('#withProgram').modal('hide') ;
            }else if(2 == versionType){
                $("#comminocationId").val(version.val());
                $("#comminocationNo").html(version.attr("versionNo"));
                $('#withProgram').modal('hide') ;
            }else if(3 == versionType){
                $("#outVersionId").val(version.val());
                $("#outVersionNo").html(version.attr("versionNo"));
                $('#withProgram').modal('hide') ;
            }
        })
    }
    var submitCreate = function () {
        $("#createBtn").bind("click",function () {
            var vmboxVersionId = $("#vmboxVersionId").val();
            var comminocationId = $("#comminocationId").val();
            var outVersionId = $("#outVersionId").val();

            if((null == vmboxVersionId || "" == vmboxVersionId) && (null == comminocationId || "" == comminocationId) && (null == outVersionId || "" == outVersionId )){
                layer.msg('请最少选择一个版本',{shade: [0.5, '#fff'],time:1000});
                return false;
            }
            var provinceSelect = $('#provinceSelect option:selected') .val();
            var citySelect = $('#citySelect option:selected') .val();
            var countrySelect = $('#countrySelect option:selected') .val();
            if(null == provinceSelect ||  "" == provinceSelect){
                layer.msg('请选择区域',{shade: [0.5, '#fff'],time:1000});
                return false;
            }
            var selectVmModelId = $("#selectVmModelId").val();
            if(null == selectVmModelId || "" ==  selectVmModelId){
                layer.msg('请选择售货机型号',{shade: [0.5, '#fff'],time:1000});
                return false;
            }
            var createCode = $("#createCode").val();
            if(null == createCode || "" == createCode){
                layer.msg('请输入验证码',{shade: [0.5, '#fff'],time:1000});
                return false;
            }
            var areaId = provinceSelect;
            if( null != citySelect && "" != citySelect){
                areaId = citySelect;
            }
            if(null != countrySelect && "" != countrySelect){
                areaId = countrySelect;
            }

            $.ajax({
                type : 'post',
                url : "/vmBoxUpgrade/submitCreate",
                data : {"areaId":areaId,"validCode":createCode,"vmModelId":selectVmModelId,"vmBoxPackageId":vmboxVersionId,
                    "comminocationBoardPackageId":comminocationId,"outBoardPackageId":outVersionId},
                async : true,
                dataType : 'json',
                cache : false,
                success : function(data) {
                    layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
                    if(data.type=="success"){
                        $("#updateModal").modal("hide");
                        searchEvent();
                    }
                }
            });


        })
    }
    return {
        init: function() {
            bindSearch();
            reset();
        },
        selectVmModel:function () {
            submitSelectVmModel();
        },
        selectVersion:function () {
            submitSelectVersion();
        },
        create:function () {
            submitCreate();
        }
    }
}();
