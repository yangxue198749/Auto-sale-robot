<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">修改接口</h4>
        </div>
        <form method="post" action="/openApi/updateOpenApiConfirm" name="form" id="edit-open-api-form" class="unKeyCode">
            <input type="hidden" name="serviceIds" id="editServiceIds">
            <input type="hidden" name="customerId" value=${customerId}>
            <div class="modal-body">
                <div class="modal_commonbody clearfix">
                    <div class="clearfix">
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20">开放公司:</span>
                            <div class="form-group pull-left">
                                <p><span class="mr5">${companyName}</span></p>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20">开放接口:</span>
                            <div class="form-group pull-left openApiItem">
                            <c:forEach items="${openService }" var="service">
                                <p>
                                    <c:choose>
                                        <c:when test="${fn:contains(serviceId,service.id)}">
                                            <label><input type="checkbox" checked class="input_v4 openApiVal" name="" value="${service.id}"><span>${service.serviceDesc}</span></label>
                                        </c:when>
                                        <c:otherwise>
                                            <label><input type="checkbox" class="input_v4 openApiVal" name="" value="${service.id}"><span>${service.serviceDesc}</span></label>
                                        </c:otherwise>
                                    </c:choose>

                                </p>
                            </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2" id="btnEditOpenApi">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    $('#btnEditOpenApi').click(function(){
        var ApiItem = [];
        $('#edit-open-api-form .openApiItem input[type="checkbox"]:checked').each(function () {
            var _thisVal = $(this).val();
            ApiItem.push(_thisVal);
        });
        console.log(ApiItem);
        $('#editServiceIds').val(ApiItem);

        var editServiceIds = $('#editServiceIds').val();
        console.log(editServiceIds);
//        var companyId = $('#companyId').val();
//
//        if(companyId == ''){
//            layer.msg("请选择公司",{shade: [0.5, '#fff'],time:2000});
//            return false;
//        }
        if(editServiceIds == '' || editServiceIds == undefined || editServiceIds == null){
            layer.msg("请选择接口",{shade: [0.5, '#fff'],time:2000});
            return false;
        }else{
            console.log('editOpenApi()');
            openApi.editOpenApi();
        }
    });
</script>
