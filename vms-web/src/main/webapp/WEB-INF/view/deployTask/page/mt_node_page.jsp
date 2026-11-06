<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<c:choose>
	<c:when test="${empty mtNodeList }">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="3">
            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
        </td></tr>--%>
	</c:when>
	<c:otherwise>
    <table>
        <thead>
            <tr>
                <td class="wh10"></td>
                <td class="wh40">点位</td>
                <td class="wh50">具体位置</td>
            </tr>
        </thead>
        <tbody class="auto oneCheck">
           <c:forEach items="${mtNodeList }" var="mtNode">
              <tr>
                  <td class="wh10"><input type="checkbox" nodeName="${mtNode.nodeName }" value="${mtNode.id }"></td>
                  <td class="wh40">${mtNode.nodeName }</td>
                  <td class="wh50">${mtNode.nodeAddress }</td>
              </tr>
           </c:forEach>
        </tbody>
    </table>
</c:otherwise>
</c:choose>
<c:if test="${! empty  mtNodeList}">
<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false" id="mtNodeDiv" />
</c:if>