<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <form method="post" action="${path}/role/editRole" name="form" id="edit-role-form">
    <input type="hidden" name="roleId" value="${role.id}" >
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">编辑角色</h4>
            </div>
            <div class="modal-body">
                <!--新建角色 start-->
                <div class="modal_commonbody clearfix">
                    <!--填写基本信息-->
                    <div class="step_info">
                        <div class="col-md-12 mt20">
                            <span class="pull-left w60 text-right mr20">角色名称:</span>
                            <input type="text"class="w220 form-control input-sm" placeholder="请输入角色名称" name="roleName" value="${role.roleName}">
                        </div>
                        <div class="col-md-12 mt20">
                            <span class="pull-left w60 text-right mr20">是否共享:</span>
                            <select class="pull-left w220 form-control input-sm" name="share">
                                <option value="">请选择</option>
                                <option value="1" >是</option>
                                <option value="2" >否</option>
                            </select>
                        </div>

                        <div class="col-md-12 mt20">
                            <span class="pull-left w60 text-right mr20">角色描述:</span>
                            <textarea type="text"class="pull-left w220  form-control input-sm mr20" style="height: 200px" name="roleDesc">${role.roleDesc}</textarea>
                        </div>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2">完成</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript">
    $(function () {
        Role.editRole();
    })
</script>
