<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script src="/plus/datetimepicker/function.datetimepicker.js"></script>
<div class="modal-dialog">
    <div class="ValidateRrrorBox"></div>
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">修改节能策略</h4>
        </div>
        <form method="POST" action="${path }/mtVmEnergySave/save" id="editTheEnergyForm">
        <input name="vmId" value="${vmId }" type="hidden">
        <input name="energySaveId" value="${mtVmEnergySave.energySaveId }" type="hidden">
        <div class="modal-body">
            <div class="modal_commonbody clearfix mt20">
                <!--填写基本信息-->
                <div class="step_info">
                    <section class="ml15">
                        <span class="pull-left text-right w80 mr10">节能类型:</span>
                        <div class="form-group">
                            <div class="labelradio_group">

                                <c:if test="${mtVmModel.temperatureHandlerMode == 29}">
                                    <label class="label_radio"><input type="radio" name="energySaveType" value="1" ${mtVmEnergySave.energySaveType == 1?'checked':'' }>制冷</label>
                                </c:if>
                                <c:if test="${mtVmModel.temperatureHandlerMode == 30}">
                                    <label class="label_radio"><input type="radio" name="energySaveType" value="2" ${mtVmEnergySave.energySaveType == 2?'checked':'' }>制热</label>
                                </c:if>
                                <c:if test="${mtVmModel.temperatureHandlerMode == 31}">
                                    <label class="label_radio"><input type="radio" name="energySaveType" value="1" ${mtVmEnergySave.energySaveType == 1?'checked':'' }>制冷</label>
                                    <label class="label_radio"><input type="radio" name="energySaveType" value="2" ${mtVmEnergySave.energySaveType == 2?'checked':'' }>制热</label>
                                </c:if>
                                <c:if test="${mtVmModel.screenHave == 1}">
                                    <label class="label_radio"><input type="radio" name="energySaveType" value="3" ${mtVmEnergySave.energySaveType == 3?'checked':'' }>关闭屏幕</label>
                                </c:if>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                    </section>
                    <div class="closewindow">
                        <section class="col-md-12">
                            <span class="pull-left text-right w80 mr10">温控说明:</span>
                            <p style="color:#f30;">${mtVmModel.tempCtrlDesc}</p>
                        </section>
                        <div class="clearfix"></div>
                        <section class="col-md-6">
                            <span class="pull-left text-right w80 mr10">保温箱编号:</span>
                            <div class="form-group">
                                <select class="form-control input-sm w160" name="tempBoxNo">
                                    <c:forEach begin="1" end="${mtVmModel.tempBoxCount}" varStatus="v">
                                        <option value="${v.index}" ${mtVmEnergySave.tempBoxNo == v.index?'selected':''}>${v.index}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left text-right w80 mr10" for="TheEnergyTemp">温度目标值:</span>
                            <div class="form-group"><input class="form-control input-sm w160 targetValue" name="targetValue" value="${mtVmEnergySave.targetValue }" type="text" placeholder="单位：摄氏度℃" digits="true"></div>
                        </section>
                        <div class="clearfix"></div>
                    </div>
                    <section class="col-md-6">
                        <span class="pull-left text-right w80 mr10" for="TheEnergyTimeStart">开始时间:</span>
                        <div class="form-group"><input class="form-control input-sm form_time w160" value="${mtVmEnergySave.startTime }" readonly type="text" placeholder="开始时间" name="startTime" required data-msg-required="请选择开始时间" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left text-right w80 mr10" for="TheEnergyTimeEnd">结束时间:</span>
                        <div class="form-group"><input class="form-control input-sm form_time w160" value="${mtVmEnergySave.endTime }" readonly type="text" placeholder="结束时间" name="endTime" required data-msg-required="请选择结束时间" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                    </section>
                </div>
            </div>

        </div>
        <div class="modal-footer">
            <button type="submit" id="submitEditEnergyBtn" class="btn btn-success btn-lg2">完成</button>
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
        </div>
        </form>
    </div>
</div>
<script>
	$(function(){
		MtVmEnergySave.edit();
        //处理节能策略-节能类型切换
        doWindow('editTheEnergyForm');

        //设置常温和屏幕
        $('#editTheEnergyForm .label_radio').click(function(){
            var energySaveType = $(this).find('input[name="energySaveType"]:checked').val();
            if(energySaveType == 3 || energySaveType == '3'){
                $('#editTheEnergyForm .closewindow').hide();
            }else{
                $('#editTheEnergyForm .closewindow').show();
            }
        });
        afterWindow();
        function afterWindow() {
            var energyType = $('#editTheEnergyForm .labelradio_group input[type="radio"]:checked').val();
            if (energyType == 3) {
                $('#editTheEnergyForm .closewindow').hide();
            } else {
                $('#editTheEnergyForm .closewindow').show();
            }
        }
	});
</script>

