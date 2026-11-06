<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
    <c:choose>
        <c:when test="${empty list}">
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
            <c:forEach items="${list}" var="report">
                <tr <c:if test="${null == report.name}">style="display: none" </c:if>>
                    <td class="wh70">
                        <span>${report.name}</span>
                    </td>
                    <td class="wh20">
                        <span>${report.time}</span>
                    </td>
                    <td class="wh10">
                        <span><a href="${report.url}" class="btn btn-success f12 btn-sm">下载</a></span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </c:otherwise>
    </c:choose>
