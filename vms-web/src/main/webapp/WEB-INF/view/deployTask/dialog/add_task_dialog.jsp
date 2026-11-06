<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script src="${path}/plus/datepicker/js/function.datepicker.js"></script>
<div class="modal-dialog w600">
      <div class="modal-content">
          <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
              <h4 class="modal-title f16" id="">新增投放工单</h4>
          </div>
          <form action="/deployTask/addTask" method="POST" id="add-task-form" class="unKeyCode">
          <input type="hidden" name="vmId" id="addTaskVmId">
          <input type="hidden" name="addNodeId" id="addNodeId">
          <div class="modal-body">
              <!--基本信息 start-->
              <div class="modal_commonbody clearfix">
                  <!--填写基本信息-->
                  <div class="col_info clearfix">
                      <section class="col-md-6">
                          <span>售货机编号:<span class="ml10" id="innerCodeP"></span></span>
                          <div class="form-group">
                              <button type="button" class="btn btn-default btn-sm2 f12 btn-block" href="/deployTask/mtVmList" data-toggle="modal" data-target="#selectMtVm">选择售货机</button>
                         </div>
                      </section>
                      <section class="col-md-6">
                          <span>点位:<span class="ml10" id="addNodeName"></span></span>
                          <div class="form-group">
          					<button type="button" class="btn btn-default btn-sm2 f12 btn-block" href="/deployTask/selectMtNodeDialog" data-toggle="modal" data-target="#choiceLocation">选择点位</button>
                          </div>
                      </section>
                      <section class="col-md-6">
                          <span>任务负责人:</span>
                          <div class="form-group">
                              <select class="form-control input-sm" id="ownerId" name="ownerId">
                               <option>请选择</option>
                              </select>
                          </div>
                      </section>
                      <section class="col-md-6">
                          <span>投放日期:</span>
                          <div class="form-group"><input class="form-control input-sm DateStartTody" name="taskTime" type="text" placeholder="选择投放日期"></div>
                      </section>
                  </div>
              </div>
          </div>
          <!--按钮-->
          <div class="modal-footer">
              <button type="submit" class="btn btn-success btn-lg2">确定</button>
              <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
          </div>
          </form>
          
      </div>
  </div>
 <script type="text/javascript">
$(function(){
	DeployTask.addTask();
})
</script>