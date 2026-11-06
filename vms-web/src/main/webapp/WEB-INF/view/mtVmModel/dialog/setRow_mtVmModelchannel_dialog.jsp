<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form action="" id="modelTableRow_form" >
	<input name="channelCode" value="${channelCode}" type="hidden" >
	<input name="vmModelId" value="${vmModelId}" type="hidden" >
	<div class="modal-dialog w400">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title">设置货道属性</h4>
			</div>
			<div class="modal-body">
				<div class="modal_commonbody clearfix">
					<section class="col-md-12 clearfix">
						<span class="w80 pull-left text-right mr10">深度:</span>
						<div class="form-group pull-left">
							<c:choose>
								<c:when test="${modelChannel == null}">
									<input name="channelLength" type="text" class="form-control input-sm" placeholder="请输入深度"  
										required data-msg-required="请输入深度" digits="true" data-msg-digits="只能输入整数">
								</c:when>
								<c:otherwise>
									<input name="channelLength" type="text" class="form-control input-sm" placeholder="请输入深度" value="${modelChannel.channelLength}" 
										required data-msg-required="请输入深度" digits="true" data-msg-digits="只能输入整数">
								</c:otherwise>
							</c:choose>
						</div>
					</section>
					<section class="col-md-12 clearfix">
						<span class="w80 pull-left text-right mr10">高度:</span>
						<div class="form-group pull-left">
							<c:choose>
								<c:when test="${modelChannel == null}">
									<input name="channelHeight" type="text" class="form-control input-sm" placeholder="请输入高度" 
										required data-msg-required="请输入高度" digits="true" data-msg-digits="只能输入整数">
								</c:when>
								<c:otherwise>
									<input name="channelHeight" type="text" class="form-control input-sm" placeholder="请输入高度" value="${modelChannel.channelHeight}" 
										required data-msg-required="请输入高度" digits="true" data-msg-digits="只能输入整数">
								</c:otherwise>
							</c:choose>
						</div>
					</section>
					<section class="col-md-12 clearfix">
						<span class="w80 pull-left text-right mr10" for="displayMode">摆放方式:</span>
						<div class="form-group">
							<select class="form-control w160 input-sm" name="displayMode" required data-msg-required="请选择">
								<option value="">请选择</option>
								<c:choose>
									<c:when test="${empty modelChannel.displayMode}">
										<option value="1" >横</option>
										<option value="0" >竖</option>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${modelChannel.displayMode == 1}">
												<option value="1" selected="selected">横</option>
												<option value="0" >竖</option>
											</c:when>
											<c:when test="${modelChannel.displayMode == 0}">
												<option value="1">横</option>
												<option value="0" selected="selected">竖</option>
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</select>
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
	// 页面加载完成后执行的方法
	$(function(){
       	$("#modelTableRow_form").validate({
	        submitHandler:function(form){
	        	setModelChannelRow();
	        }    
	    });
	});

</script>
