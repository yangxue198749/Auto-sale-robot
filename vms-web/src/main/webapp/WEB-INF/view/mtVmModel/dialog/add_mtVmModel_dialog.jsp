<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新建型号</h4>
        </div>
        <form action="" id="addModel_form">
            <!--基本信息 start-->
	        <div class="modal-body">
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">售货机型号:</span>
                        <div class="form-group pull-left">
                            <input name="modelName" type="text" class="form-control input-sm w160" placeholder="请输入售货机型号"
                            	required data-msg-required="请输入售货机型号" minlength="2" data-msg-minlength="最少输入2个字符" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="modelType">机型分类:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="modelType" required data-msg-required="请选择机型分类">
                                <option value="">请选择</option>
                                <c:forEach items="${modelType.dictionary}" var="tSysDictionary">
                                    <option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">型号描述:</span>
                        <div class="form-group pull-left">
                            <input name="modelDesc" type="text" class="form-control input-sm w160" placeholder="请输入型号描述" 
                            	required data-msg-required="请输入型号描述" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="channelType">货道类型:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="channelType" required data-msg-required="请选择货道类型">
                               	<option value="">请选择</option>
                            	<c:forEach items="${channelType.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">展示位数量:</span>
                        <div class="form-group pull-left">
                            <input name="shortcutsQty" type="text" class="form-control input-sm w160" placeholder="请输入展示位数量" 
                            	required data-msg-required="请输入展示位数量" digits="true" data-msg-digits="只能输入正整数" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">货道层数:</span>
                        <div class="form-group pull-left">
                            <input id="channelLayerQty" name="channelLayerQty" type="text" class="form-control input-sm w160" placeholder="请输入货道层数" 
                            	required data-msg-required="请输入货道层数" digits="true" data-msg-digits="只能输入整数" 
                            	min="1" data-msg-min="货道层数不能小于1" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10">各层货道数:</span>
                        <div class="form-group pull-left">
                            <input id="channelPerLayer" name="channelPerLayer" type="text" class="form-control input-sm w160" placeholder="请输入各层货道数"
                            	required data-msg-required="请输入各层货道数"  >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="allowMerge">货道能否合并:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="allowMerge"  required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <option value="0">是</option>
                                <option value="1">否</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="companyInit">公司缩写:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="companyInit" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${companyInit.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryDesc}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="machineView">售货机外观:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="machineView" required data-msg-required="请选择售货机外观">
                                <option value="">请选择</option>
                                <c:forEach items="${machineView.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>

                    <%--v3.2 新增 保温箱数量 温控说明 start--%>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="tempBoxCount">保温箱数量:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="tempBoxCount" required data-msg-required="请选择保温箱数量">
                                <c:forEach begin="0" end="10" step="1" varStatus="v">
                                    <option value="${v.index}">${v.index}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="temperatureHandlerMode">温控模式:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="temperatureHandlerMode" required data-msg-required="请选择温控模式">
                                <option value="">请选择</option>
                                <c:forEach items="${temperatureHandlerMode.dictionary}" var="tSysDictionary">
                                    <option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="pull-left w80 text-right mr10" for="tempCtrlDesc">温控说明:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w444" name="tempCtrlDesc" placeholder="请输入温控说明" maxLength="100"
                                    data-msg-required="请输入温控说明" data-msg-maxlength="说明过长" >
                        </div>
                    </section>
                    <%--v3.2 新增 保温箱数量 温控说明 end--%>

                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="combinatorialPattern">组合模式:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="combinatorialPattern"  required data-msg-required="请选择组合模式">
                                <option value="">请选择</option>
                                <option value="1">单机</option>
                                <option value="2">主机</option>
                                <option value="3">副机</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6" id="screenHave">
                        <span class="pull-left w80 text-right mr10" for="allowMerge">屏有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="screenHave" required data-msg-required="请选择屏有无">
                                <option value="">请选择</option>
                                <option value="1">有</option>
                                <option value="2">无</option>
                            </select>
                        </div>
                    </section>
                    <script>
                        $(function(){
                            var screenHave = $('#screenHave select option:selected');
                            if(screenHave.val() == 2){
                                $('#screenType,#screenSize').hide();
                            }
                            $('#screenHave select').change(function(){
                               var _thisSelect = $(this).find('option:selected');
                               if(_thisSelect.val() == 2){
                                   $('#screenType,#screenSize').hide();
                               }else{
                                   $('#screenType,#screenSize').show();
                               }
                            });
                        })
                    </script>
                    <section class="col-md-6" id="screenType">
                        <span class="pull-left w80 text-right mr10" for="screenType">屏种类:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="screenType" required data-msg-required="请选择屏种类">
                                <option value="">请选择</option>
                                <c:forEach items="${screenType.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6" id="screenSize">
                        <span class="pull-left w80 text-right mr10" for="screenSize">屏尺寸:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="screenSize" required data-msg-required="请选择屏尺寸">
                                <option value="">请选择</option>
                                <c:forEach items="${screenSize.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="vendOutArchitecture">出货结构:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="vendOutArchitecture" required data-msg-required="请选择出货结构">
                                <option value="">请选择</option>
                                <c:forEach items="${vendOutArchitecture.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
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
                                    <option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="coolingYn">制冷有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="coolingYn" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <option value="0">有</option>
                                <option value="1">无</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="colingHdid">制冷设备:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="colingHdid" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${coolingHdid.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="hittingYn">加热有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="hittingYn" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <option value="0">有</option>
                                <option value="1">无</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="hittingHdid">加热设备:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="hittingHdid" required data-msg-required="请选择">
                                <option value="">请选择</option>
                                <c:forEach items="${hittingHdid.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
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
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                     v3.2 隐藏 温控模式 制冷有无 制冷设备 加热有无 加热设备 云台设备 end --%>

                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="elevatorPlatformYn">云台有无:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="elevatorPlatformYn" required data-msg-required="请选择云台有无">
                                <option value="">请选择</option>
                                <option value="0">是</option>
                                <option value="1">否</option>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="vendOutType">出货口:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="vendOutType" required data-msg-required="请选择出货口">
                                <option value="">请选择</option>
                                <c:forEach items="${vendOutType.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w80 text-right mr10" for="rfidCard">非接卡:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w160" name="rfidCard" required data-msg-required="请选择非接卡">
                                <option value="">请选择</option>
                                <c:forEach items="${rfidCard.dictionary}" var="tSysDictionary">
                            		<option value="${tSysDictionary.id}">${tSysDictionary.dictionaryName}</option>
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
	
	// 页面加载完成后执行的方法
	$(function(){
       	$("#addModel_form").validate({
	        submitHandler:function(form){
	        	addModel();
	        }    
	    });
	});

</script>