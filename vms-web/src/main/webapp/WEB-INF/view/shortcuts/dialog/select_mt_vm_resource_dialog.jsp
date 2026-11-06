<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择跳转页</h4>
        </div>
        <div class="modal-body">
            <div class="modal_commonbody clearfix">
                <div class="main_commonhead hl30">
                    <form class="unKeyCode" id="search-resource-form" method="post" action="/mtVmShortcuts/selectMtVmResourcesPage">
                        <input id="resource_table_shortcuts_code" type="hidden" value="${shortcutsCode}">
                        <section>
                            <strong>策略名称:</strong>
                            <div class="form-group w120 pull-left"><input name="name" class="form-control input-sm" type="text" placeholder="请输入策略名称"></div>
                        </section>
                        <button type="button" id="search-resource-btn" class="btn btn-default f12 btn-lg2">查询</button>
                    </form>
                </div>
                <!--table body start-->
                <div class="main_commontable max400 border_line clearfix">
                    <div id="selectMtVmResourcesDiv">
                        <jsp:include page="/mtVmShortcuts/selectMtVmResourcesPage" flush="true"/>
                    </div>
                </div>
                <!--table body end-->
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" id="selectMtVmResourcesBtn" class="btn btn-success btn-lg2">确定</button>
                    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function(){

        $("#search-resource-btn").bind("click",function () {
            var options = {
                type: 'POST',
                beforeSubmit: function() {
                    // Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success: function(data) {
                    $("#selectMtVmResourcesDiv").html(data);
                }
            };
            $("#search-resource-form").ajaxSubmit(options);
        });
        $("#selectMtVmResourcesBtn").bind("click",function () {
            var check = $("#selectMtVmResourcesDiv table tbody input[type='checkbox']:checked");
            if(check.length != 1){
                layer.msg("请选择一个闪购键跳转页策略",{shade: [0.5, '#fff'],time:1000,});
                return ;
            }
            var resourceName=check.attr("resourceName");
            var shortcutsCode=$("#resource_table_shortcuts_code").val();
            var tp = $('#select-sku-div').attr('tp');
            $('#'+shortcutsCode+'_id_add').val("");
            if (tp == 'add') {
                $('#'+shortcutsCode+'_id_add').val("");
                $('#'+shortcutsCode+'_bind_type_add').val(2);
                $('#'+shortcutsCode+'_resource_Id_add').val(check.val());

                $('#'+shortcutsCode+'_name_add').html(resourceName);
                $('#'+shortcutsCode+'_img_add').attr("src","");
            } else {
                $('#'+shortcutsCode+'_id_edit').val("");
                $('#'+shortcutsCode+'_bind_type_edit').val(2);
                $('#'+shortcutsCode+'_resource_Id_edit').val(check.val());

                $('#'+shortcutsCode+'_name_edit').html(resourceName);
                $('#'+shortcutsCode+'_img_edit').attr("src","");
            }
            $('#selectLink').modal('hide');
        });
    });
</script>