<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">配置配送员</h4>
        </div>
        <div class="modal-body">
            <form id="delivery-form-search" action="${path}/mtNode/deliveryList" method="POST">
	            <c:forEach items="${ids}" var="id">
	                <input type="hidden" value="${id}" name="ids">
	            </c:forEach>
            <div class="main_commonhead">
                <section>
                    <strong>用户姓名:</strong>
                    <div class="form-group w120 pull-left">
                        <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入用户姓名" name="userName"></div>
                    </div>
                </section>
                <button type="button" class="btn btn-default f12 btn-lg2 mr10" id="delivery-search-button">查询</button>
                <div class="clearfix"></div>
            </div>
                </form>
            <form id="delivery-form" action="${path}/mtNode/delivery" method="POST">
                <c:forEach items="${ids}" var="id">
                    <input type="hidden" value="${id}" name="ids">
                </c:forEach>
            <div class="main_commontable max400 border_line">
                <table>
                    <thead class="auto">
                    <tr>
                        <td><span>选择</span></td>
                        <td><span>用户姓名</span></td>
                        <td><span>手机</span></td>
                    </tr>
                    </thead>
                    <tbody class="auto singleCheck">

                        <c:forEach items="${list}" var="user">
                            <tr>
                        <td><span><input type="checkbox" value="${user.id}" name="deliveryId"></span></td>
                        <td><span>${user.userName}</span></td>
                        <td><span>${user.userPhone}</span></td>
                    </tr>
                        </c:forEach>

                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="submit" class="btn btn-success" >确定</button>
            </div>
            </form>
        </div>

    </div>
</div>
<script>
    $(".singleCheck td").on("click","input[type='checkbox']",function(){
        if($(this).prop("checked")){
            $(this).parents().siblings().find("input[type='checkbox']").prop("checked",false);
        }
    });
    MtNode.addDelivery();
</script>