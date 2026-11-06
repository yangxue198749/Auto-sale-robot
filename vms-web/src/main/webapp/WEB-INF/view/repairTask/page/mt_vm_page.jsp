<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<div class="main_commontable max300 mt15 border_line">
<c:choose>
	<c:when test="${empty mtVmList }">
		<div>
			<section style="padding:100px 0; text-align:center;">
				<p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
				<p>暂无内容</p>
			</section>
		</div>
		<%--<tr><td style="padding:100px 0; text-align: center" colspan="3"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>--%>
	</c:when>
	<c:otherwise>
	 <table>
		 <thead>
		 <tr>
			 <td class="wh10"></td>
			 <td class="wh40">售货机编号</td>
			 <td class="wh50">点位</td>
		 </tr>
		 </thead>
		 <tbody class="auto oneCheck">
	         <c:forEach items="${mtVmList }" var="mtVm">
	             <tr>
	    			<td class="wh10"><input type="checkbox" userId="${mtVm.node.repair.id }" innerCode="${mtVm.innerCode }" value="${mtVm.id }"></td>
	                <td class="wh40">${mtVm.innerCode }</td>
	                <td class="wh50">${mtVm.node.nodeName }</td>
	             </tr>
	         </c:forEach>
		 </tbody>
	 </table>
	</c:otherwise>
</c:choose>
</div>
<c:if test="${! empty  mtVmList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false" id="mtVmDiv"/>
</c:if>
