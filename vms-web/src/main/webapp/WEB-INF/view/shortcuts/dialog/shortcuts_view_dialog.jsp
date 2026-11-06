<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">查看闪购键配置策略</h4>
        </div>
        <div class="modal-body">
            <!--闪购键配置 table start-->
            <div class="main_commontable max400">
              <c:choose>
              	<c:when test="${empty shortcutsList}">
                    <div>
                        <section style="padding:100px 0; text-align:center;">
                            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                            <p>暂无内容</p>
                        </section>
                    </div>
              		<%--<tr>
              			<td style="padding:100px 0; text-align: center" colspan="4">
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
                            <td class="wh20">按键位置</td>
                            <td class="wh80">配置说明</td>
                         </tr>
                       </thead>
                       <tbody class="auto">
                        <c:forEach items="${shortcutsList}" var="shortcuts">
                            <tr>
                              <td class="wh20">${shortcuts.shortcutsCode}</td>
                              <td class="wh80">
                                  <c:choose>
                                      <c:when test="${shortcuts.bindType == 2}">
                                            ${shortcuts.resource.resourceName}
                                      </c:when>
                                      <c:otherwise>
                                          ${u:getSkuById(shortcuts.skuId).skuName}${u:getSkuById(shortcuts.skuId).skuUnit}
                                      </c:otherwise>
                                  </c:choose>
                              </td>
                            </tr>
                        </c:forEach>
                       </tbody>
                  </table>
              	</c:otherwise>
              </c:choose>
            </div>
            <!--闪购键 table end-->
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
        </div>
    </div>
</div>
