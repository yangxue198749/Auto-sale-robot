<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.vms.tags" prefix="t" %>
<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">新增接口</h4>
        </div>
        <form method="post" action="/openApi/addOpenApiConfirm" name="form" id="add-open-api-form" class="unKeyCode">
            <input type="hidden" name="serviceIds" id="addServiceIds">
            <input type="hidden" name="companyId" id="companyId">
            <div class="modal-body">
                <div class="modal_commonbody clearfix">
                    <div class="clearfix">
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20">开放公司:</span>
                            <div class="form-group pull-left">
                                <button type="button" class="btn btn-default btn-sm2 f12 w160 btn-block" href="/openApi/companyList" data-toggle="modal" data-target="#choiceCompany">选择公司</button>
                                <div class="getCompanyName" data-company-id=""></div>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <span class="pull-left w60 text-right mr20">开放接口:</span>
                            <div class="form-group pull-left openApiItem" data-openApiVal="">
                                <c:forEach items="${openService }" var="service">
                                    <p>
                                        <label><input type="checkbox" class="input_v4 openApiVal" name="" value="${service.id}"><span>${service.serviceDesc}</span></label>
                                    </p>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2" id="btnAddOpenApi">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
    </div>
    </div>
<script type="text/javascript">
    $('#btnAddOpenApi').click(function(){
        var ApiItem = [];
        $('#add-open-api-form .openApiItem input[type="checkbox"]:checked').each(function () {
            var _thisVal = $(this).val();
            ApiItem.push(_thisVal);
        });
        console.log(ApiItem);
        $('#addServiceIds').val(ApiItem);

        var addServiceIds = $('#addServiceIds').val();
        console.log(addServiceIds);
        var companyId = $('#companyId').val();

        if(companyId == ''){
            layer.msg("请选择公司",{shade: [0.5, '#fff'],time:2000});
            return false;
        }
        if(addServiceIds == '' || addServiceIds == undefined || addServiceIds == null){
            layer.msg("请选择接口",{shade: [0.5, '#fff'],time:2000});
            return false;
        }else{
            console.log('addTask()');
            openApi.addOpenApi();
        }
    });
</script>