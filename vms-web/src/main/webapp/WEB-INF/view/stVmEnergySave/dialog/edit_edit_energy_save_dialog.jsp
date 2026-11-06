<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="/plus/datetimepicker/function.datetimepicker.js"></script>
<div class="modal-dialog">
<div class="modal-content">
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h4 class="modal-title">节能策略</h4>
</div>
<form action="/stVmEnergySave/addEditEnergySave" id="editEditEnergySave-form" method="POST">
<input name="energySaveId" type="hidden" value="${energySaveId }">
<div class="modal-body">
<div class="modal_commonbody mt10 clearfix">
<section class="ml15">
    <span class="pull-left text-right w80 mr10">节能类型:</span>
    <div class="form-group pull-left">
        <div class="labelradio_group">

            <c:if test="${energySave.strategy.vmModel.temperatureHandlerMode == 29}">
                <label class="label_radio"><input type="radio" name="addEnergySaveType" value="1" ${energySave.energySaveType == 1?'checked':'' }>制冷</label>
            </c:if>
            <c:if test="${energySave.strategy.vmModel.temperatureHandlerMode == 30}">
                <label class="label_radio"><input type="radio" name="addEnergySaveType" value="2" ${energySave.energySaveType == 2?'checked':'' }>制热</label>
            </c:if>
            <c:if test="${energySave.strategy.vmModel.temperatureHandlerMode == 31}">
                <label class="label_radio"><input type="radio" name="addEnergySaveType" value="1" ${energySave.energySaveType == 1?'checked':'' }>制冷</label>
                <label class="label_radio"><input type="radio" name="addEnergySaveType" value="2" ${energySave.energySaveType == 2?'checked':'' }>制热</label>
            </c:if>
            <c:if test="${energySave.strategy.vmModel.screenHave == 1}">
                <label class="label_radio"><input type="radio" name="addEnergySaveType" value="3" ${energySave.energySaveType == 3?'checked':'' }>关闭屏幕</label>
            </c:if>
            <div class="clearfix"></div>
        </div>
    </div>
    </section>
    <div class="closewindow">
    <section class="col-md-12" style="margin-bottom:15px;">
        <span class="pull-left text-right w80 mr10">温控说明:</span>
        <p style="color:#f30;">${energySave.strategy.vmModel.tempCtrlDesc}</p>
    </section>
    <div class="clearfix"></div>
    <section class="col-md-6">
    <span class="pull-left text-right w80 mr10">保温箱编号:</span>
    <div class="form-group">
    <select class="form-control input-sm w160" name="addTempBoxNo">
    <c:forEach begin="1" end="${energySave.strategy.vmModel.tempBoxCount}" varStatus="v">
        <option value="${v.index}" ${energySave.tempBoxNo == v.index?'selected':''}>${v.index}</option>
    </c:forEach>
    </select>
    </div>
    </section>
    <section class="col-md-6">
    <span class="pull-left text-right w80 mr10">温度目标值:</span>
    <div class="form-group">
    <input type="text" class="form-control input-sm w160 targetValue" value="${energySave.targetValue }" name="addTargetValue" placeholder="单位：摄氏度℃">
    </div>
    </section>
    <div class="clearfix"></div>
    </div>
    <section class="col-md-6">
    <span class="pull-left text-right w80 mr10">开始时间:</span>
    <div class="form-group">
    <input class="form-control input-sm form_time w160" name="addStartTime" value="${energySave.startTime }" readonly type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii">
    </div>
    </section>
    <section class="col-md-6">
    <span class="pull-left text-right w80 mr10">结束时间:</span>
    <div class="form-group">
    <input class="form-control input-sm form_time w160" name="addEndTime" value="${energySave.endTime }" readonly type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii">
    </div>
    </section>
    <div class="clearfix"></div>
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
    $(function(){

        //处理节能策略-节能类型切换
        doWindow('editEditEnergySave-form');
        StVmEnergySave.editEditEnergySave();
        //设置常温和屏幕
        $('#editEditEnergySave-form .label_radio').click(function(){
            var energySaveType = $(this).find('input[name="addEnergySaveType"]:checked').val();
            if(energySaveType == 3 || energySaveType == '3'){
                $('#editEditEnergySave-form .closewindow').hide();
            }else{
                $('#editEditEnergySave-form .closewindow').show();
            }
        });
    });
    afterWindow();
    function afterWindow() {
        var energyType = $('#editEditEnergySave-form .labelradio_group input[type="radio"]:checked').val();
        if (energyType == 3) {
            $('#editEditEnergySave-form .closewindow').hide();
        } else {
            $('#editEditEnergySave-form .closewindow').show();
        }
    }
    </script>