<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">查看闪购键</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable max400 border_line">
                <table>
                    <thead>
                    <tr>
                        <td class="wh40">闪购键位置</td>
                        <td class="wh60">配置说明</td>
                    </tr>
                    </thead>
                    <tbody class="auto">
                    <c:forEach items="${subList}" var="object">
                        <tr>
                            <td class="wh40">${object.shortcutsCode}</td>
                            <td class="wh60">
                            <c:if test="${object.bindType == 1}">
                                ${u:getSkuById(object.skuId).skuName}${u:getSkuById(object.skuId).skuUnit}
                            </c:if>
                            <c:if test="${object.bindType == 2}">
                                ${object.resource.resourceName}
                            </c:if>
                            </td>
                        </tr>
                    </c:forEach>

                    </tbody>
                </table>
            </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2 confirmbtn" data-dismiss="modal">确定</button>
        </div>

    </div>
</div>