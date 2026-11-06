<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">查看价格</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable max400 border_line">
                <c:choose>
                    <c:when test="${empty subList}">
                        <div>
                            <section style="padding:100px 0; text-align:center;">
                                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                                <p>暂无内容</p>
                            </section>
                        </div>
                        <%--<tr><td style="padding:100px 0; text-align: center" colspan="3"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>--%>
                    </c:when>
                    <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <td class="wh40">商品名称</td>
                                <td class="wh30">建议零售价(分)</td>
                                <td class="wh30">实际售价(分)</td>
                            </tr>
                        </thead>
                        <tbody class="auto">
                        <c:forEach items="${subList}" varStatus="vs" var="object">
                            <tr>
                                <td class="wh40">${object.sku.skuName}</td>
                                <td class="wh30">${object.sku.offerPrice}</td>
                                <td class="wh30">${object.realPrice}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2 confirmbtn" data-dismiss="modal">确定</button>
        </div>

    </div>
</div>