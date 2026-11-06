<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="10">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
    </c:when>
	<c:otherwise>
    <table>
        <tbody class="auto">
             <c:forEach items="${nodeList }" var="company">
                  <tr>
                    <td class="wh3"><input type="checkbox" name="companyCheckbox" value="${company.id}"></td>
                    <td class="wh20"><span>${company.companyName}</span></td>
                    <td class="wh10"><span>${company.customer.customerName}</span></td>
                    <td class="wh7"><span><c:if test="${company.maxVmCapacity == null}" >无限大</c:if>${company.maxVmCapacity}</span></td>
                    <td class="wh10"><span>${company.adminUser.userName}</span></td>
                    <td class="wh10"><span>${company.adminUser.userAccount}</span></td>
                    <td class="wh10"><span>${company.adminUser.userPhone}</span></td>
                    <td class="wh10">
                        <p><span>${u:getUserById(company.ctor).userName}</span></p>
                        <p><span><fmt:formatDate value="${company.ctime}" type="both"/></span></p>
                    </td>
                    <td class="wh10">
                        <p><span>${u:getUserById(company.utor).userName}</span></p>
                        <p><span><fmt:formatDate value="${company.utime}" type="both"/></span></p>
                    </td>
                   <td class="wh10">
                      <span>${company.exportStock == 1 ? "已导出" : "未导出"}</span>
                   </td>
                 </tr>
			</c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="/${url }" setPage="true" />
</c:if>