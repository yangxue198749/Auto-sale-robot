<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-dialog w400" id="deleteModal">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">删除接口</h4>
        </div>
        <div class="modal-body">
            <div class="modal_commonbody clearfix">
                <div class="step_info text-center">
                    <p>是否确认删除该公司接口？</p>
                    <input type="hidden" value="${customerId}" class="customerId">
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2 deleteBtn">确定</button>
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
        </div>
    </div>
</div>
</div>
<script>
    $(function(){
        $('#deleteModal .deleteBtn').click(function(){
            var customerId = $(this).parents('#deleteModal').find('.customerId').val();
            console.log('customerId:'+customerId);
            $.ajax({
                type: "GET",
                url: "/openApi/deleteOpenApiConfirm/",
                data: {customerId:customerId},
                dataType: "json",
                success: function(data){
                    console.log(data);
                    if(data.type == 'success'){
                        layer.msg("操作成功！",{shade: [0.5, '#fff'],time:2000});
                        $('#deleteOpenInterface').modal('hide');
                        location.reload();
                    }else{
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
                        return false;
                    }
                }
            });
        });
    });
</script>
