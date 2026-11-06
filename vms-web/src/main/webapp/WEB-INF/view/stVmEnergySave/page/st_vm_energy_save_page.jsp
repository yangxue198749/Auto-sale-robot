<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty stVmStrategyList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
	</c:when>
	<c:otherwise>
	<table>
		<tbody class="auto">
		    <c:forEach items="${stVmStrategyList }" var="stVmStrategy">
			    <tr>
			        <td class="wh5">
			            <input type="checkbox" value="${stVmStrategy.strategyId }">
			        </td>
			        <td class="wh20">
			            <span>${stVmStrategy.strategyName }</span>
			        </td>
			        <td class="wh20">
			            <span>${stVmStrategy.strategyDesc }</span>
			        </td>
			        <td class="wh15">
			            <span>${stVmStrategy.vmModel.modelName }</span>
			        </td>
			        <td class="wh10">
			            <button class="btn btn-success btn-sm" data-toggle="modal" href="/stVmEnergySave/showStVmEnergySave?strategyId=${stVmStrategy.strategyId }" data-target="#checkenergy">查看</button>
			        </td>
			        <td class="wh15">
			            <p>
                     		<span>${u:getUserById(stVmStrategy.ctor).userName}</span>
                     	</p>
                     	<p>
                     		<span>
                     			<fmt:formatDate value="${stVmStrategy.ctime}" type="both"/>
                     		</span>
                     	</p>
			        </td>
			        <td class="wh15">
			            <p>
		                    <span>${u:getUserById(stVmStrategy.utor).userName}</span>
                     	</p>
                     	<p>
		                   <span>
		                   		<fmt:formatDate value="${stVmStrategy.utime}" type="both"/>
		                   </span>
                     	</p>
			        </td>
			    </tr>
			</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  stVmStrategyList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true" />
</c:if>