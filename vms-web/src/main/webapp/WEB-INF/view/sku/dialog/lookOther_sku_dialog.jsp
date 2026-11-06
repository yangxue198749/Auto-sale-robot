<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form action="">
    <div class="modal-dialog w600">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title f16">其他信息</h4>
	        </div>
	        <div class="modal-body">
				<!--new table start-->
				<div class="main_commontable border_line">
					<table>
						<tbody>
							<tr>
								<td class="wh20">商品编码:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.skuCode}</span></td>
								<td class="wh20">商品颜色:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.skuColor}</span></td>
							</tr>
							<tr>
								<td class="wh20">保质期:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.skuShelfLife}${0 == sku.validityType || null == sku.validityType ? "日" : "月"}</span></td>
								<td class="wh20">商品产地:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.skuMadeArea}</span></td>
							</tr>
							<tr>
								<td class="wh20">存货类别:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.inventoryType}</span></td>
								<td class="wh20">品牌厂商:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.skuProducer}</span></td>
							</tr>
							<tr>
								<td class="wh20">存储方式:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.storageMethod}</span></td>
								<td class="wh20">箱规:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.packingSpecification}</span></td>
							</tr>
							<tr>
								<td class="wh20">国标码:</td><td class="wh30" style="background:#edf7ed;"><span>${sku.dbCode}</span></td>
								<td class="wh20">是否可预订:</td>
								<td class="wh30" style="background:#edf7ed;">
									<span>
										<c:if test="${sku.reserve == 0 || sku.reserve == null}">否</c:if>
										<c:if test="${sku.reserve == 1}">是</c:if>
									</span>
								</td>
							</tr>
							<tr>
								<td class="wh20">效期监控</td>
								<td class="wh30" style="background:#edf7ed;">
									<span>
										<c:if test="${ 1 == sku.validityMonitor }">是</c:if>
										<c:if test="${ 0 == sku.validityMonitor || null == sku.validityMonitor }">否</c:if>
									</span>
								</td>
								<td class="wh20"></td><td class="wh30" style="background:#edf7ed;"><span></span></td>
								<!-- 2017/12/21 移除押金 -->
								<%--<td class="wh20">押金</td>
								<td class="wh30" style="background:#edf7ed;">
									<span>
										<c:if test="${sku.deposit == null}">0分</c:if>
										<c:if test="${sku.deposit != null}">${sku.deposit}分</c:if>
									</span>
								</td>--%>
							</tr>
							<%-- 20171228 10:45 隐藏推广属性
							<tr>
								<td class="wh20">推广属性</td>
								<td class="wh30" style="background:#edf7ed;">
									<span>
										<c:if test="${sku.generalizeType == 0}">常规</c:if>
										<c:if test="${sku.generalizeType == 1}">积分</c:if>
										<c:if test="${sku.generalizeType == 2}">公众号</c:if>
										<c:if test="${sku.generalizeType == 3}">免费</c:if>
									</span>
								</td>
								<td class="wh20"></td>
								<td class="wh30"></td>
							</tr>--%>
						</tbody>
					</table>
				</div>
				<!--new table end-->
				<!----以下内容为旧版本风格，待测试没问题完毕后，我将移除旧版界面 by tonney 20170405---->
	            <!--基本信息 start-->
<%--	            <form action="">
	                <div class="modal_commonbody mt10 clearfix">
	                    <section class="col-md-12">
	                        <span class="w80 pull-left text-right mr10">商品编码:</span>
	                        <span>${sku.skuCode}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                    <span class="w80 pull-left text-right mr10">商品颜色:</span>
	                    <span>${sku.skuColor}</span>
	                </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w60 pull-left text-right mr10">保质期:</span>
	                        <span>${sku.skuShelfLife}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w80 pull-left text-right mr10">商品产地:</span>
	                        <span>${sku.skuMadeArea}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w60 pull-left text-right mr10">存货类别:</span>
	                        <span>${sku.inventoryType}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w80 pull-left text-right mr10">品牌厂商:</span>
	                        <span>${sku.skuProducer}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w60 pull-left text-right mr10">存储方式:</span>
	                        <span>${sku.storageMethod}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w80 pull-left text-right mr10">箱规:</span>
	                        <span>${sku.packingSpecification}</span>
	                    </section>
	                    <section class="col-md-6 mt20">
	                        <span class="w60 pull-left text-right mr10">国际码:</span>
	                        <span>${sku.dbCode}</span>
	                    </section>
	                </div>
	                <!--基本信息 end-->
	            </form>--%>
	        </div>
	        <!--按钮-->
	        <div class="modal-footer">
	            <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
	        </div>
	    </div>
	</div>
</form>
