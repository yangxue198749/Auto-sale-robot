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
                <td class="wh8"></td>
                <td class="wh30">皮肤名称</td>
                <td class="wh42">皮肤描述</td>
                <td class="wh20">效果图</td>
            </tr>
        </thead>
        <tbody class="auto singleCheck">
        <c:forEach items="${subList}" varStatus="vs" var="object">
            <tr>
                <td class="wh8"><input type="checkbox" value="${object.id}" name="strategyId"></td>
                <td class="wh30">${object.themeName}</td>
                <td class="wh42">${object.themeDesc}</td>
                <td class="wh20">
                    <button type="button" href="${path}/theme/preview?themeId=${object.id}" class="btn btn-success btn-sm" data-toggle="modal" data-target="#viewTheme">查看</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${!empty subList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" id="st_vm_theme_div" setPage="false"/>
</c:if>
<!-- 查看 模态框 start-->
<div class="modal fade" id="viewTheme" tabindex="-1" role="dialog" aria-labelledby="查看" aria-hidden="true"></div>
<!-- 查看 模态框 end -->
<script>
    $(".singleCheck td").on("click","input[type='checkbox']",function(){
        if($(this).prop("checked")){
            $(this).parents("tr").siblings().find("input[type='checkbox']").prop("checked",false);
        }
    });
</script>