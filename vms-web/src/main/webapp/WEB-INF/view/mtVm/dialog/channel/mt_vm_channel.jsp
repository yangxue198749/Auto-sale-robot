<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script type="text/javascript" src="${path}/vmsjs/mt_vm_channel.js"></script>
<!--
**
****    模态框包含如下：
****    货道设置所需模态框：
#CtrlTheGoods   货道设置
#Child_choiceTheGoods   子模态 关联商品
#Child_setTheList    子模态 列表页设置
#viewTheGoods  查看货道
#viewTheList  查看列表页配置
****
-->
<style>
    .slt.undo{ cursor:not-allowed; background: #eee; color: #aaa;}
</style>
<!-------------------------------------货道设置：#CtrlTheGoods start------------------------------------------------>
    <div class="modal-dialog w1000">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">货道设置</h4>
            </div>
            <div class="modal-body">
                <!---Tab 选择策略 与 直接设置 切换 start--->
                <div class="tabheader">
                    <ul>
                        <li class="on slt"><span>选择策略</span></li>
                        <c:choose>
                            <c:when test="${fn:length(ids)<2}"><li class="slt"><span>直接设置</span></li></c:when>
                            <c:otherwise><li class="slt undo"><span>直接设置</span></li></c:otherwise></c:choose>
                    </ul>
                </div>
                <!--控制tab切换-->
                <script>
                    $(function(){
                        $('#CtrlTheGoods .tabheader').on('click','.slt',function(){
                            if($(this).hasClass('undo')){
                                layer.msg("多选售货机时无法直接设置，请重新选择。",{shade: [0.5, '#fff'],time:2000,});
                                return false;
                            }else{
                                $(this).addClass('on').siblings().removeClass('on');
                                $('#CtrlTheGoods .modal_commonbody').hide().eq($('#CtrlTheGoods .tabheader .slt').index(this)).show();
                            }
                        });
                    });
                </script>
                <!---Tab 选择策略 与 直接设置 切换 end--->
                <!--选择策略 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
                        <form action="${path}/mtVmChannel/page" name="" method="post" id="st_vm_channel_search_form" class="unKeyCode">
                            <input type="hidden" name="modelId" value="${mtVm.vmModelId}">
                            <section>
                                <strong>策略名称:</strong>
                                <div class="form-group w120 pull-left"><input class="form-control input-sm" name="strategyName" type="text" placeholder="请输入策略名称"></div>
                            </section>
                            <button type="button" class="btn btn-default f12 btn-lg2 mr10" id="st_vm_channel_search_btn">查询</button>
                        </form>
                    </div>
                    <form id="mt_vm_channel_form_st" method="post" action="${path}/mtVmChannel/save/st">
                        <c:forEach items="${ids}" var="vmId" >
                            <input type="hidden" name="ids" value="${vmId}">
                        </c:forEach>
                    <!--table body start-->
                    <div class="main_commontable max400 border_line clearfix" id="st_vm_channel_div">
                        <jsp:include page="/mtVmChannel/page?modelId=${mtVm.vmModelId}" flush="true"></jsp:include>
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
                    <div class="btn-group" data-toggle="buttons">
                        <button type="button" id="relationSku" class="btn btn-default btn-lg2" data-toggle="modal" href="${path}/mtVmChannel/sku" data-target="#Child_choiceTheGoods" data-must="true" li-set="#editChannelDetailDiv li.on input.channelCode">关联商品</button>
                        <button type="button" id="editCancelRelate" class="btn btn-default btn-lg2"  href="${path}/mtVmChannel/cancelRelate">取消关联</button>
                        <button type="button" id="editMergeButton" class="btn btn-default btn-lg2" href="${path}/mtVmChannel/mergeChannel">合并货道</button>
                        <button type="button" id="editCancelMerge" class="btn btn-default btn-lg2"  href="${path}/mtVmChannel/cancelMerge">解除合并</button>
                        <%--<button type="button" class="btn btn-default btn-lg2" href="${path}/mtVmChannel/sort?vmId=${mtVm.id}" data-toggle="modal" data-target="#Child_setTheList">列表页设置</button>--%>
                    </div>
                    <div class="col_thegoods mt15 thegoods v3_2" id="editChannelDetailDiv">
                       ${html}
                    </div>

                    <!--按钮-->
                    <form id="mt_vm_channel_form" method="post" action="${path}/mtVmChannel/save">
                        <input type="hidden" name="vmId" value="${mtVm.id}">
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success btn-lg2" id="submitChangesBtn">应用配置</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                        </form>
                </div>
                <!--直接设置 end-->
            </div>

        </div>
    </div>
<!-----修改货道满量 模态框 start----->
<div class="modal fade" id="setFullSku" tabindex="-1" role="dialog" aria-labelledby="设置满量" aria-hidden="true">

</div>

<!-------------------------------------货道设置：#CtrlTheGoods end------------------------------------------------>
<script>
    $(function(){
        $("#CtrlTheGoods .thegoods").on("click","li", function(){
            $(this).toggleClass("on");
        })
        MtVmChannel.init();
        MtVmChannel.editMergeChannel();
        MtVmChannel.editCancelMerge();
        MtVmChannel.editCancelRelate();
        MtVmChannel.editSubmit();
        Metronic.imgEmpty('editChannelDetailDiv');
        $("[data-toggle='tooltip']").tooltip();
    })

</script>