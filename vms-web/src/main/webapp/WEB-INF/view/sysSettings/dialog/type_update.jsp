<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新建类型</h4>
        </div>
        <form id="strategy-form" action="${path}/settings/save" method="POST">
            <div class="modal-body">
                <input type="hidden" name="pid" value="0">
                <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-12">
                        <span class="w60 pull-left text-right mr10">类型key:</span>
                        <div class="form-group pull-left w220">
                            <input type="text" class="form-control input-sm" name="key" required minlength="2">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w60 pull-left text-right mr10">类型描述:</span>
                        <div class="form-group pull-left w220">
                            <input type="text" class="form-control input-sm" name="name" minlength="2">
                        </div>
                    </section>
                </div>
                <!--基本信息 end-->
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2 confirmbtn">提交</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
</div>


<script type="text/javascript">
    $(function () {
        SysSetting.updateType();
    })
</script>