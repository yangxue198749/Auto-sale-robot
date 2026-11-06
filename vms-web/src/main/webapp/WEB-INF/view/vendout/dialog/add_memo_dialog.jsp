<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!--datepicker 扩展插件-->
<link href="${path}/plus/datepicker/css/foundation-datepicker.css" rel="stylesheet" type="text/css">
<script src="${path}/plus/datepicker/js/foundation-datepicker.js"></script>
<script src="${path}/plus/datepicker/js/locales/foundation-datepicker.zh-CN.js"></script>
<script src="${path}/plus/datepicker/js/function.datepicker.js"></script>
    <div class="modal-dialog w400">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">备注</h4>
            </div>
            <form method="post" action="/vendout/memo" name="form" id="add-memo-form" class="unKeyCode">
                <div class="modal-body">
                    <div class="modal_commonbody clearfix">
                        <div class="clearfix">
                            <div class="col-md-12">
                                <div class="form-group textSum">
                                    <input name="detailId" value="${detail.id}" type="hidden">
                                    <textarea class="form-control input-sm" name="memo" value="" placeholder="请输入备注描述" required maxlength="100">${detail.memo}</textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2">确定</button>
                    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                </div>
            </form>

        </div>
    </div>
 <script type="text/javascript">
$(function(){
    Vendout.addRemarkModel();
})
</script>