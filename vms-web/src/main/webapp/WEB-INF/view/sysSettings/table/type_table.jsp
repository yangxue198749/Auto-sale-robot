<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty subList}">
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
                <td><span>类型key</span></td>
                <td><span>类型描述</span></td>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${subList}" var="object">
            <tr settingId="${object.id}" style="cursor: pointer">
                <td><span>${object.key}</span></td>
                <td><span>${object.name}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false" />
</c:if>

<script type="text/javascript">
    $(function () {
        SysSetting.settingTable();
    })
</script>