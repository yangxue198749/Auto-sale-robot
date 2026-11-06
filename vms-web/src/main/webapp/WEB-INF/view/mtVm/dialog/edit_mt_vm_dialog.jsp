<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal-dialog w400">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">修改售货机</h4>
         </div>
         <form action="/mtVm/editMtVm" id="edit-mtVm-form" method="POST">
         <input name="vmId" type="hidden" value="${ids }">
         <div class="modal-body">
             <!--基本信息 start-->
             <div class="modal_commonbody clearfix">
                 <!--填写基本信息-->
                 <div class="col_info clearfix">
                    <section class="col-md-12">
                        <span>具体位置:</span>
                        <div class="form-group mb5"><textarea class="form-control input-sm" name="vmDesc" type="text" placeholder="请输入具体位置">${mtVm.vmDesc }</textarea></div>
                    </section>
                     <!-- v3.4 打印机设置 start-->
                     <section class="col-md-12" id="DeviceSwitch">
                         <div class="text-right"><span class="cf_green pointer switchbtn">更多选项</span></div>
                         <div class="form-group mb5 devicegroup hide">
                             <p>打印设置：</p>
                             <label class="input-radio style2 mr10">
                                 <input type="radio" name="printer" value="0" ${mtVm.printer eq 0 ? 'checked' : ''}><span>无打印机</span>
                             </label>
                             <label class="input-radio style2">
                                 <input type="radio" name="printer" value="1" ${mtVm.printer eq 1 ? 'checked' : ''}><span>有打印机</span>
                             </label>
                         </div>
                         <div class="form-group mb5 devicegroup hide">
                             <p>售卖设置：</p>
                             <select class="form-control input-sm" name="reserve">
                                 <%--<option value="0" ${reserve == 0 ? 'selected' : ''}>即售机</option>
                                 <option value="1" ${reserve == 1 ? 'selected' : ''}>预定机</option>
                                 <option value="2" ${reserve == 2 ? 'selected' : ''}>地推机</option>--%>
                                 <!-- 20180105 16:30 变更为只读不可选择其他 by tonney -->
                                 <c:choose>
                                     <c:when test="${reserve == 0}">
                                         <option value="0" selected="selected">即售机</option>
                                     </c:when>
                                     <c:when test="${reserve == 1}">
                                         <option value="1" selected="selected">预定机</option>
                                     </c:when>
                                 </c:choose>
                             </select>
                             <p>支付方式：</p>
                             <label class="input-radio style2" style="width: 90px;"><input <c:forEach items="${tMtPayTypes}" var="tMtPay">${tMtPay eq 1201 ? 'checked' : ''}</c:forEach> type="checkbox" name="payType" value="1201" class="input_v4" ><span>支付宝</span></label>
                             <label class="input-radio style2" style="width: 90px;"><input <c:forEach items="${tMtPayTypes}" var="tMtPay">${tMtPay eq 1101 ? 'checked' : ''}</c:forEach> type="checkbox" name="payType" value="1101" class="input_v4" ><span>微信</span></label>
                             <label class="input-radio style2" style="width: 90px;"><input <c:forEach items="${tMtPayTypes}" var="tMtPay">${tMtPay eq 1301 ? 'checked' : ''}</c:forEach> type="checkbox" name="payType" value="1301" class="input_v4" ><span>银联扫码</span></label>
                             <label class="input-radio style2" style="width: 90px;"><input <c:forEach items="${tMtPayTypes}" var="tMtPay">${tMtPay eq 2000 ? 'checked' : ''}</c:forEach> type="checkbox" name="payType" value="2000" class="input_v4" ><span>卡支付</span></label>

                         </div>
                         <%--<div class="form-group checkbox mb5 devicegroup hide"></div>--%>
                     </section>
                     <!-- v3.4 打印机设置 end -->
                 </div>
             </div>
         </div>
         <!--按钮-->
         <div class="modal-footer">
             <button type="button" id="submitEditMtVmBtn" class="btn btn-success btn-lg2">确定</button>
             <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
         </div>
         </form>
         
     </div>
 </div>
 <script type="text/javascript">
$(function(){
	MtVm.editMtVm();
    //打印设置
    $('#DeviceSwitch .switchbtn').click(function(){
        $('#DeviceSwitch .devicegroup').toggleClass('hide');
    })
})
</script>