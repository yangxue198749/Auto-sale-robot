<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">查看列表页</h4>
        </div>
        <div class="modal-body">
                <!--货道设置信息 start-->
                <div class="modal_commonbody clearfix">
                    <!--商品 box start-->
                    <div class="col_relation mt15 small" style="border: 0; padding: 0 10px; margin-top: 0;">
                         ${html }
                    </div>
                    <!--商品 box end-->

                </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">关闭</button>
        </div>
    </div>
</div>

<script>
    $(function(){
        $("[data-toggle='tooltip']").tooltip();
        $('.col_relation li i').remove();
    });
</script>