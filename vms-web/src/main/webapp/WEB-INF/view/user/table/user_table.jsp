<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty userList}">
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
            <c:forEach items="${userList}" var="user">
                <tr>
                    <td class="wh5"><input type="checkbox" value="${user.id}"></td>
                    <td class="wh10"><span>${user.userAccount}</span></td>
                    <td class="wh10"><span>${user.userName}</span></td>
                    <td class="wh10"><span>${user.userPhone}</span></td>
                    <td class="wh15">
                      <c:choose>
                          <c:when test="${empty user.company}">
                          <span>无</span>
                          </c:when>
                          <c:otherwise>
                          <span>${user.company.companyName}</span>
                          </c:otherwise>
                      </c:choose>
                    </td>
                    <td class="wh20 text-left" id="userRoleValue">
                        <c:forEach items="${user.userRole}" var="userRole">
                            <c:choose>
                                <c:when test="${userRole.role.roleType == 2 || userRole.role.roleType == 1}">
                                    <span class="icon_state2">${userRole.role.roleName}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="icon_state2 orange">${userRole.role.roleName}</span>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </td>
                    <td class="wh10">
                       <span><fmt:formatDate value="${user.loginTime}" type="both"/></span>
                        <%--<span><fmt:formatDate value="${user.loginTime}" type="both" dateStyle="full"/></span>--%>
                    </td>
                    <td class="wh10">
                        <p><span>${u:getUserById(user.ctor).userName}</span></p>
                        <p><span><fmt:formatDate value="${user.ctime}" type="both"/></span></p>
                    </td>
                    <td class="wh10">
                        <p><span>${u:getUserById(user.utor).userName}</span></p>
                        <p><span><fmt:formatDate value="${user.utime}" type="both"/></span></p>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty userList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
