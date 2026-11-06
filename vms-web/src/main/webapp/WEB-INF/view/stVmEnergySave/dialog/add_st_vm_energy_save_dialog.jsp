<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新增节能策略</h4>
        </div>
            <div class="modal-body">
                <div class="modal-body">
                    <div class="modal_commonbody mt10 clearfix">
                        <section class="col-md-12">
                            <span class="pull-left text-right w160 mr10">策略名:</span>
                            <div class="form-group pull-left">
                                <input type="text" class="form-control input-sm w240" id="addStVmName" name="addStVmName" placeholder="请输入策略名">
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span class="pull-left text-right w160 mr10">策略说明:</span>
                            <div class="form-group pull-left">
                                <input type="text" class="form-control input-sm w240" id="addStVmDesc" name="addStVmDesc" placeholder="请输入策略说明">
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span class="pull-left text-right w160 mr10">售货机型号:</span>
                            <div class="form-group pull-left">
                                <select class="form-control input-sm w240" id="addVmModelId" name="addVmModelId">
                                    <option value="">请选择</option>
                                    <c:forEach items="${vmModelList }" var="vmModel">
				                      <option value="${vmModel.id }" data-temperature-handler-mode="${u:getVmTemperatureHandlerMode(vmModel.id )}" data-screen-have="${u:getVmScreenHave(vmModel.id )}">${vmModel.modelName }
                                      </option>
				                    </c:forEach>
                                </select>
                            </div>
                        </section>
                    </div>

                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="submitAddStVmBtn"  class="btn btn-success btn-lg2"  data-toggle="modal" data-target="#addNextEnergy">下一步</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
   </div>
</div>
<script type="text/javascript">
    $(function(){
    	StVmEnergySave.nextBtn();
    })
</script>