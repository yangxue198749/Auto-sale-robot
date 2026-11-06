<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div id="energySaveDetailDiv">
<c:choose>
	<c:when test="${empty stVmStrategyList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="5"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>--%>
	</c:when>
	<c:otherwise>
		 <table>
			 <thead>
			 <tr>
				 <td class="wh5"></td>
				 <td class="wh20">策略名称</td>
				 <td class="wh35">策略说明</td>
				 <td class="wh20">售货机型号</td>
				 <td class="wh20">节能策略</td>
			 </tr>
			 </thead>
			 <tbody class="auto singleCheck">
			<c:forEach  items="${stVmStrategyList }" var="stVmStrategy">
				 <tr>
					<td class="wh5"><input type="checkbox" value="${stVmStrategy.strategyId }"></td>
					<td class="wh20">${stVmStrategy.strategyName }</td>
					<td class="wh35">${stVmStrategy.strategyDesc }</td>
					<td class="wh20">${stVmStrategy.vmModel.modelName }</td>
					<td class="wh20"><button type="button" href="${path }/stVmEnergySave/showStVmEnergySave?strategyId=${stVmStrategy.strategyId }" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#viewTheEnergy">查看</button></td>
				 </tr>
			</c:forEach>
			 </tbody>
		 </table>
	</c:otherwise>
</c:choose>
</div>
<c:if test="${! empty  stVmStrategyList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false" id="energyStDiv" />
</c:if>
