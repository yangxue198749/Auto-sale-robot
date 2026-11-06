<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">查看价格</h4>
         </div>
         <div class="modal-body">
             <div class="main_commontable max400 border_line">
                 <table>
                      <thead>
                          <tr>
                 			<td class="wh40">商品名称</td>
                             <td class="wh30">实际售价(分)</td>
                             <td class="wh30">建议零售价(分)</td>
                          </tr>
                      </thead>
                      <tbody class="auto">
	                       <c:choose>
			             		<c:when test="${! empty skuSortList}">
			             			<c:forEach items="${skuSortList }" var="skuSort">
			                          <tr>
			                 			<td class="wh40">${u:getSkuById(skuSort.skuId).skuName}${u:getSkuById(skuSort.skuId).skuUnit}</td>
			                             <td class="wh30">${u:getSkuPirceForMtVm(skuSort.skuId,skuSort.mtVm.id)}</td>
			                             <td class="wh30">${u:getSkuById(skuSort.skuId).offerPrice}</td>
			                          </tr>
			             			</c:forEach>
			             		</c:when>
			              </c:choose>
                      </tbody>
                 </table>
             </div>
         </div>
         <!--按钮-->
         <div class="modal-footer">
             <button type="button" class="btn btn-success btn-lg2 confirmbtn" data-dismiss="modal">关闭</button>
         </div>
     </div>
 </div>
