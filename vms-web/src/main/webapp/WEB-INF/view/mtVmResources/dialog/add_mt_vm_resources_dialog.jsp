<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-dialog w500">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新增跳转页</h4>
        </div>
        <form id="add-mt-vm-resources-form" action="/mtVmResources/addMtVmResource" type="POST" class="unKeyCode">
            <div class="modal-body">
                <div class="modal-body">
                    <div class="modal_commonbody mt10 clearfix">
                        <section class="col-md-12">
                            <span class="pull-left text-right w100 mr10">跳转页名称:</span>
                            <div class="form-group pull-left w240">
                                <input type="text" id="addMtVmResourcesName" name="mtVmResourceName" class="form-control input-sm" placeholder="请输入跳转页名称">
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span class="pull-left text-right w100 mr10">跳转页描述:</span>
                            <div class="form-group pull-left w240">
                                <textarea name="mtVmResourceDesc" class="form-control input-sm" placeholder="请输入跳转页描述"></textarea>
                            </div>
                        </section>
                        <section class="col-md-12">
                            <span class="pull-left text-right w100 mr10">跳转页包:</span>
                            <div class="form-group pull-left w240">
                                <div class="btn btn-default fileinput fileinput-add-png w240">
                                    <span class="f12">点击上传文件</span>
                                    <input type="file" id="mtVmResourcesZip" name = "mtVmResourcesZip" accept="application/x-zip-compressed" class="uploadFile uploadFile-add-png">
                                </div>
                                <p>格式：zip，大小：10M以内</p>
                            </div>
                        </section>
                    </div>

                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2">提交</button>
                <button type="reset" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>

    </div>
</div>

<script type="text/javascript">
    // 页面加载完成后执行的方法
    MtVmResources.addMtVmResources();
</script>
