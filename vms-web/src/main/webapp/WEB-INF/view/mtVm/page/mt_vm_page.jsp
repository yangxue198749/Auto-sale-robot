<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty mtVmList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="9">
			<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
		</td></tr>--%>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
		     	<c:forEach items="${mtVmList }" var="mtVm">
			         <tr>
			            <td class="wh5"><input type="checkbox" value="${mtVm.id }"></td>
			            <td class="wh10"><span>${mtVm.innerCode }</span></td>
			            <td class="wh10"><span>${u:getAreaName(mtVm.node.provinceId)}${u:getAreaName(mtVm.node.cityId)}${u:getAreaName(mtVm.node.areaId)}</span></td>
			            <td class="wh15">
			                <p><span>${mtVm.node.nodeName }</span></p>
			                <p><a href="#" data-toggle="tooltip" data-placement="bottom" title="${mtVm.vmDesc }" data-original-title="${mtVm.node.nodeAddress }">具体位置<i class="iconfont f14 ml5 icon-tishi cf_blue"></i></a></p>
			            </td>
			            <td class="wh10">
                            <p><span>${u:getModelName(mtVm.vmModelId)}</span><input type="hidden" class="shortcuts" value="${u:getVmShortcuts(mtVm.vmModelId)}"></p>
							<input type="hidden" class="temperatureHandlerMode" value="${u:getVmTemperatureHandlerMode(mtVm.vmModelId)}">
							<input type="hidden" class="screenHave" value="${u:getVmScreenHave(mtVm.vmModelId)}">
                            <p><a href="#" data-toggle="tooltip" data-placement="bottom" data-placement="left" data-html="true" title="编号：${mtVm.hardwareCode}<br>SIM卡：${mtVm.simNumber }<br>打印机：${mtVm.printStatus}" data-original-title="编号：${mtVm.hardwareCode}<br>SIM卡：${mtVm.simNumber }<br>打印机：${mtVm.printStatus}">更多<i class="iconfont f14 ml5 icon-tishi cf_blue"></i></a></p>
						</td>
			            <td class="wh10">
			            		<c:choose>
									<c:when test="${mtVm.vmStatus == 0 }"><span class="icon_state gray">未投放</span></c:when>
			            			<c:when test="${mtVm.vmStatus == 1 }"><span class="icon_state">投放中</span></c:when>
			            			<c:when test="${mtVm.vmStatus == 2 }"><span class="icon_state">在售</span></c:when>
			            			<c:when test="${mtVm.vmStatus == 3 }"><span class="icon_state red">停售</span></c:when>
			            			<c:when test="${mtVm.vmStatus == 4 }"><span class="icon_state blue">撤回中</span></c:when>
			            		</c:choose>
			            </td>
			            <%-- v3.2 将SIM卡合并到 售货机信息 中 <td class="wh10"><span>${mtVm.sim.simPhone }</span></td>--%>
			            <td class="wh20">
			                <div class="btn-group" data-toggle="buttons">
			        			<button type="button" class="btn btn-default btn-sm" href="/mtVm/showChannelDialog?mtVmId=${mtVm.id }" data-toggle="modal" data-target="#viewTheGoods">货道</button>
								<c:if test="${u:getVmCombinatorialPattern(mtVm.vmModelId)}"> <button type="button" class="btn btn-default btn-sm" href="/mtVm/showSkuSortDialog?mtVmId=${mtVm.id }" data-toggle="modal" data-target="#viewTheList">列表页</button> </c:if>
			        			<button type="button" class="btn btn-default btn-sm" href="/mtVm/showMtSkuPriceDialog?mtVmId=${mtVm.id }" data-toggle="modal" data-target="#viewThePrice">价格</button>
			        			<button type="button" class="btn btn-default btn-sm" href="/mtVm/showMtStrategyDialog?mtVmId=${mtVm.id }" data-toggle="modal" data-target="#viewTheStrategy">策略</button>
			                </div>
			            </td>
			            <td class="wh10">
			            	<p><span>${u:getUserById(mtVm.ctor).userName}</span></p>
			                <p><span><fmt:formatDate value="${mtVm.ctime}" type="both"/></span></p>
			            </td>
			            <td class="wh10">
			            	<p><span>${u:getUserById(mtVm.utor).userName}</span></p>
			                <p><span><fmt:formatDate value="${mtVm.utime}" type="both"/></span></p>
			            </td>
			         </tr>
		         </c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  mtVmList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true" />
</c:if>
<script type="text/javascript">
$(function(){
	 $("[data-toggle='tooltip']").tooltip();
})
</script>
