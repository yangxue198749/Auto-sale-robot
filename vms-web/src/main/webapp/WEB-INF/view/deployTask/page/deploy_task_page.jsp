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
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="8">
			<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
		</td></tr>--%>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto workorder_list">
		      <c:forEach items="${taskVmList }" var="taskVm">
		         <tr>
		            <td class="wh5"><input type="checkbox" value="${taskVm.taskVmId }"></td>
		            <td class="wh10"><span>${taskVm.mtVm.innerCode }</span></td>
		            <td class="wh20">
		                <p><span>${taskVm.mtVm.node.nodeName }</span></p>
		                <p><a href="#" data-toggle="tooltip" data-placement="bottom" title="${taskVm.mtVm.node.nodeAddress }">具体位置<i class="iconfont f14 ml5 icon-tishi cf_blue"></i></a></p>
		            </td>
		            <td class="wh10 workorder_state">

		                	<c:choose>
								<c:when test="${taskVm.taskVmStatus==1 }"><span class="icon_state blue">未开始</span></c:when>
								<c:when test="${taskVm.taskVmStatus==2 }"><span class="icon_state green">进行中</span></c:when>
								<c:when test="${taskVm.taskVmStatus==4 }"><span class="icon_state gray">已完成</span></c:when>
								<c:when test="${taskVm.taskVmStatus==7 }"><span class="icon_state red">已取消</span></c:when>
		                	</c:choose>

		            </td>
		            <td class="wh10">
		            	<p><span>${u:getUserById(taskVm.owner).userName}</span></p>
		            </td>
		            <td class="wh15">
		                <p><span><fmt:formatDate value="${taskVm.taskTime}" pattern="yyyy-MM-dd" type="both" dateStyle="full"/></span></p>
		            </td>
		            <td class="wh15">
		            	<p><span>${u:getUserById(taskVm.ctor).userName}</span></p>
		                <p><span><fmt:formatDate value="${taskVm.ctime}" type="both"/></span></p>
		            </td>
		            <td class="wh15">
		                <p><span><fmt:formatDate value="${taskVm.finishTime}" type="both"/></span></p>
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