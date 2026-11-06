<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">新建售货机</h4>
        </div>
        <form action="/mtVm/addMtVm" id="add-mtvm-form" method="POST">
        <input name="copyMtVmId" type="hidden">
        <input name="nodeId" type="hidden">
        <input name="vmModelId" type="hidden">
        <div class="modal-body">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix">
                <!--填写基本信息-->
                <div class="col_info clearfix">
                    <section class="col-md-12">
                        <span class="text-right mr10">售货机型号:<span class="vmModelName"></span></span>
                        <div class="form-group mb5">
                            <button type="button" class="btn btn-default btn-sm2 f12 btn-block" data-toggle="modal" href="/mtVm/selectVmModelDialog" data-target="#selectVmModel">选择型号</button>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span>所属点位:<span class="mr10 nodeName"></span></span>
                        <div class="form-group mb5">
                        	<button type="button" class="btn btn-default btn-sm2 f12 btn-block" href="/mtVm/selectNodeDialog" data-toggle="modal" data-target="#Child_choiceLocation">选择点位</button>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span>复制策略:<span class="copyInnerCode"></span></span>
                        <div class="form-group mb5">
                        	<button type="button" class="btn btn-default btn-sm2 f12 btn-block" href="/mtVm/selectCopyMtVmDialog" data-toggle="modal" data-target="#Child_copyMatStrategy">复制设备策略</button>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span>具体位置:</span>
                        <div class="form-group mb5"><textarea class="form-control input-sm" name="vmDesc" type="text" placeholder="请输入具体位置"></textarea></div>
                    </section>
                    <!-- v3.4 打印机设置 start-->
                    <section class="col-md-12" id="DeviceSwitch">
                        <div class="text-right"><span class="cf_green pointer switchbtn">更多选项</span></div>
                        <div class="form-group mb5 devicegroup hide">
                            <p>打印设置：</p>
                            <label class="input-radio style2 mr10">
                                <input type="radio" name="printer" value="0" checked><span>无打印机</span>
                            </label>
                            <label class="input-radio style2">
                                <input type="radio" name="printer" value="1"><span>有打印机</span>
                            </label>
                        </div>
                        <div class="form-group mb5 devicegroup hide">
                            <p>售卖设置：</p>
                            <select class="form-control input-sm" name="reserve">
                                <option value="0">即售机</option>
                                <option value="1">预定机</option>
                            </select>
                            <p>支付方式：</p>
                            <label class="input-radio style2" style="width: 90px;"><input type="checkbox" class="input_v4" name="payType" value="1201" checked="checked"><span>支付宝</span></label>
                            <label class="input-radio style2" style="width: 90px;"><input type="checkbox" class="input_v4" name="payType" value="1101" checked="checked"><span>微信</span></label>
                            <label class="input-radio style2" style="width: 90px;"><input type="checkbox" class="input_v4" name="payType" value="1301" ><span>银联扫码</span></label>
                            <label class="input-radio style2" style="width: 90px;"><input type="checkbox" class="input_v4" name="payType" value="2000" ><span>卡支付</span></label>
                        </div>
                    </section>
                    <!-- v3.4 打印机设置 end -->

                </div>
            </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" id="submitAddMtVmBtn" class="btn btn-success btn-lg2">确定</button>
            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
        </div>
        </form>
    </div>
</div>
<script type="text/javascript">
$(function(){
	MtVm.addMtVm();
	//打印设置
	$('#DeviceSwitch .switchbtn').click(function(){
	    $('#DeviceSwitch .devicegroup').toggleClass('hide');
    })
})
</script>