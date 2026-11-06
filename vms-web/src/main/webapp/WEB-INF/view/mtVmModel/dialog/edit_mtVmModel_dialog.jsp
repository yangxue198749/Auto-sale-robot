<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">修改型号</h4>
        </div>
        <form action="" id="editModel_form">
            <!--基本信息 start-->
            <input name="id" value="${vmModel.id}" type="hidden" >
	        <div class="modal-body">
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">售货机型号:</span>
                        <div class="form-group pull-left">
                            <input name="modelName" type="text" class="form-control input-sm w160" value="${vmModel.modelName}" readonly="readonly" 
                            	required data-msg-required="请输入售货机型号" minlength="2" data-msg-minlength="最少输入2个字符" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="modelType">机型分类:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="modelType" required data-msg-required="请选择机型分类">
                                <option value="">请选择</option>
                                <c:forEach items="${modelType.dictionary}" var="tSysDictionary">
                                    <c:choose>
                                        <c:when test="${vmModel.modelType == tSysDictionary.id}">
                                            <option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">型号描述:</span>
                        <div class="form-group pull-left">
                            <input name="modelDesc" type="text" class="form-control input-sm w160" placeholder="请输入型号描述" value="${vmModel.modelDesc}" 
                            	required data-msg-required="请输入型号描述" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="channelType">货道类型:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="channelType" required data-msg-required="请选择货道类型">
                            	<c:forEach items="${channelType.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${vmModel.channelType == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">展示位数量:</span>
                        <div class="form-group pull-left">
                            <input name="shortcutsQty" type="text" class="form-control input-sm w160" value="${vmModel.shortcutsQty}" readonly="readonly" 
                            	required data-msg-required="请输入展示位数量" digits="true" data-msg-digits="只能输入整数" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">货道层数:</span>
                        <div class="form-group pull-left">
                            <input id="channelLayerQty" name="channelLayerQty" type="text" class="form-control input-sm w160" value="${vmModel.channelLayerQty}" readonly="readonly" 
                            	required data-msg-required="请输入货道层数" digits="true" data-msg-digits="只能输入整数" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">各层货道数:</span>
                        <div class="form-group pull-left">
                            <input id="channelPerLayer" name="channelPerLayer" type="text" class="form-control input-sm w160" value="${vmModel.channelPerLayer}" readonly="readonly" 
                            	required data-msg-required="请输入各层货道数"  >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="allowMerge">货道能否合并:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="allowMerge" disabled="disabled"  required data-msg-required="请选择">
                                <c:choose>
                                	<c:when test="${vmModel.allowMerge == 0}">
                                		<option value="0" selected="selected">是</option>
                                	</c:when>
                                	<c:otherwise>
                                		<option value="1" selected="selected">否</option>
                                	</c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="companyInit">公司缩写:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="companyInit" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${companyInit.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${vmModel.companyInit == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryDesc}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryDesc}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="machineView">售货机外观:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="machineView" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${machineView.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${vmModel.machineView == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>

                    <%--v3.2 新增 保温箱数量 温控说明 start--%>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="tempBoxCount">保温箱数量:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="tempBoxCount" required data-msg-required="请选择">
                                <c:forEach begin="0" end="10" step="1" varStatus="v">
                                    <option value="${v.index}" ${v.index == vmModel.boxCount?'selected':''}>${v.index}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="temperatureHandlerMode">温控模式:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="temperatureHandlerMode" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${temperatureHandlerMode.dictionary}" var="tSysDictionary">
                                    <c:choose>
                                       <c:when test="${vmModel.temperatureHandlerMode == tSysDictionary.id}">
                                            <option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="pull-left w80 text-right mr10" for="tempCtrlDesc">温控说明:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w444" name="tempCtrlDesc" value="${vmModel.tempCtrlDesc}" placeholder="请输入温控说明" maxLength="100"
                                   data-msg-required="请输入温控说明" data-msg-maxlength="说明过长"  >
                        </div>
                    </section>
                    <%--v3.2 新增 保温箱数量 温控说明 end--%>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="combinatorialPattern">组合模式:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="combinatorialPattern"  required data-msg-required="请选择">
                                <option value="">请选择</option>
                                    <option value="1" <c:if test="${vmModel.combinatorialPattern == 1}">selected="selected"</c:if> >单机</option>
                                    <option value="2" <c:if test="${vmModel.combinatorialPattern == 2}">selected="selected"</c:if> >主机</option>
                                    <option value="3" <c:if test="${vmModel.combinatorialPattern == 3}">selected="selected"</c:if> >副机</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6" id="screenHave">
                        <span class="pull-left w80 text-right mr10" for="screenHave">屏有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="screenHave" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <option value="1" <c:if test="${vmModel.screenHave == 1}">selected="selected"</c:if> >有</option>
                                <option value="2" <c:if test="${vmModel.screenHave == 2}">selected="selected"</c:if> >无</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6" id="screenType">
                        <span class="pull-left w80 text-right mr10" for="screenType">屏种类:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="screenType" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${screenType.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${vmModel.screenType == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6" id="screenSize">
                        <span class="pull-left w80 text-right mr10" for="screenSize">屏尺寸:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="screenSize" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${screenSize.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${vmModel.screenSize == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="vendOutArchitecture">出货结构:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="vendOutArchitecture" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${vendOutArchitecture.dictionary}" var="tSysDictionary">
                            		<c:choose>
	                           			<c:when test="${vmModel.vendOutArchitecture == tSysDictionary.id}">
	                           				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:when>
	                           			<c:otherwise>
	                           				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:otherwise>
	                           		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <%-- v3.2 隐藏 温控模式 制冷有无 制冷设备 加热有无 加热设备 云台设备 start
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="temperatureHandlerMode">温控模式:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="temperatureHandlerMode" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${temperatureHandlerMode.dictionary}" var="tSysDictionary">
                            		<c:choose>
                            			<c:when test="${vmModel.temperatureHandlerMode == tSysDictionary.id}">
                            				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:when>
                            			<c:otherwise>
                            				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                            			</c:otherwise>
                            		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="coolingYn">制冷有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="coolingYn" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:choose>
                                	<c:when test="${vmModel.coolingYn == 0}">
                                		<option value="0" selected="selected">有</option>
                                	</c:when>
                                	<c:otherwise>
                                		<option value="1" selected="selected">无</option>
                                	</c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="colingHdid">制冷设备:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="colingHdid" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${coolingHdid.dictionary}" var="tSysDictionary">
                            		<c:choose>
	                           			<c:when test="${vmModel.colingHdid == tSysDictionary.id}">
	                           				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:when>
	                           			<c:otherwise>
	                           				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:otherwise>
	                           		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="hittingYn">加热有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="hittingYn" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:choose>
                                	<c:when test="${vmModel.hittingYn == 0}">
                                		<option value="0" selected="selected">有</option>
                                	</c:when>
                                	<c:otherwise>
                                		<option value="1" selected="selected">无</option>
                                	</c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="hittingHdid">加热设备:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="hittingHdid" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${hittingHdid.dictionary}" var="tSysDictionary">
                            		<c:choose>
	                           			<c:when test="${vmModel.hittingHdid == tSysDictionary.id}">
	                           				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:when>
	                           			<c:otherwise>
	                           				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:otherwise>
	                           		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                     <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="elevatorPlatformHdid">云台设备:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="elevatorPlatformHdid" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${elevatorPlatformHdid.dictionary}" var="tSysDictionary">
                            		<c:choose>
	                           			<c:when test="${vmModel.elevatorPlatformHdid == tSysDictionary.id}">
	                           				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:when>
	                           			<c:otherwise>
	                           				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:otherwise>
	                           		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                     v3.2 隐藏 温控模式 制冷有无 制冷设备 加热有无 加热设备 云台设备 end --%>

                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="elevatorPlatformYn">云台有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="elevatorPlatformYn" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:choose>
                                	<c:when test="${vmModel.elevatorPlatformYn == 0}">
                                		<option value="0" selected="selected">有</option>
                                	</c:when>
                                	<c:otherwise>
                                		<option value="1" selected="selected">无</option>
                                	</c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="vendOutType">出货口:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="vendOutType" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${vendOutType.dictionary}" var="tSysDictionary">
                            		<c:choose>
	                           			<c:when test="${vmModel.vendOutType == tSysDictionary.id}">
	                           				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:when>
	                           			<c:otherwise>
	                           				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:otherwise>
	                           		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="rfidCard">非接卡:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="rfidCard" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${rfidCard.dictionary}" var="tSysDictionary">
                                	<c:choose>
                            			<c:when test="${vmModel.rfidCard == tSysDictionary.id}">
	                           				<option selected="selected" value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:when>
	                           			<c:otherwise>
	                           				<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
	                           			</c:otherwise>
	                           		</c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                </div>
                <!--基本信息 end-->
        	</div>
	        <!--按钮-->
	        <div class="modal-footer">
	            <button type="submit" class="btn btn-success btn-lg2 confirmbtn">提交</button>
	            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
	        </div>
        </form>

    </div>
</div>

<script>
	$(function(){
        initScreen();

       	$("#editModel_form").validate({
	        submitHandler:function(form){
	        	editModel();
	        }    
	    });
        $('#screenHave').on('change','select',function(){
            var _thisSelect = $(this).find('option:selected').val();
            console.log('_thisSelect:'+_thisSelect);
            if(_thisSelect == 2){
                $('#screenType,#screenSize').hide();
            }else{
                $('#screenType,#screenSize').show();
            }
        });

        // initScreen
        function initScreen(){
            var screenHave = $('#screenHave select option:selected');
            if(screenHave.val() == 2){
                $('#screenType,#screenSize').hide();
            }else {
                $('#screenType,#screenSize').show();
            }
        }
	});
</script>