<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w1000">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">选择商品</h4>
        </div>
            <div class="modal-body">
                <!--货道设置信息 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
                        <form action="${path}/mtVmShortcuts/sku/page" name="" id="st_vm_shortcuts_sku_search_form" method="post">
                            <section>
                                <strong>商品分类:</strong>
                                <div class="form-group w120 pull-left mr10">
                                    <select class="form-control input-sm" id="typeGroupAdd" name="skuGroupTypeId" onchange="refreshRenderType()">
                                    </select>
                                </div>
                                <div class="form-group w120 pull-left" >
                                    <select class="form-control input-sm" id="typeAdd" name="skuTypeId">
                                    </select>
                                </div>
                            </section>
                            <section>
                                <strong>商品名称:</strong>
                                <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="skuName" placeholder="请输入商品名称"></div>
                            </section>
                            <section>
                                <strong>商品编号:</strong>
                                <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="skuCode" placeholder="请输入商品编号"></div>
                            </section>
                            <button type="button" class="btn btn-default f12 btn-lg2 mr10" id="st_vm_shortcuts_sku_search_btn">查询</button>
                        </form>
                    </div>
                    <form>
                        <input id="sku_table_shortcuts_code" type="hidden" value="${shortcutsCode}">
                    <!--商品 box start-->
                    <div class="col_relation mt15 small choiceTheGoods" id="st_vm_shortcuts_sku_div">
                        <jsp:include page="/mtVmShortcuts/sku/page" flush="true"></jsp:include>
                    </div>
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal" id="sku_submit">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                    <!--商品 box end-->
                    </form>
                    <script>
                        $(function(){
                            $(".choiceTheGoods").on("click","li", function(){
                                $(this).addClass("on").siblings().removeClass("on");
                            });
                            $("#sku_submit").on("click",function(){
                                var skuId=$("#st_vm_shortcuts_sku_div").find(".on input[name=skuId]").val();
                                if(null==skuId){
                                    layer.msg("请先选择商品",{shade: [0.5, '#fff'],time:2000});
                                    return false;
                                }
                                var skuName=$("#st_vm_shortcuts_sku_div").find(".on input[name=skuName]").val();
                                var shortcutsCode=$("#sku_table_shortcuts_code").val();
                                $('#'+shortcutsCode+'_id').val(skuId);
                                $('#'+shortcutsCode+'_name').html(skuName);
                                $("#"+shortcutsCode+"_type").val(1);
                                $('#Child_choiceTheGoods').modal('hide') ;
                            });
                        })
                    </script>
                </div>
            </div>
    </div>
</div>

<script type="text/javascript" src="${path}/vmsjs/skuType.js"></script>
<script type="text/javascript">
    var sku = '${u:getSkuJson()}';
    // 将字符串转成JSON对象
    var skuJson = eval("(" + sku + ")");
    $(function(){
        renderTypeGroup();
        var groupId = $("#typeGroupAdd").val();
        renderType(groupId);
        MtVmShortcuts.searchSku();
    });
</script>