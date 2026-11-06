<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal-dialog w400">
    <form id="editSkuFullForm" name="form" class="unKeyCode">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">设置满量</h4>
            </div>
            <div class="modal-body">
                <input type="hidden" name="channelId" value="${channelId}">
                <div class="modal_commonbody clearfix">
                    <div class="col-md-12">
                        <span class="pull-left w60 text-right mr20">新满量:</span>
                        <div class="form-group pull-left textSum">
                            <input type="text" class="w220 form-control input-sm" name="fullval" placeholder="请输入正整数" required maxlength="3">
                        </div>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-lg2 editbtn">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript">
    $(function(){
        StVmChannel.editFullSku();
    })
</script>