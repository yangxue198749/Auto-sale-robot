<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<c:choose>
    <c:when test="${empty versionList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
    </c:when>
    <c:otherwise>
    <table>
        <thead>
            <tr>
                <td width="40px"></td>
                <td>程序类型</td>
                <td>版本号</td>
            </tr>
        </thead>
        <tbody class="auto" id="withProgramDiv">
        <c:forEach items="${versionList}" var="version">
            <tr>
                <td>
                    <input type="radio" name="version" value="${version.id}" versionNo="${version.packageVersion}">
                </td>
                <td>
                    <c:choose>
                        <c:when test="${1==version.packageType}">
                            vmBox
                        </c:when>
                        <c:when test="${2==version.packageType}">
                            通讯板
                        </c:when>
                        <c:when test="${3==version.packageType}">
                            出货版
                        </c:when>
                    </c:choose>
                </td>
                <td>
                    ${version.packageVersion}
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty versionList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" id="selectVersionDiv" setPage="false" />
</c:if>