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
                    <td class="wh5"></td>
                    <td><span>系统配置key</span></td>
                    <td><span>系统配置value</span></td>
                    <td><span>系统配置描述</span></td>
                </tr>
            </thead>
            <tbody class="singleCheck">
            <c:forEach items="${subList}" var="object">
                <tr>
                    <td><input type="checkbox" value="${object.id}"></td>
                    <td><span>${object.key}</span></td>
                    <td><span>${object.value}</span></td>
                    <td><span>${object.name}</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </c:otherwise>
    </c:choose>
<script>
    $(".singleCheck td").on("click","input[type='checkbox']",function(){
        if($(this).prop("checked")){
            $(this).parents().siblings().find("input[type='checkbox']").prop("checked",false);
        }
    });

</script>