<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-dialog w500">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">关联程序</h4>
        </div>
        <input id="selectVersionType" value="${type}" type="hidden">
        <div class="modal-body">
            <div id="selectVersionDiv" class="main_commontable max300 border_line">
                <jsp:include page="/vmBoxUpgrade/selectVersionPage?type=${type}" flush="true"/>
            </div>
            <!--table footer end-->
            <div class="clearfix"></div>
        </div>
        <div class="modal-footer">
            <button id="selectVersionBtn" type="button" class="btn btn-success btn-lg2">确定</button>
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function(){
        VmBoxUpgrade.selectVersion();
    })
</script>