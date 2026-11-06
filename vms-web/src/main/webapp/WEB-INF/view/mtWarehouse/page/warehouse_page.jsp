<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty nodeList }">
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
            <c:forEach items="${nodeList }" var="warehouse">
                <tr>
                    <td class="wh5"><input type="checkbox" value="${warehouse.id}"></td>
                    <td class="wh15"><span>${warehouse.warehouseCode}</span></td>
                    <td class="wh20">
                        <p>
                            <span class="mr5">${warehouse.warehouseName}</span>
                            <a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="${warehouse.warehouseLevel}级仓库">
                                <i class="lv_icon">${warehouse.warehouseLevel}</i>
                            </a>
                        </p>
                        <p>地址:<span class="ml5">${warehouse.warehouseAddress}</span></p>

                    </td>
                    <td class="wh10"><button onclick="lookUniteArea(this)" type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#lookUniteArea" name="${warehouse.id}" >查看</button></td>
                    <td class="wh10"><span>${warehouse.warehouseType}</span></td>
                    <td class="wh10">
                        <p><span>${warehouse.warehouseManager}</span></p>
                        <p><span>${warehouse.warehousePhone}</span></p>
                    </td>
                    <td class="wh10">
                        <p>经度:<span class="ml5">${warehouse.longitude}</span></p>
                        <p>纬度:<span class="ml5">${warehouse.dimensions}</span></p>
                    </td>
                    <td class="wh10">
                        <p><span><fmt:formatDate value="${warehouse.ctime}" type="both"/></span></p>
                    </td>
                    <td class="wh10">
                        <p><span><fmt:formatDate value="${warehouse.utime}" type="both"/></span></p>
                    </td>
                 </tr>
            </c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }"
		pageNo="${pageNo }" url="/${url }" setPage="true" />
</c:if>
