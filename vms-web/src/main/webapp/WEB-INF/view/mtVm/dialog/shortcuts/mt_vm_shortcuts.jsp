<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script type="text/javascript" src="${path}/vmsjs/mt_vm_shortcuts.js"></script>
<!--
**
****    模态框包含如下：
****    闪购键设置所需模态框：
#CtrlTheFlashBuy   闪购键设置
#Child_choiceTheGoods   子模态 关联商品
#viewTheFlashBuy  查看闪购键配置
****
-->

<!-------------------------------------闪购键设置：#CtrlTheFlashBuy start------------------------------------------------>
    <div class="modal-dialog w1000">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">闪购键设置</h4>
            </div>
            <div class="modal-body">
                <!---Tab 选择策略 与 直接设置 切换 start--->
                <div class="tabheader">
                    <ul>
                        <li class="on"><span>选择策略</span></li>
                        <li><span>直接设置</span></li>
                    </ul>
                </div>
                <!--控制tab切换-->
                <script>
                    $(function(){
                        $('#CtrlTheFlashBuy .tabheader').on('click','li',function(){
                            $(this).addClass('on').siblings().removeClass('on');
                            $('#CtrlTheFlashBuy .modal_commonbody').hide().eq($('#CtrlTheFlashBuy .tabheader li').index(this)).show();
                        });
                    });
                </script>
                <!---Tab 选择策略 与 直接设置 切换 end--->
                <!--选择策略 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
                        <form action="${path}/mtVmShortcuts/page" name="" method="post" id="st_vm_shortcuts_search_form" class="unKeyCode">
                            <input type="hidden" name="modelId" value="${mtVm.vmModelId}">
                            <section>
                                <strong>策略名称:</strong>
                                <div class="form-group w120 pull-left"><input class="form-control input-sm" name="strategyName" type="text" placeholder="请输入策略名称"></div>
                            </section>
                            <button type="button" class="btn btn-default f12 btn-lg2 mr10" id="st_vm_shortcuts_search_btn">查询</button>
                        </form>
                    </div>
                    <form id="mt_vm_shortcuts_form_st" method="post" action="${path}/mtVmShortcuts/save/st">
                        <c:forEach items="${ids}" var="vmId" >
                            <input type="hidden" name="ids" value="${vmId}">
                        </c:forEach>
                    <!--table body start-->
                    <div class="main_commontable max400 border_line clearfix" id="st_vm_shortcuts_div">
                        <jsp:include page="/mtVmShortcuts/page?modelId=${mtVm.vmModelId}" flush="true"></jsp:include>
                    </div>
                    <!--table body end-->
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                        </form>
                </div>
                <!--选择策略 end-->
                <!--直接设置 start-->
                <div class="modal_commonbody clearfix" style="display:none">
                    <form id="mt_vm_shortcuts_form" method="post" action="${path}/mtVmShortcuts/save">
                        <c:forEach items="${ids}" var="vmId" >
                            <input type="hidden" name="ids" value="${vmId}">
                        </c:forEach>
                    <!--table body start-->
                    <div class="main_commontable max400 border_line clearfix">
                        <table>
                            <thead>
                            <tr>
                                <td class="wh30">闪购键位置</td>
                                <td class="wh50">商品</td>
                                <td class="wh20">配置</td>
                            </tr>
                            </thead>
                            <tbody class="auto">
                            <c:forEach items="${mtVm.shortcutsAsc}" var="shortcuts">
                                <input type="hidden" name="skuIds" id="${shortcuts.shortcutsCode}_id" value="${shortcuts.sku.id}">
                                <input type="hidden" name="shortcutsCodes" id="${shortcuts.shortcutsCode}_code" value="${shortcuts.shortcutsCode}">
                                <input type="hidden" name="resourceIds" id="${shortcuts.shortcutsCode}_resource_id" value="${shortcuts.resource.id}">
                                <c:choose>
                                    <c:when test="${null == shortcuts.bindType || shortcuts.bindType == 1}">
                                        <input type="hidden" name="bindTypes" id="${shortcuts.shortcutsCode}_type" value="1">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="bindTypes" id="${shortcuts.shortcutsCode}_type" value="2">
                                    </c:otherwise>
                                </c:choose>
                                <tr>
                                    <td class="wh30">${shortcuts.shortcutsCode}</td>
                                    <td class="wh50" id="${shortcuts.shortcutsCode}_name">
                                        <c:choose>
                                            <c:when test="${shortcuts.bindType == 2}">
                                                ${shortcuts.resource.resourceName}
                                            </c:when>
                                            <c:otherwise>
                                                ${shortcuts.sku.skuName}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <%--<td class="wh20"><button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#Child_choiceTheGoods" href="${path}/mtVmShortcuts/sku?shortcutsCode=${shortcuts.shortcutsCode}">配置</button></td>--%>
                                    <td class="wh20">
                                        <div class="btn-group" data-toggle="buttons">
                                            <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#Child_choiceTheGoods" href="${path}/mtVmShortcuts/sku?shortcutsCode=${shortcuts.shortcutsCode}">商品</button>
                                            <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#selectLink" href="/mtVmShortcuts/selectMtVmResourcesDialog?shortcutsCode=${shortcuts.shortcutsCode}">链接</button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <!--table body end-->
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                        </form>
                </div>
                <!--直接设置 end-->
            </div>

        </div>
    </div>
<!-------------------------------------闪购键设置：#CtrlTheFlashBuy end------------------------------------------------>


<!-------------------------------------选择跳转页：#selectLink start------------------------------------------------>
<div class="modal fade" id="selectLink" tabindex="-1" role="dialog" aria-labelledby="选择跳转页" aria-hidden="true">

</div>
<!-------------------------------------选择跳转页：#selectLink end------------------------------------------------>

<script>
    $(function(){
        MtVmShortcuts.init();
    });
</script>