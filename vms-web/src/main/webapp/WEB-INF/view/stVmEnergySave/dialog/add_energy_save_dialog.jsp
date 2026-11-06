<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="/plus/datetimepicker/function.datetimepicker.js"></script>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">节能策略</h4>
        </div>
        <form action="/stVmEnergySave/addEnergySave" id="addEnergySave-form" method="POST">
            <input name="strategyId" type="hidden" value="${strategyId }">
            <div class="modal-body">
                <div class="modal_commonbody mt10 clearfix">
                    <section class="ml15">
                        <span class="pull-left text-right w80 mr10">节能类型:</span>
                        <div class="form-group pull-left">
                            <div class="labelradio_group">
                                <c:if test="${mtVmModel.temperatureHandlerMode == 29}">
                                    <label class="label_radio"><input type="radio" name="addEnergySaveType" value="1" >制冷</label>
                                </c:if>
                                <c:if test="${mtVmModel.temperatureHandlerMode == 30}">
                                    <label class="label_radio"><input type="radio" name="addEnergySaveType" value="2" >制热</label>
                                </c:if>
                                <c:if test="${mtVmModel.temperatureHandlerMode == 31}">
                                    <label class="label_radio"><input type="radio" name="addEnergySaveType" value="1" >制冷</label>
                                    <label class="label_radio"><input type="radio" name="addEnergySaveType" value="2">制热</label>
                                </c:if>
                                <c:if test="${mtVmModel.screenHave == 1}">
                                    <label class="label_radio"><input type="radio" name="addEnergySaveType" value="3" >关闭屏幕</label>
                                </c:if>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                    </section>
                    <div class="closewindow">
                        <section class="col-md-12" style="margin-bottom:15px;">
                            <span class="pull-left text-right w80 mr10">温控说明:</span>
                            <p style="color:#f30;">${mtVmModel.tempCtrlDesc}</p>
                        </section>
                        <div class="clearfix"></div>
                        <section class="col-md-6">
                            <span class="pull-left text-right w80 mr10">保温箱编号:</span>
                            <div class="form-group">
                                <select class="form-control input-sm w160" name="addTempBoxNo">
                                    <c:forEach begin="1" end="${mtVmModel.tempBoxCount}" varStatus="v">
                                        <option value="${v.index}">${v.index}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </section>
                        <section class="col-md-6">
                            <span class="pull-left text-right w80 mr10">温度目标值:</span>
                            <div class="form-group">
                                <input type="text" class="form-control input-sm w160 targetValue" name="addTargetValue" placeholder="单位：摄氏度℃">
                            </div>
                        </section>
                    </div>
                    <section class="col-md-6">
                        <span class="pull-left text-right w80 mr10">开始时间:</span>
                        <div class="form-group">
                            <input class="form-control input-sm form_time w160" name="addStartTime" readonly type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left text-right w80 mr10">结束时间:</span>
                        <div class="form-group">
                            <input class="form-control input-sm form_time w160" name="addEndTime" readonly type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii">
                        </div>
                    </section>
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-success">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        //处理节能策略-节能类型切换
        StVmEnergySave.addEnergySave();
        doWindow('addEnergySave-form');
        //设置常温和屏幕
        $('#addEnergySave-form .label_radio').click(function(){
            var energySaveType = $(this).find('input[name="addEnergySaveType"]:checked').val();
            if(energySaveType == 3 || energySaveType == '3'){
                $('#addEnergySave-form .closewindow').hide();
            }else{
                $('#addEnergySave-form .closewindow').show();
            }
        });
    });
</script>