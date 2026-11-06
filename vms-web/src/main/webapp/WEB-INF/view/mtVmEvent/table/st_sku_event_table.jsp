<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w800">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择商品策略</h4>
        </div>
        <div class="modal-body" id="setSalePromotion">
            <!--基本信息 start-->
            <div class="modal_commonbody clearfix step_info">
                <div class="main_commonhead hl30">
                    <form action="${path}/mtVmEvent/table/st/sku" id="search-st-sku-form" method="post" >
                    <section>
                        <strong>策略名:</strong>
                        <div class="form-group w110 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入策略名称" name="searchValue" value="${searchValue}"></div>
                    </section>
                    <button type="button" class="btn btn-default btn-sm" id="search-st-sku">查询</button>
                    </form>
                </div>
                <div class="main_commontable max300">
                    <!--theadscroll start-->
                    <div class="theadscroll" id="theadScroll">
                        <ul>
                            <li class="wh5"></li>
                            <li class="wh30">策略名</li>
                            <li class="wh55">策略说明</li>
                            <li class="wh10">详情</li>
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
                                    <td class="wh5"><input type="checkbox" value="${object.strategyId}" strategyName="${object.strategyName}"></td>
                                    <td class="wh30"><span class="cf_green">${object.strategyName}</span></td>
                                    <td class="wh55"><span>${object.strategyDesc}</span></td>
                                    <td class="wh10">
                                        <button type="button" class="btn btn-success btn-sm" href="${path}/mtVmEvent/detail/st?strategyId=${object.strategyId}" data-toggle="modal" data-target="#checkTactics">查看</button>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        </c:otherwise>
                    </c:choose>
                    <!--table footer start-->
                    <c:if test="${!empty subList}">
                        <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${path}/${url}" id="selectShop" setPage="false"/>
                    </c:if>
                    <!--table footer end-->
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2 nextbtn" data-set="#selectShop table tbody input[type=checkbox]:checked" id="sku_st_next_btn">确定</button>
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