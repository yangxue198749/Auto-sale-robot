<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<c:choose>
	<c:when test="${empty nodeList }">
		<table>
             <tbody class="auto">
	             <tr>
		            <td class="wh3"><input type="checkbox" value="${customer.id}"></td>
		            <td class="wh25">
		                <c:forEach items="${giroAccountType.dictionary}" var="tSysDictionary">
		                	<c:if test="${customer.giroAccountType == tSysDictionary.id}">
		                		<span>${tSysDictionary.dictionaryName}</span>
		                	</c:if>
		                </c:forEach>
		            </td>
		            <td class="wh22">
		                <span>${customer.giroAccount}</span>
		            </td>
		            <td class="wh20">
		                <span>${customer.giroAccountName}</span>
		            </td>
		            <td class="wh15">
		                <p><span>${u:getUserById(customer.ctor).userName}</span></p>
		                <p><span><fmt:formatDate value="${customer.ctime}" type="both"/></span></p>
		            </td>
		            <td class="wh15">
		                <p><span>${u:getUserById(customer.utor).userName}</span></p>
		                <p><span><fmt:formatDate value="${customer.utime}" type="both"/></span></p>
		            </td>
		        </tr>
            </tbody>
         </table>
	</c:when>
	<c:otherwise>
		<table>
             <tbody class="auto">
             <c:forEach items="${nodeList }" var="customer">
             	<tr>
                    <td class="wh3"><input type="checkbox" value="${customer.id}"></td>
                    <td class="wh25">
                        <c:forEach items="${giroAccountType.dictionary}" var="tSysDictionary">
                        	<c:if test="${customer.giroAccountType == tSysDictionary.id}">
                        		<span>${tSysDictionary.dictionaryName}</span>
                        	</c:if>
                        </c:forEach>
                    </td>
                    <td class="wh22">
                        <span>${customer.giroAccount}</span>
                    </td>
                    <td class="wh20">
                        <span>${customer.giroAccountName}</span>
                    </td>
                    <td class="wh15">
                        <p><span>${u:getUserById(customer.ctor).userName}</span></p>
                        <p><span><fmt:formatDate value="${customer.ctime}" type="both" /></span></p>
                    </td>
                    <td class="wh15">
                        <p><span>${u:getUserById(customer.utor).userName}</span></p>
                        <p><span><fmt:formatDate value="${customer.utime}" type="both" /></span></p>
						<%--<p><span><fmt:formatDate value="${customer.utime}" type="both" dateStyle="full" /></span></p>--%>
                    </td>
                </tr>
			</c:forEach>
            </tbody>

         </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true" />
</c:if>
