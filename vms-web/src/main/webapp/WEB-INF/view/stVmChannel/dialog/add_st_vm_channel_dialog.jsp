<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">新增货道策略</h4>
        </div>
       <form action="" name="" class="unKeyCode">
        <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody clearfix">
                    <div class="col_info clearfix">
                        <section class="col-md-12">
                            <span class="w205">策略名称:</span>
                            <div class="form-group">
                            	<input class="form-control input-sm" id="addStVmName" name="addStVmName" type="text" placeholder="请输入策略名称">
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span>售货机型号:</span>
                            <div class="form-group">
                                <select class="form-control input-sm" id="addVmModelId" name="addVmModelId">
                                  <option value="">请选择</option>
                                  <c:forEach items="${vmModelList }" var="vmModel">
			                      	<option value="${vmModel.id }" >${vmModel.modelName }</option>
			                      </c:forEach>
                                </select>
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span class="w205">策略说明:</span>
                            <div class="form-group">
                            	<textarea class="form-control input-sm" id="addStVmDesc" name="addStVmDesc" required type="text" placeholder="请输入策略说明"></textarea>
                            </div>
                        </section>
                    </div>
                </div>
                <!--基本信息 end-->
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2 nextbtn" id="addNextButton" data-href="/stVmChannel/addStVmChannelDetailDialog" data-target="#setTheGoods_02" data-toggle="modal">下一步</button>
            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
        </div>
       </form>
                    
    </div>
</div>
<script type="text/javascript">
    $(function(){
    	StVmChannel.addStVmNext();
        $("[data-toggle='tooltip']").tooltip();
    })
</script>