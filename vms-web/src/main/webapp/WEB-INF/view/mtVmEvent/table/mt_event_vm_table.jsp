<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script type="text/javascript" src="${path}/dist/js/function.table.js"></script>
<div class="modal-dialog w1000">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" >选择售货机</h4>
        </div>
        <div class="modal-body" id="mtvm_vm_div">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix step_info">
                <form action="${path}/mtVmEvent/table/vm" method="POST" id ="search-form-vm"  class="unKeyCode">
                    <c:forEach items="${ids}" var="id">
                        <input type="hidden" name="ids" value="${id}">
                    </c:forEach>
                <div class="main_commonhead hl30">
                    <section>
                        <strong>区域:</strong>
                        <div class="form-group pull-left mr5">
                            <select class="form-control input-sm w100" name="province" id="provinceAdd" onchange="refreshCitySelectUpdate()" value="${province}"></select>
                        </div>
                        <div class="form-group pull-left mr5">
                            <select class="form-control input-sm w100"  name="city" id="cityAdd" onchange="refreshCountrySelectUpdate()" value="${city}"></select>
                        </div>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm w100" name="area" id="countryAdd" value="${area}"></select>
                        </div>
                    </section>
                    <%-- 20171225 11:26 隐藏场景属性 by tonney
                    <section>
                        <strong>场景:</strong>
                        <div class="form-group pull-left">
                            <select class="form-control input-sm pull-left w80">
                                <option value="">请选择</option>
                                <c:forEach items="${bdlist}" var="bd">
                                    <option value="${bd.id}" ${bd.id eq businessDistrictId?'selected':''}>${bd.businessDistrictName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>--%>
                    <section>
                        <strong>点位:</strong>
                        <div class="form-group w110 pull-left"><input class="form-control input-sm" type="text" name="searchNodeName" value="${searchNodeName}" placeholder="请输入点位"></div>
                    </section>
                    <section>
                        <strong>售货机编号:</strong>
                        <div class="form-group w110 pull-left"><input class="form-control input-sm" type="text" name="searchInnerCode" value="${searchInnerCode}" placeholder="售货机编号"></div>
                    </section>
                    <button type="button" class="btn btn-default btn-lg2 f12 pull-left" id="searchButtonVm">查询</button>
                    <div class="clearfix"></div>
                </div>
                    </form>
                <div class="main_commontable max300">
                    <!--theadscroll start-->
                    <div class="theadscroll">
                        <ul>
                            <li class="wh3"><input type="checkbox" value=""></li>
                            <li class="wh17">售货机编号</li>
                            <li class="wh20">点位</li>
                            <li class="wh20">区域</li>
                            <li class="wh20">具体位置</li>
                            <li class="wh20">商品促销策略</li>
                        </ul>
                    </div>
                    <!--theadscroll end-->
                    <c:choose>
                        <c:when test="${empty subList}">
                            <div>
                                <section style="padding:100px 0; text-align:center;">
                                    <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                                    <p>暂无内容</p>
                                </section>
                            </div>
                            <%--<tr><td style="padding:100px 0; text-align: center" colspan="6">
                                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p>
                            </td></tr>--%>
                        </c:when>
                        <c:otherwise>
                        <table>
                            <tbody class="auto">
                            <c:forEach items="${subList}" varStatus="vs" var="object">
                                <tr>
                                    <td class="wh3"><input type="checkbox" value="${object.id}" innerCode="${object.innerCode}"></td>
                                    <td class="wh17">${object.innerCode}</td>
                                    <td class="wh20">${object.node.nodeName}</td>
                                    <td class="wh20">${u:getAreaName(object.node.provinceId)} ${u:getAreaName(object.node.cityId)} ${u:getAreaName(object.node.areaId)}</td>
                                    <td class="wh20">${object.vmDesc}</td>
                                    <td class="wh20">${object.skuEvent}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        </c:otherwise>
                    </c:choose>
                    <!--table footer start-->
                    <c:if test="${!empty subList}">
                        <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" id="selectVmbox" setPage="false"/>
                    </c:if>
                    <!--table footer end-->
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2 nextbtn" data-set="#mtvm_vm_div table tbody input[type=checkbox]:checked" id="vm_next_btn">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 " data-dismiss="modal">取消</button>
                </div>
            </div>

        </div>

    </div>
</div>
<script type="text/javascript">

    $(function(){
        MtEvent.bindNext();

        renderProvinceUpdate();

        var selectedProvinceId = $("#provinceAdd").val();

        renderCityUpdate(selectedProvinceId);

        var selectedCityeId = $("#cityAdd").val();

        renderCountryUpdate(selectedCityeId);
    });

</script>