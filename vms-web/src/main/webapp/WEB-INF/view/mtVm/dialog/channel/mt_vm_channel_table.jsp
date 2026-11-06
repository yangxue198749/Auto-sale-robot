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
        <%--<tr><td style="padding:100px 0; text-align: center" colspan="6"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>--%>
    </c:when>
    <c:otherwise>
    <table>
        <thead>
            <tr>
                <td class="wh3"></td>
                <td class="wh27">策略名称</td>
                <td class="wh30">策略说明</td>
                <td class="wh20">售货机型号</td>
                <td class="wh10">货道配置</td>
                <td class="wh10">列表页配置</td>
            </tr>
        </thead>
        <tbody class="auto singleCheck">
        <c:forEach items="${subList}" varStatus="vs" var="object">
            <tr>
                <td class="wh3"><input type="checkbox" value="${object.strategyId}" name="strategyId"></td>
                <td class="wh27">${object.strategyName}</td>
                <td class="wh30">${object.strategyDesc}</td>
                <td class="wh20">${object.vmModel.modelName}</td>
                <td class="wh10"><button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#viewTheGoods" href="${path}/stVmChannel/showStVmChannelDialog?strategyId=${object.strategyId}">查看</button></td>
                <td class="wh10"><button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#viewTheList"  href="${path}/stVmChannel/showSortDialog?strategyId=${object.strategyId}">查看</button></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${!empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" id="st_vm_channel_div" setPage="false"/>
</c:if>

<script>
    $(".singleCheck td").on("click","input[type='checkbox']",function(){
        if($(this).prop("checked")){
            $(this).parents("tr").siblings().find("input[type='checkbox']").prop("checked",false);
        }
    });
</script>