<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<div class="commonTable max400">
    <c:choose>
        <c:when test="${empty companyList }">
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
                    <td class="wh10"></td>
                    <td>公司名称</td>
                </tr>
                </thead>
                <tbody class="oneCheck">
                <c:forEach items="${companyList }" var="company">
                    <tr>
                        <td><input type="checkbox" data-company-id=${company.id} value=${company.companyName}></td>
                        <td>${company.companyName}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<c:if test="${! empty  companyList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize}" pageNo="${pageNo }" id="openApiDiv" url="${url }" setPage="false"/>
</c:if>

<script>
    $(function(){
        //处理table单选
        $(".oneCheck td").on("click","input[type='checkbox']",function(){
            if($(this).prop("checked")){
                $(this).parents().siblings().find("input[type='checkbox']").prop("checked",false);
            }
        });
    })
</script>
