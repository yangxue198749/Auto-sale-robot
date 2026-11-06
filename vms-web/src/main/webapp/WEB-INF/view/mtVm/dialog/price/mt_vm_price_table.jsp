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
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="4"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <thead>
        <tr>
            <td class="wh3"></td>
            <td class="wh27">策略名称</td>
            <td class="wh40">策略说明</td>
            <td class="wh30">价格配置</td>
        </tr>
        </thead>
        <tbody class="auto singleCheck">
        <c:forEach items="${subList}" varStatus="vs" var="object">
            <tr>
                <td class="wh3"><input type="checkbox" value="${object.strategyId}" name="strategyId"></td>
                <td class="wh27">${object.strategyName}</td>
                <td class="wh40">${object.strategyDesc}</td>
                <td class="wh30"><button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#viewThePrice" href="${path}/mtVmPrice/detail?strategyId=${object.strategyId}">查看</button></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${!empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" id="st_vm_price_div" setPage="false"/>
</c:if>

<script>
    $(".singleCheck td").on("click","input[type='checkbox']",function(){
        if($(this).prop("checked")){
            $(this).parents("tr").siblings().find("input[type='checkbox']").prop("checked",false);
        }
    });
</script>