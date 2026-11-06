<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w400">
  <form method="post" action="${path}/user/editUser"  id="edit-user-form">
    <input type="hidden" name="userId" value="${userObj.id}" >
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <%--<h4 class="modal-title f16" id="">编辑用户</h4>--%>
        <h4 class="modal-title f16"><c:choose><c:when test="${ empty userObj}">新增</c:when><c:otherwise>修改</c:otherwise></c:choose>用户</h4>
      </div>
      <div class="modal-body">
        <div class="modal_commonbody clearfix">
        <!--填写基本信息-->
          <div class="step_info">
            <div class="col-md-12 form-group">
              <span class="pull-left w60 text-right mr20">登录账号:</span>
              <input type="text" class="w220 form-control input-sm" placeholder="请输入账号" name="userAccount" value="${userObj.userAccount}" <c:if test="${!empty userObj}">readonly</c:if>>
            </div>
            <div class="col-md-12 form-group">
              <span class="pull-left w60 text-right mr20">用户姓名:</span>
              <input type="text" class="w220 form-control input-sm" placeholder="请输入用户姓名" value="${userObj.userName}" name="userName">
            </div>
            <div class="col-md-12 form-group">
              <span class="pull-left w60 text-right mr20">手机号码:</span>
              <input type="text" class="w220 form-control input-sm" placeholder="请输入手机号码" value="${userObj.userPhone}" name="userPhone">
            </div>
            <div class="col-md-12 form-group">
              <span class="pull-left w60 text-right mr20">所属公司:</span>
              <select class="form-control input-sm w220" name="companyId">
              	<option value="${companyObj.id}" selected>${companyObj.companyName}</option>
              </select>
            </div>
            <div class="col-md-12 form-group">
                <span class="pull-left w60 text-right mr20">所属角色:</span>
                <button type="button" class="btn btn-default btn-sm2 btn-block pull-left w220" href="${path}/user/editRolePage?userId=${userObj.id}" data-toggle="modal" data-target="#edit-role-div">选择角色</button>
            </div>
            <div class="col-md-12">
                <div class="role_textwrap" id="role-ids-text"></div>
                <input type="hidden" id="roleIdStr" name="roleIdStr" value="${roleIdStr}" >
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
        User.editUser();
    })
</script>
