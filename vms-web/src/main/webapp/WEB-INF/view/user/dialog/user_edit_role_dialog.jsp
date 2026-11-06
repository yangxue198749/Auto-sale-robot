<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">选择角色</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable max300">
              <c:choose>
              	<c:when test="${empty roleAllList}">
                    <div>
                        <section style="padding:100px 0; text-align:center;">
                            <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                            <p>暂无内容</p>
                        </section>
                    </div>
              		<%--<tr>
              			<td style="padding:100px 0; text-align: center;" colspan="4">
              				<p>
              					<i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i>
              				</p>
              				<p>暂无内容</p>
              			</td>
              		</tr>--%>
              	</c:when>
              	<c:otherwise>
              	<table>
                    <tbody class="auto">
                        <tr>
                          <c:forEach items="${roleAllList}" varStatus="status" var="role">
                          <td style="text-align:left;"><input type="checkbox" class="input_v4" id="role_cb_${role.id}" name="roleIds" value="${role.id}" roleName="${role.roleName}">${role.roleName}</td>
                          <c:if test="${status.count % 4 == 0}">
                            </tr> <tr>
                          </c:if>
                          </c:forEach>
                        </tr>
              		</tbody>
              	</table>
              	</c:otherwise>
              </c:choose>
            </div>
        </div>
        <div class="modal-body">
            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-lg2 confirmbtn" id="role_submit">确定</button>
                <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $('#role_submit').bind('click', function() {
      var cb = $("#edit-role-div table tbody input[type='checkbox']:checked");
      var roleIds = new Array();
      var roleNames = new Array();
      cb.each(function () {
          roleIds.push($(this).val());
          roleNames.push($(this).attr('roleName'));
      });
      var roleIdStr   = roleIds.join(',');
      var roleNameStr = roleNames.join('; ');
      $("#role-ids-text").html(roleNameStr);
      $("#roleIdStr").val(roleIdStr);
      $('#edit-role-div').modal('hide');
    });

    $(function() {
      var idListStr = $('#roleIdStr').val();
      var idsArray = idListStr.split(",");
      for (var i = 0; i < idsArray.length; i++) {
        $('#role_cb_' + idsArray[i]).attr("checked",true);
      }
    })
</script>
