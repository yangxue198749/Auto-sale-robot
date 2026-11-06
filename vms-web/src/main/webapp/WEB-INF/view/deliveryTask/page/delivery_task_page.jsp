<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty taskVmList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="10">
			<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
		</td></tr>--%>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto workorder_list" id="workOrderGrid">
		      <c:forEach items="${taskVmList }" var="taskVm">
		         <tr>
		            <td class="wh3"><input type="checkbox" value="${taskVm.taskVmId }"></td>
		            <td class="wh10"><span>${taskVm.mtVm.innerCode }</span></td>
		            <td class="wh17">
		                <p><span>${taskVm.mtVm.node.nodeName }</span></p>
		                <p><a href="#" data-toggle="tooltip" data-placement="bottom" title="${taskVm.mtVm.node.nodeAddress }">具体位置<i class="iconfont f14 ml5 icon-tishi cf_blue"></i></a></p>
		            </td>
		            <td class="wh10 workorder_state">
						<c:choose>
							<c:when test="${taskVm.taskVmStatus==1 }"><span class="icon_state blue">未开始</span></c:when>
							<c:when test="${taskVm.taskVmStatus==2 }"><span class="icon_state green">进行中</span></c:when>
							<c:when test="${taskVm.taskVmStatus==4 }"><span data-status="1" class="icon_state gray">已完成</span></c:when>
							<c:when test="${taskVm.taskVmStatus==7 }"><span class="icon_state red">已取消</span></c:when>
						</c:choose>
		            </td>
					 <td class="wh10">
						 <p><span>
							 <c:if test="${taskVm.deliveryPattern == 0}">
								 <span>补满</span>
							 </c:if>
							 <c:if test="${taskVm.deliveryPattern == 1}">
								 <span>手动</span>
							 </c:if>
							 <c:if test="${taskVm.deliveryPattern == ''}">
								 <span></span>
						     </c:if>
							<%-- <c:choose>
								 <c:when test="${taskVm.deliveryPattern == 0 }"><span>补满</span></c:when>
								 <c:when test="${taskVm.deliveryPattern == 1 }"><span>手动</span></c:when>
								 <c:when test="${taskVm.deliveryPattern == ''}"><span></span></c:when>
							 </c:choose>--%>
						 </span></p>
					 </td>
		            <td class="wh10">
		            	<p><span>${u:getUserById(taskVm.owner).userName}</span></p>
		            </td>
		            <td class="wh10">
		                <p><span><fmt:formatDate value="${taskVm.taskTime}" pattern="yyyy-MM-dd" type="both" dateStyle="full"/></span></p>
		            </td>
		            <td class="wh10">
		            	<p><span>${u:getUserById(taskVm.ctor).userName}</span></p>
		                <p><span><fmt:formatDate value="${taskVm.ctime}" type="both"/></span></p>
		            </td>
		            <td class="wh10">
		                <p><span><fmt:formatDate value="${taskVm.finishTime}" type="both"/></span></p>
		            </td>
					 <td class="wh10">
						 <button type="button" class="btn btn-success f12 btn-sm" data-toggle="modal" href="/deliveryTask/showTaskDetalDialog?taskId=${taskVm.taskVmId }" data-target="#showTaskDialog">查看</button>
					 </td>
		         </tr>
		        </c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  taskVmList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true" />
</c:if>
<script>
	$(function(){
		$('#workOrderGrid .btn-success').click(function(){
			var _this = $(this);
			var _statusClass = _this.parents('td').siblings().find('.icon_state');
			var _status = _statusClass.attr('data-status');
			if(_status != '1'){
				layer.msg("当前状态不可查看",{shade: [0.5, '#fff'],time:2000});
				return false;
			}
		})
	})
</script>