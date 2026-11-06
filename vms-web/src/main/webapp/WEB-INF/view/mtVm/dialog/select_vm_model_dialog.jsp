<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <div class="modal-dialog w800">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">选择售货机型号</h4>
            </div>
            <div class="modal-body">
                <div class="main_commontable max300 border_line">
                    <table>
                        <thead>
                        <tr>
                            <td class="wh5"></td>
                            <td class="wh20">售货机型号</td>
                            <td class="wh10">机型分类</td>
                            <td class="wh20">型号描述</td>
                            <td class="wh15">货道类型</td>
                            <td class="wh15">货道层数</td>
                            <td class="wh15">各层货道数</td>
                        </tr>
                        </thead>
                        <tbody class="auto">
                            <c:if test="${! empty vmModelList}">
                                <c:forEach items="${vmModelList}" var="vmModel">
                                    <tr>
                                        <td class="wh5"><input type="checkbox" value="${vmModel.id}" selectVmModelName="${vmModel.modelName}"></td>
                                        <td class="wh20">${vmModel.modelName}</td>
                                        <td class="wh10">${u:getDictNameById(vmModel.modelType)}</td>
                                        <td class="wh20">${vmModel.modelDesc}</td>
                                        <td class="wh15">${u:getDictNameById(vmModel.channelType)}</td>
                                        <td class="wh15">${vmModel.channelLayerQty}</td>
                                        <td class="wh15">${vmModel.channelPerLayer}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2" id="select-vm-model-btn">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>
<script type="text/javascript">
    $(function(){
        MtVm.selectVmModel();
    })
</script>

