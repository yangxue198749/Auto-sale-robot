<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script src="${path}/plus/datetimepicker/function.datetimepicker.js"></script>
<div class="modal-dialog w800">
    <div class="modal-content">
        <form id="strategy-form" action="${path}/mtNode/save" method="POST">
            <input type="hidden"  name="nodeId" id="nodeId"  value="${mtNode.id}">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id=""><c:choose><c:when test="${ empty mtNode}">新增</c:when><c:otherwise>修改</c:otherwise></c:choose>点位</h4>
            </div>
            <div class="modal-body">
                <div class="modal_commonbody clearfix mt20">
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5" for="location_name">点位名称:</span>

                        <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入点位名称" name="nodeName1" value="${mtNode.nodeName}" required data-msg-required="请输入点位名称" minlength="2" data-msg-minlength="最少输入2个字符" maxlength="25" data-msg-maxlength="最多可输入25个字符" data-rule-Au9="true"></div>
                    </section>
                    <section class="col-md-6">
                        <span class=" pull-left w100 text-right pr5">所属场景:</span>
                        <div class="form-group pull-left w250">
                            <select class="form-control input-sm" name="businessDistrictId" required>
                                <option value="">请选择</option>
                                <c:forEach items="${bdlist}" var="bd">
                                    <option value="${bd.id}" ${mtNode.businessDistrictId==bd.id?'selected':''}>${bd.businessDistrictName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">区域:</span>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w80 pull-left mr5" name="provinceId" id="provinceAdd" onchange="refreshCitySelectUpdate()" value="${mtNode.provinceId}" required></select>
                            <select class="form-control input-sm w80 pull-left mr5" name="cityId" id="cityAdd" onchange="refreshCountrySelectUpdate()" value="${mtNode.cityId}" required></select>
                            <select class="form-control input-sm w80 pull-left" name="areaId" id="countryAdd" value="${mtNode.areaId}" required></select>
                        </div>
                    </section>

                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5" for="location_address">详细地址:</span>
                        <div class="form-group w250 pull-left"><input class="form-control input-sm w200" type="text" placeholder="请输入详细地址" name="nodeAddress" id="nodeAddress" value="${mtNode.nodeAddress}" required data-msg-required="输入详细地址按下回车" minlength="2" data-msg-minlength="最少输入2个字符" maxlength="200" data-msg-maxlength="最多可输入200个字符" data-rule-Au9="true"></div>
                    </section>

                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">经度:</span>
                        <div class="form-group w250 pull-left"><input class="form-control onChange_error input-sm" id="longitude" name="longitude" type="text" required placeholder="自动获取经度" readonly value="${mtNode.longitude}"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">纬度:</span>
                        <div class="form-group w250 pull-left"><input class="form-control onChange_error input-sm" id="latitude" name="latitude" type="text" required placeholder="自动获取纬度" readonly value="${mtNode.latitude}"></div>
                    </section>

                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">配送员:</span>
                        <div class="form-group pull-left w250">
                            <select class="form-control input-sm ClientName" name="deliveryId" required>
                                <option value="">请选择</option>
                                <c:forEach items="${deliveryList}" var="delivery">
                                    <option value="${delivery.id}" ${delivery.id eq mtNode.delivery.id?'selected':''}>${delivery.userName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">维修员:</span>
                        <div class="form-group pull-left w250">
                            <select class="form-control w250 input-sm" name="repairId" required>
                                <option value="">请选择</option>
                                <c:forEach items="${repairList}" var="repair">
                                    <option value="${repair.id}" ${repair.id eq mtNode.repair.id?'selected':''}>${repair.userName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>

                        <c:if test="${u:getUser().company.customer.customerMode eq u:getIdByCode('oneself_business','customer_mode' ,'240')}">
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">合伙人:</span>
                        <div class="form-group pull-left w250">
                            <select class="form-control input-sm ClientName" name="partnerId">
                                <option value="">请选择</option>
                                <c:forEach items="${u:getUser().company.partner}" var="part">
                                    <option value="${part.id}" ${part.id eq mtNode.partner.id?'selected':''}>${part.partnerName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">负责补货方:</span>
                        <select class="form-control w250 input-sm" name="fillType" value="${mtNode.fillType}">
                            <option value="1">自补</option>
                            <option value="2" ${mtNode.fillType eq 2?'selected':''}>客户补</option>
                        </select>
                    </section>
                    </c:if>
                    <div class="clearfix"></div>
                    <%--<section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">开始经营时间:</span>
                        <div class="form-group w250 pull-left"><input class="form-control onChange_error input-sm form_time"  readonly  value="${mtNode.optStartTime}" name="optStartTime" type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">结束经营时间:</span>
                        <div class="form-group w250 pull-left"><input class="form-control onChange_error input-sm form_time"  readonly value="${mtNode.optEndTime}" name="optEndTime"type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                    </section>--%>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">经营时间:</span>
                        <div class="form-group w110 pull-left"><input class="form-control onChange_error input-sm form_time"  readonly  value="${mtNode.optStartTime}" name="optStartTime" type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                        <span class="pull-left ml9">至</span>
                        <div class="form-group w110 pull-left ml9"><input class="form-control onChange_error input-sm form_time"  readonly value="${mtNode.optEndTime}" name="optEndTime"type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">可投放时间:</span>
                        <div class="form-group w110 pull-left"><input class="form-control onChange_error input-sm form_time"  readonly value="${mtNode.putStartTime}" name="putStartTime" type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                        <span class="pull-left ml9">至</span>
                        <div class="form-group w110 pull-left ml9"><input class="form-control onChange_error input-sm form_time"  readonly value="${mtNode.putEndTime}" name="putEndTime" type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                    </section>
                    <div class="clearfix"></div>
                    <%--<section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">投放方式:</span>
                        <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text"  name="putMethod" value="${mtNode.putMethod}" placeholder="请输入投放方式"></div>
                    </section>--%>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">物业名称:</span>
                        <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" name="propertyName" value="${mtNode.propertyName}" placeholder="非必填"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">物业方负责人:</span>
                        <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" name="propertyManager" value="${mtNode.propertyManager}" placeholder="非必填"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">物业负责人电话:</span>
                        <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" name="propertyPhone" value="${mtNode.propertyPhone}" placeholder="非必填"></div>
                    </section>
                    <section class="col-md-6">
                        <span class="pull-left w100 text-right pr5">物业负责人职位:</span>
                        <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" name="propertyLevel" value="${mtNode.propertyLevel}" placeholder="非必填"></div>
                    </section>
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2 confirmbtn" >确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>
        <script>
            $(function(){
                $(document).on("change",".onChange_error", function(){
                    var tm = $(this).val();
                    if(tm != ""){
                       $(this).parents(".form-group").removeClass("has-error");
                    }
                });
            });
        </script>
    </div>
</div>
<script type="text/javascript" src="${path}/vmsjs/area.js"></script>
<script type="text/javascript">
    $(function(){
        MtNode.addMtNode();
        renderProvinceUpdate();
        var selectedProvinceId = $("#provinceAdd").val();
        renderCityUpdate(selectedProvinceId);
        var selectedCityeId = $("#cityAdd").val();
        renderCountryUpdate(selectedCityeId);
        $(document).on('keydown','#nodeAddress',function (event) {
            if(event.keyCode==13){
            // 创建地址解析器实例
            var myGeo = new BMap.Geocoder();
            // 将地址解析结果显示在地图上,并调整地图视野
                var nodeAddress = $('#nodeAddress').val();
                var provinceName = $('#provinceAdd').find("option:selected").text();
                var cityName = $('#cityAdd').find("option:selected").text();
                var countryName = $('#countryAdd').find("option:selected").text();
                var address = provinceName+cityName+countryName+nodeAddress;
            myGeo.getPoint(address, function(point){
                if (point) {
                    $('#longitude').val(point.lng);
                    $('#latitude').val(point.lat);
                    $('#latitude, #longitude').parents(".form-group").removeClass("has-error");
                }else{
                    layer.msg("您选择地址没有解析到结果",{shade: [0.5, '#fff'],time:2000});
                }
            }, "北京市");
        }})
    });
</script>