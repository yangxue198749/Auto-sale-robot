<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">详细库存</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable max300">
              <c:choose>
              	<c:when test="${empty skuStockList}">
                    <div>
                        <section style="padding:100px 0; text-align:center;">
                            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                            <p>暂无内容</p>
                        </section>
                    </div>
              		<%--<tr>
              			<td style="padding:100px 0; text-align: center;" colspan="4">
              				<p>
              					<i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i>
              				</p>
              				<p>暂无内容</p>
              			</td>
              		</tr>--%>
              	</c:when>
              	<c:otherwise>
                  <table>
                      <thead>
                      <tr>
                          <td class="wh20">货道</td>
                          <td class="wh50">商品名称</td>
                          <td class="wh15">库存余量</td>
                          <td class="wh15">是否有货</td>
                      </tr>
                      </thead>
                      <tbody class="auto">
              				<c:forEach items="${skuStockList}" var="skuStock">
                				<tr>
                          <td class="wh20">${skuStock.channelCode}</td>
                          <td class="wh50">${skuStock.sku.skuName}</td>
                          <td class="wh15">${skuStock.currNum}</td>
                          <td class="wh15">${skuStock.hasStock}</td>
                				</tr>
              				</c:forEach>
                      </tbody>
                  </table>
              	</c:otherwise>
              </c:choose>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
    </div>
</div>
