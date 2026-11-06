<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty nodeList }">
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
				<c:forEach  var="i" begin="0" end="${nodeList.size() - 1 }" >
					<tr>
	                    <td class="wh5">${i+1}</td>
	                    <td class="wh15">
	                    	<span>${u:getUserById(nodeList[i].userId).userName}</span>
	                    </td>
	                    <td class="wh10">
	                        <span>${u:getDictNameById(nodeList[i].moduleId)}</span>
	                    </td>
	                    <td class="wh10">
	                        <%--<span>${nodeList[i].logOperation}</span>--%>
							<c:choose>
								<c:when test="${nodeList[i].logOperation == '保存'}"><span class="icon_state blue">保存</span></c:when>
								<c:when test="${nodeList[i].logOperation == '新建'}"><span class="icon_state green">新建</span></c:when>
								<c:when test="${nodeList[i].logOperation == '删除'}"><span class="icon_state gray">删除</span></c:when>
								<c:when test="${nodeList[i].logOperation == '修改'}"><span class="icon_state orange">修改</span></c:when>
								<c:otherwise><span class="icon_state yellow">其它</span></c:otherwise>
							</c:choose>
	                    </td>
	                    <td class="wh45">
	                        <span>${nodeList[i].logDesc}</span>
	                    </td>
	                    <td class="wh15">
	                        <span><fmt:formatDate value="${nodeList[i].logTime}" type="both"/></span>
	                    </td>
	                </tr>
				</c:forEach>
		</tbody>
	</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }"
		pageNo="${pageNo }" url="/${url }" setPage="true" />
</c:if>