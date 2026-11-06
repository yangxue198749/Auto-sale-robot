<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新建配置</h4>
        </div>
        <form id="setting-form" action="${path}/settings/save" method="POST">
            <c:if test="${!empty sysSetting}">
                <input type="hidden" name="sysSettingId" value="${sysSetting.id}"/>
            </c:if>
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">类型:</span>
                        <div class="form-group pull-left w220">
                            <select class="form-control input-sm" name="pid" id="pid">
                                <c:forEach items="${typeList}" var="type">
                                    <option value="${type.id}" ${pid == type.id?'selected':''}>${type.key}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">系统配置key:</span>
                        <div class="form-group pull-left w220">
                            <input type="text" class="form-control input-sm" name="key" required value="${sysSetting.key}">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">系统配置value:</span>
                        <div class="form-group pull-left w220">
                            <input type="text" class="form-control input-sm" name="value" required value="${sysSetting.value}">
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w80 pull-left text-right mr10">系统配置描述:</span>
                        <div class="form-group pull-left w220">
                            <input type="text" class="form-control input-sm" name="name" value="${sysSetting.name}">
                        </div>
                    </section>
                </div>
                <!--基本信息 end-->
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2">提交</button>
                <button type="reset" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        SysSetting.updateSetting();
    })
</script>