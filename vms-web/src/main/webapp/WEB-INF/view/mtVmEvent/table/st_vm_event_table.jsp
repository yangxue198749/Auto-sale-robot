<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w800">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择整机策略</h4>
        </div>
        <div class="modal-body" id="setSalePromotion">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix step_info">
                <div class="main_commonhead hl30">
                    <form action="${path}/mtVmEvent/table/st/vm" id="search-st-vm-form" method="post" >
                    <section>
                        <strong>策略名:</strong>
                        <div class="form-group w110 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入策略名称"  name="searchValue" value="${searchValue}"></div>
                    </section>
                    <button type="button" class="btn btn-default btn-sm" id="search-st-vm">查询</button>
                    </form>
                </div>
                <div class="main_commontable max300">
                    <!--theadscroll start-->
                    <div class="theadscroll" id="theadScroll">
                        <ul>
                            <li class="wh3"></li>
                            <li class="wh22">策略名</li>
                            <li class="wh25">策略说明</li>
                            <li class="wh25">折扣类型</li>
                            <li class="wh25">折扣额</li>
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
                        </c:when>
                        <c:otherwise>
                            <table>
                                <tbody class="auto singleCheck">
                                    <c:forEach items="${subList}" varStatus="vs" var="object">
                                    <tr>
                                        <td class="wh3"><input type="checkbox" value="${object.strategy.strategyId}" strategyName="${object.strategy.strategyName}"></td>
                                        <td class="wh22"><span>${object.strategy.strategyName}</span></td>
                                        <td class="wh25"><span>${object.strategy.strategyDesc}</span></td>
                                        <td class="wh25"><span>${u:getDesc(object.discountType,"discountType")}</span></td>
                                        <td class="wh25"><span>${object.discountValue}${u:getSymbol(object.discountType,"discountType")}</span></td>
                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                    <!--table footer start-->
                    <c:if test="${!empty subList}">
                        <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" id="selectVmboxSales" setPage="false"/>
                    </c:if>
                    <!--table footer end-->
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2 nextbtn" data-set="#selectVmboxSales table tbody input[type=checkbox]:checked" id="vm_st_next_btn">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>

        </div>

    </div>
</div>
<script>
    $(function(){
        MtEvent.bindNext();
    });
    $(".singleCheck td").on("click","input[type='checkbox']",function(){
        if($(this).prop("checked")){
            $(this).parents().siblings().find("input[type='checkbox']").prop("checked",false);
        }
    });
</script>