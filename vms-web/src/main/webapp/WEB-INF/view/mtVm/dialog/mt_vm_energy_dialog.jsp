<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w800">
     <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title f16" id="">节能设置</h4>
         </div>
         <div class="modal-body">
             <!---Tab 选择策略 与 直接设置 切换 start--->
             <div class="tabheader">
                <ul>
                    <li class="on slt"><span>选择策略</span></li>
                     <c:choose>
                            <c:when test="${fn:length(idsArry)<2}">
                           		<li class="slt"><span>直接设置</span></li>
                            </c:when>
                            <c:otherwise>
                            	<li class="slt undo"><span>直接设置</span></li>
                            </c:otherwise>
                     </c:choose>
                </ul>
             </div>
             <input name="mtVmEnergySaveMtVmIds" id="mtVmEnergySaveMtVmIds"  type="hidden" value="${ids }">
             <!--选择策略 start-->
             <div class="modal_commonbody clearfix">
                 <div class="main_commonhead hl30">
                     <form action="${path }/mtVmEnergySave/listPage" method="POST" id="searchStEnergySave-form">
                         <section>
                             <strong>售货机型号:</strong>
                             <div class="form-group w120 pull-left">
                                 <select class="form-control input-sm" name="vmModelId">
			                     	 <option value="${mtVm.vmModelId}" selected="selected">${u:getModelName(mtVm.vmModelId)}</option>
                                 </select>
                             </div>
                         </section>
                         <section>
                             <strong>策略名称:</strong>
                             <div class="form-group w120 pull-left"><input class="form-control input-sm" name="stName" type="text" placeholder="请输入策略名称"></div>
                         </section>
                         <button type="button" id="searchStEnergySave-btn" class="btn btn-default f12 btn-lg2 mr10">查询</button>
                     </form>
                 </div>
                 <!--table body start-->
                 <div class="main_commontable max400 border_line clearfix" id="energyStDiv">
                     <jsp:include page="/mtVmEnergySave/listPage?vmModelId=${mtVm.vmModelId}" flush="true"/>
                 </div>
                 <!--table body end-->
                 <!--按钮-->
                 <div class="modal-footer">
                     <button type="button" id="selectStEnergySaveBtn"class="btn btn-success btn-lg2">确定应用</button>
                     <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                 </div>
             </div>
             <!--选择策略 end-->
             <!--直接设置 start-->
             <div class="modal_commonbody clearfix" style="display:none">
                    <div class="main_commonhead hl30">
                         <div class="btn-group" data-toggle="buttons">
                             <button type="button" class="btn btn-default btn-lg2" href="${path }/mtVmEnergySave/add?vmId=${mtVm.id}" data-toggle="modal" data-target="#Child_addTheEnergy">新增</button>
                             <button type="button" class="btn btn-default btn-lg2" id="deleteEnergySaveBtn" href="${path }/mtVmEnergySave/delete" data-must="true" data-set="#mtVmEnergySaveDiv table tbody input[type='checkbox']:checked">删除</button>
                             <button type="button" class="btn btn-default btn-lg2" href="${path }/mtVmEnergySave/update?vmId=${mtVm.id}" data-must="true" select-one="true" data-set="#mtVmEnergySaveDiv table tbody input[type='checkbox']:checked" data-toggle="modal" data-target="#Child_editTheEnergy">修改</button>
                         </div>
                    </div>
                     <div class="main_commontable max400 border_line" id="mtVmEnergySaveDiv">
                         <table>
                              <thead>
                                  <tr>
                                     <td class="wh3"><input type="checkbox" value=""></td>
                                     <td class="wh17">节能类型</td>
                                     <td class="wh20">开始时间</td>
                                     <td class="wh20">结束时间</td>
                                      <td class="wh20">保温箱编号</td>
                                     <td class="wh20">温度目标值</td>
                                  </tr>
                              </thead>
                              <tbody class="auto">
	                              ${html }
                              </tbody>
                         </table>
                     </div>
                    
                     <!--按钮-->
                     <div class="modal-footer">
                         <button type="button" id="saveEnergyBtn" class="btn btn-success btn-lg2">确定应用</button>
                         <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                     </div>
             </div>
             <!--直接设置 end-->
         </div>
                    
     </div>
 </div>
 <script>
	$(function(){
		MtVmEnergySave.init();
		Metronic.singleCheck('.singleCheck td');
        $('#CtrlTheEnergy .tabheader').on('click','.slt',function(){
            if($(this).hasClass('undo')){
                layer.msg("多选售货机时无法直接设置，请重新选择。",{shade: [0.5, '#fff'],time:2000});
                return false;
            }else{
                $(this).addClass('on').siblings().removeClass('on');
                $('#CtrlTheEnergy .modal_commonbody').hide().eq($('#CtrlTheEnergy .tabheader .slt').index(this)).show();
            }
        });
    });
</script>