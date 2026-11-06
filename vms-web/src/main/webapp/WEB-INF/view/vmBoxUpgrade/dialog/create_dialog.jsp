<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">升级</h4>
        </div>
        <form class="unKeyCode">
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">vmBox版本:</span>
                        <button type="button" class="btn btn-default btn-lg2 btn-block f12 w220 pull-left" data-toggle="modal" data-target="#withProgram" href="/vmBoxUpgrade/selectVersionDialog?type=1">选择vmBox版本</button>
                        <div class="clearfix"></div>
                        <!--这里是选择关联程序后获得的版本号信息 填写如下：-->
                        <div class="lh20 pl90 mb15">
                            <span>版本号: <b id="vmboxVersionNo"></b></span><!--初始状态下，这行代码清除<span>...</span>，父级div容器保留-->
                            <input type="hidden" id="vmboxVersionId">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">通信板版本:</span>
                        <button type="button" class="btn btn-default btn-lg2 btn-block f12 w220 pull-left" data-toggle="modal" data-target="#withProgram" href="/vmBoxUpgrade/selectVersionDialog?type=2">选择通信板版本</button>
                        <div class="clearfix"></div>
                        <!--这里是选择关联程序后获得的版本号信息 填写如下：-->
                        <div class="lh20 pl90 mb15">
                            <span>版本号: <b id="comminocationNo"></b></span><!--初始状态下，这行代码清除<span>...</span>，父级div容器保留-->
                            <input type="hidden" id="comminocationId">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">出货板版本:</span>
                        <button type="button" class="btn btn-default btn-lg2 btn-block f12 w220 pull-left" data-toggle="modal" data-target="#withProgram" href="/vmBoxUpgrade/selectVersionDialog?type=3">选择出货板版本</button>
                        <div class="clearfix"></div>
                        <!--这里是选择关联程序后获得的版本号信息 填写如下：-->
                        <div class="lh20 pl90 mb15">
                            <span>版本号: <b id="outVersionNo"></b></span><!--初始状态下，这行代码清除<span>...</span>，父级div容器保留-->
                            <input type="hidden" id="outVersionId">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">选择区域:</span>
                        <div class="form-group w220 pull-left">
                            <select class="form-control input-sm w70 pull-left mr5" name="province" id="provinceSelect" onchange="refreshCitySelect()"></select>
                            <select class="form-control input-sm w70 pull-left mr5" name="city" id="citySelect" onchange="refreshCountrySelect()"></select>
                            <select class="form-control input-sm w70 pull-left" name="area" id="countrySelect"></select>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">选择型号:</span>
                        <button type="button" class="btn btn-default btn-lg2 btn-block f12 w220 pull-left" data-toggle="modal" data-target="#chooseMat" href="/vmBoxUpgrade/selectVmModelDialog">选择售货机型号</button>
                        <div class="clearfix"></div>
                        <!--这里是选择关联程序后获得的版本号信息 填写如下：-->
                        <div class="lh20 pl90 mb15">
                            <span>售货机型号: <b id="vmModelName"></b></span><!--初始状态下，这行代码清除<span>...</span>，父级div容器保留-->
                            <input id="selectVmModelId" type="hidden">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">验证码:</span>
                        <div class="form-group w220 pull-left vercode_group">
                            <input class="code_input" id="createCode" type="text" placeholder="请输入输入验证码">
                            <div class="code_wp"><img src="" id="qrcode" class="qrcode" alt=""></div>
                        </div>
                    </section>
                </div>
                <!--基本信息 end-->
            </div>
            <div class="modal-footer">
                <button id="createBtn" type="button" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    var area = '${u:getAreaJson()}';
    // 将字符串转成JSON对象
    var areaJson = eval("(" + area + ")");
    $(function(){

        VmBoxUpgrade.create();
        getCode();
        // 页面加载完成后执行的方法
        renderProvince();

        var selectedProvinceId = $("#provinceSelect").val();

        renderCity(selectedProvinceId);

        var selectedCityeId = $("#citySelect").val();

        renderCountry(selectedCityeId);
        timeWidgetInit();
        $('#qrcode').click(function(){
            getCode();
        });
    })
    //验证码
    function getCode(){
        $("#qrcode").attr("src","/vmBoxUpgrade/createCode?rand=" +new Date().getMilliseconds());
    }
    function timeWidgetInit(){
        $('.timewidget').datetimepicker({
            language:  'zh-CN',
            format: 'hh:ii',
            weekStart: 1,
            todayBtn:  1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 1,
            minuteStep: 1,
            minView: 0,
            maxView: 1,
            pickerPosition: "top-left", //为了适配小屏幕
            forceParse: 0
        })
    }
</script>