<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择跳转页</h4>
        </div>
        <div class="modal-body">
            <div class="main_commonhead hl30">
                <form id="selectMtVmResources-form" method="post" action="/mtVmShortcuts/selectMtVmResourcesPage" class="unKeyCode">
                    <input type="hidden" id="selectMtVmResources-shortcutsCode" value="${shortcutsCode}">
                    <section>
                        <strong>策略名称:</strong>
                        <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="name" placeholder="请输入策略名称"></div>
                    </section>
                    <button id="searchMtVmResoutrcesBtn" type="button" class="btn btn-default f12 btn-lg2">查询</button>
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
<script type="text/javascript">
    $(function(){
        $("#searchMtVmResoutrcesBtn").bind("click",function () {
                var options = {
                    type: 'POST',
                    beforeSubmit: function() {
                    },
                    success: function(data) {
                        var hasLogin = Metronic.hasLogin();
                        if(hasLogin==false){
                            window.location.href="/";
                            return;
                        }
                        $("#selectMtVmResourcesDiv").html(data);
                    }
                };
                $("#selectMtVmResources-form").ajaxSubmit(options);
        });
        $("#selectMtVmResourcesBtn").bind("click",function () {
            var check = $("#selectMtVmResourcesDiv table tbody input[type='checkbox']:checked");
            if(check.length != 1){
                layer.msg("请选择一个闪购键跳转页策略",{shade: [0.5, '#fff'],time:1000,});
                return ;
            }
            $("#"+$('#selectMtVmResources-shortcutsCode').val()+"_name").html(check.attr("resourceName"));
            $("#"+$('#selectMtVmResources-shortcutsCode').val()+"_resource_id").val(check.val());
            $("#"+$('#selectMtVmResources-shortcutsCode').val()+"_type").val(2);
            $("#"+$('#selectMtVmResources-shortcutsCode').val()+"_id").val("");
            $('#selectLink').modal('hide');
        });
    });
</script>