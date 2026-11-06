<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<ul id="skuUl" style="overflow: hidden;">
    <%--<c:forEach items="${subList}" varStatus="vs" var="object">
        <c:choose>
            <c:when test="${vs.index==0}">
                <li class="on">
            </c:when>
            <c:otherwise>
                <li>
            </c:otherwise>
        </c:choose>
        <section><img src="${object.imgUrl}"></section><i></i>
        &lt;%&ndash;<p>${object.skuName}</p>&ndash;%&gt;
        <p><a href="#" data-toggle="tooltip" data-placement="top" title="${object.skuName}${object.skuUnit}" data-original-title="${object.skuName}${object.skuUnit}"><span>${object.skuName}</span></a></p>
        <input type="hidden" value="${object.id}" name="skuId">
        <input type="hidden" value="${object.skuName}" name="skuName">
        </li>
    </c:forEach>--%>
</ul>
<div id="addLibraryDiv" style="display:none; text-align: center; line-height: 40px; margin: 50px auto 10px;">
    <p>您的商品库还没有商品，请先添加商品</p>
    <a href="/skuLibrary/list" target="_blank" class="btn btn-success btn-lg2">添加商品</a>
</div>
<script>
    var skuIds = '${skuIds}';
    var skuName = '${skuName}';
    var skuCode = '${skuCode}';
    var skuGroupTypeId = '${skuGroupTypeId}';
    var skuTypeId = '${skuTypeId}';
    var pageNo = 1;
    var loading = false;
    function load(){
        loading = true;
        pageNo = loadSku(skuIds,skuName,skuCode,skuGroupTypeId,skuTypeId,pageNo);
        loading = false;
    }
    $('#st_vm_shortcuts_sku_div').scroll(function(){
        totalheight = parseFloat($('#st_vm_shortcuts_sku_div').height()) + parseFloat($('#st_vm_shortcuts_sku_div').scrollTop()) + 120;
        if($('#skuUl').height() <= totalheight){
            if(!loading){
                load();
            }
        }
    });
    $(function () {
        $("[data-toggle='tooltip']").tooltip();
        load();
    });
</script>