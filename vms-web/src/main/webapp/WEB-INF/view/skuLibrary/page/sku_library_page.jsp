<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!--theadscroll end-->
<c:choose>
    <c:when test="${empty skuList }">
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
        <c:forEach items="${skuList}" var="skuLibrary">
            <tr>
                <td class="wh3">
                    <input type="checkbox" value="${skuLibrary.sku.id}">
                </td>
                <td class="wh8">
                    <img src="${skuLibrary.sku.imgUrl}" style="width: 40px;height: 71px">
                </td>
                <td class="wh14">
                    <span>${skuLibrary.sku.skuName}</span>
                </td>
                <td class="wh5">
                    <span>${skuLibrary.sku.skuUnit}</span>
                </td>
                <td class="wh5">
                    <span>${skuLibrary.sku.id}</span>
                </td>
                <td class="wh10">
                    <span>${skuLibrary.sku.offerPrice}</span>
                </td>
                <td class="wh5">
                    <span>${skuLibrary.sku.skuType.parent.typeName}</span>
                </td>
                <td class="wh5">
                    <span>${skuLibrary.sku.skuType.typeName}</span>
                </td>
                <td class="wh10">
                    <p><span>厚:${skuLibrary.sku.skuLength}</span></p>
                    <p><span>宽:${skuLibrary.sku.skuWidth}</span></p>
                    <p><span>高:${skuLibrary.sku.skuHeight}</span></p>
                </td>
                <td class="wh15">
                    <span>${skuLibrary.sku.skuDesc}</span>
                </td>
                <td class="wh5">
                    <span>${skuLibrary.sku.saleArea}</span>
                </td>
                <td class="wh5">
                    <span class="btn btn-success btn-sm" data-toggle="modal" data-target="#checkInfo" href="/skuLibrary/showSkuLibraryDetailDialog?id=${skuLibrary.id}">查看</span>
                </td>
                <td class="wh10">
                    <p><span>${u:getUserById(skuLibrary.ctor).userName}</span></p>
                    <p><span><fmt:formatDate value="${skuLibrary.ctime}" type="both"/></span></p>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty  skuList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true" />
</c:if>