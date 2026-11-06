<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">拓展售货机</h4>
        </div>
        <form action="/mtVm/extendMtVm" id="add-mtvm-form" method="POST">
            <input name="copyMtVmId" type="hidden">
            <input name="vmModelId" type="hidden">
            <input name="mainVmId" value="${mainVmId}" type="hidden">
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody clearfix">
                    <!--填写基本信息-->
                    <div class="col_info clearfix">
                        <section class="col-md-12">
                            <span class="text-right mr10">售货机型号:<span class="vmModelName"></span></span>
                            <div class="form-group mb5">
                                <button type="button" class="btn btn-default btn-sm2 f12 btn-block" data-toggle="modal" href="/mtVm/selectExtendVmModelDialog" data-target="#selectVmModel">选择型号</button>
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span>复制策略:<span class="copyInnerCode"></span></span>
                            <div class="form-group mb5">
                                <button type="button" class="btn btn-default btn-sm2 f12 btn-block" href="/mtVm/selectCopyMtVmDialog" data-toggle="modal" data-target="#Child_copyMatStrategy">复制设备策略</button>
                            </div>
                        </section>
                    </div>
                </div>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="button" id="submitAddMtVmBtn" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function(){
        MtVm.extendMtVm();
    })
</script>