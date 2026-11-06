<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">查看价格</h4>
        </div>
        <div class="modal-body">
            <!--查看价格 table start-->
            <div class="main_commontable max400">
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
                                    <td class="wh60">商品名称</td>
                                    <td class="wh20">建议零售价(分)</td>
                                    <td class="wh20">售价(分)</td>
                                </tr>
                            </thead>
                            <tbody class="auto">
                            <c:forEach items="${subList}" varStatus="vs" var="object">
                                <tr>
                                    <td class="wh60">${object.sku.skuName}${object.sku.skuUnit}</td>
                                    <td class="wh20">${object.sku.offerPrice}</td>
                                    <td class="wh20">${object.realPrice}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        </c:otherwise>
                    </c:choose>
            </div>
            <!--查看价格 table end-->
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
        </div>
    </div>
</div>
