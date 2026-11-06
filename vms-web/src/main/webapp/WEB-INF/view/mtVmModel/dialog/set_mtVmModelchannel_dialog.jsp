<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form action="" id="modelTable_form">
	<input name="channelCode" value="${channelCode}" type="hidden" >
	<input name="vmModelId" value="${vmModelId}" type="hidden" >
	<div class="modal-dialog w400">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title">设置货道属性</h4>
	        </div>
	        <div class="modal-body">
	            <div class="modal_commonbody clearfix">
	                <section class="col-md-12 clearfix">
	                    <span class="w80 pull-left text-right mr10" >标准宽度:</span>
	                    <div class="form-group pull-left">
	                    	<c:choose>
								<c:when test="${modelChannel == null}">
									<input name="channelWidth" type="text" class="form-control input-sm" placeholder="请输入标准宽度" 
										required data-msg-required="请输入标准宽度" digits="true" data-msg-digits="只能输入整数" >
								</c:when>
								<c:otherwise>
									<input name="channelWidth" type="text" class="form-control input-sm" placeholder="请输入标准宽度" value="${modelChannel.channelWidth}" 
										required data-msg-required="请输入标准宽度" digits="true" data-msg-digits="只能输入整数" >
								</c:otherwise>
							</c:choose>
	                    </div>
	                </section>
	                <section class="col-md-12 clearfix">
	                    <span class="w80 pull-left text-right mr10">最大宽度:</span>
	                    <div class="form-group pull-left">
	                    	<c:choose>
								<c:when test="${modelChannel == null}">
									<input name="acceptWidthMax" type="text" class="form-control input-sm" placeholder="请输入最大宽度" 
										required data-msg-required="请输入最大宽度" digits="true" data-msg-digits="只能输入整数">
								</c:when>
								<c:otherwise>
									<input name="acceptWidthMax" type="text" class="form-control input-sm" placeholder="请输入最大宽度" value="${modelChannel.acceptWidthMax}" 
										required data-msg-required="请输入最大宽度" digits="true" data-msg-digits="只能输入整数">
								</c:otherwise>
							</c:choose>
	                    </div>
	                </section>
	                <section class="col-md-12 clearfix">
	                    <span class="w80 pull-left text-right mr10" for="isFixedCapacity">是否固定容量:</span>
	                    <div class="form-group" >
	                        <select class="form-control w160 input-sm" name="isFixedCapacity" id="isFixedCapacity" required data-msg-required="请选择">
	                        	<option value="">请选择</option>
	                        	<c:choose>
									<c:when test="${empty modelChannel.isFixedCapacity }">
										<option value="1" >是</option>
										<option value="0" >否</option>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${modelChannel.isFixedCapacity == 1}">
												<option value="1" selected="selected">是</option>
												<option value="0" >否</option>
											</c:when>
											<c:when test="${modelChannel.isFixedCapacity == 0}">
												<option value="1">是</option>
												<option value="0" selected="selected">否</option>
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
	                        </select>
	                    </div>
	                </section>
	                <section class="col-md-12 clearfix">
	                    <span class="w80 pull-left text-right mr10">固定容量数:</span>
	                    <div class="form-group pull-left">
	                        <input name="fixedCapacity" id="fixedCapacity" type="text" class="form-control input-sm" placeholder="请输入固定容量数" value="${modelChannel.fixedCapacity}"
	                        	<c:if test="${modelChannel.isFixedCapacity == 0}"> disabled='disabled' </c:if>
	                        	<c:if test="${modelChannel.isFixedCapacity == 1}">required='required' digits='true' data-msg-digits='只能输入整数' </c:if>
	                        >
	                    </div>
	                </section>
	            </div>
	        </div>
	        <div class="modal-footer">
	            <button type="submit" class="btn btn-success">确定</button>
	            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	        </div>
	    </div>
	</div>
</form>

<script>

	$('#isFixedCapacity').change(function(){
		isFixedCapacity(this.value);
	})
	
	// 页面加载完成后执行的方法
	$(function(){
       	$("#modelTable_form").validate({
	        submitHandler:function(form){
	        	setModelChannel();
	        }    
	    });
	});
	

</script>


