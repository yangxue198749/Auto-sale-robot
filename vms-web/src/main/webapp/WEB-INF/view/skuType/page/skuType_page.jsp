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
				<c:forEach items="${nodeList }" var="skuType">
					<tr <c:if test="${isOnelevel}">class="type_group"</c:if> >
                        <td class="wh10">
                        	<c:choose>
                        		<c:when test="${isOnelevel}"><input name="skuTypeGroupIds" type="checkbox" value="${skuType.id}"></c:when>
                        		<c:otherwise><input name="skuTypeIds" type="checkbox" value="${skuType.id}"></c:otherwise>
                        	</c:choose>
                        </td>
                        <td class="wh30">
                            <span>${skuType.typeName}</span>
                        </td>
                        <td class="wh30">
                        	<p><span>${u:getUserById(skuType.ctor).userName}</span></p>
							<p><span><fmt:formatDate value="${skuType.ctime}" type="both"/></span></p>
                        </td>
                        <td class="wh30">
                        	<p><span>${u:getUserById(skuType.utor).userName}</span></p>
							<p><span><fmt:formatDate value="${skuType.utime}" type="both"/></span></p>
                        </td>
                    </tr>
				</c:forEach>
            </tbody>
		</table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<c:choose>
		<c:when test="${isOnelevel}">
			<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="/${url }" setPage="false" id="pagediv1"/>
		</c:when>
		<c:otherwise>
			<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="/${url }" setPage="false" id="pagediv2"/>
		</c:otherwise>
	</c:choose>
	
</c:if>
