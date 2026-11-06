<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">其他信息</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable border_line color">
                <table>
                    <tbody>
                    <c:choose>
                        <c:when test="${! empty skuLibrary}">
                            <tr>
                                <td class="wh20">商品编码:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.skuCode}</span></td>
                                <td class="wh20">商品颜色:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.skuColor}</span></td>
                            </tr>
                            <tr>
                                <td class="wh20">保质期:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.skuShelfLife}</span></td>
                                <td class="wh20">商品产地:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.skuMadeArea}</span></td>
                            </tr>
                            <tr>
                                <td class="wh20">存货类别:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.inventoryType}</span></td>
                                <td class="wh20">品牌厂商:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.skuProducer}</span></td>
                            </tr>
                            <tr>
                                <td class="wh20">存储方式:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.storageMethod}</span></td>
                                <td class="wh20">箱规:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.packingSpecification}</span></td>
                            </tr>
                            <tr>
                                <td class="wh20">国际码:</td><td class="wh30" style="background:#edf7ed;"><span>${skuLibrary.sku.dbCode}</span></td>
                                <td class="wh20"></td><td class="wh30" style="background:#edf7ed;"><span></span></td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td class="wh20">商品编码:</td><td class="wh30"><span></span></td>
                                <td class="wh20">商品颜色:</td><td class="wh30"><span></span></td>
                            </tr>
                            <tr>
                                <td class="wh20">保质期:</td><td class="wh30"><span></span></td>
                                <td class="wh20">商品产地:</td><td class="wh30"><span></span></td>
                            </tr>
                            <tr>
                                <td class="wh20">存货类别:</td><td class="wh30"><span></span></td>
                                <td class="wh20">品牌厂商:</td><td class="wh30"><span></span></td>
                            </tr>
                            <tr>
                                <td class="wh20">存储方式:</td><td class="wh30"><span></span></td>
                                <td class="wh20">箱规:</td><td class="wh30"><span></span></td>
                            </tr>
                            <tr>
                                <td class="wh20">国际码:</td><td class="wh30"><span></span></td>
                                <td class="wh20"></td><td class="wh30"><span></span></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>


                    </tbody>
                </table>
            </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
        </div>
    </div>
</div>
