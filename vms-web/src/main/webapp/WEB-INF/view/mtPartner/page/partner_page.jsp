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
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="8">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
	</c:when>
	<c:otherwise>
    <table class="sysTypeTable">
        <tbody class="auto" >
				<c:forEach items="${nodeList }" var="partner">
					<tr>
                        <td class="wh5"><input type="checkbox" value="${partner.id}"></td>
                        <td class="wh10"><span>${partner.partnerName}</span></td>
                        <td class="wh10"><span>${u:getDictNameById(partner.partnerType)}</span></td>
                        <td class="wh15"><span>${partner.partnerAddress}</span></td>
                        <td class="wh10">
                            <p><span>${partner.managerName}</span></p>
                            <p><span>${partner.managerPhone}</span></p>
                            <p><span>${partner.partnerEmail}</span></p>
                        </td>
                        <td class="wh15">
                            <p><span>${partner.collectionAccountType}</span></p>
                            <p><b>${partner.collectionAccount}</b></p>
                        </td>
                        <td class="wh15"><span>${partner.taxCode}</span></td>
                        <td class="wh10">
                            <p><span>${u:getUserById(partner.ctor).userName}</span></p>
	                        <p><span><fmt:formatDate value="${partner.ctime}" type="both"/></span></p>
                        </td>
                        <td class="wh10">
                            <p><span>${u:getUserById(partner.utor).userName}</span></p>
	                        <p><span><fmt:formatDate value="${partner.utime}" type="both"/></span></p>
                        </td>
                    </tr>
				</c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }"
		pageNo="${pageNo }" url="${path}/${url }" setPage="true"/>
</c:if>

