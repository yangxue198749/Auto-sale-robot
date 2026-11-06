<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script type="text/javascript" src="${path}/vmsjs/mt_vm_price.js"></script>
<!--
**
****    模态框包含如下：
****    价格设置所需模态框：
#CtrlThePrice  价格设置
#viewThePrice  查看价格
****
-->

<!-------------------------------------价格设置：#CtrlThePrice start------------------------------------------------>
    <div class="modal-dialog w800">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">选择价格策略</h4>
            </div>
            <div class="modal-body">
                <!--选择策略 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
                        <form action="${path}/mtVmPrice/page" id="st_vm_price_search_form" name="" method="post" class="unKeyCode">
                            <section>
                                <strong>策略名称:</strong>
                                <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="strategyName" placeholder="请输入策略名称"></div>
                            </section>
                            <button type="button" id="st_vm_price_search_btn" class="btn btn-default f12 btn-lg2 mr10">查询</button>
                            <button type="button" id="originalPrice" class="btn btn-success btn-lg2 pull-right">恢复原价</button>
                        </form>
                    </div>
                    <form id="mt_vm_price_form_st" method="post" action="${path}/mtVmPrice/save">
                        <c:forEach items="${ids}" var="vmId" >
                            <input type="hidden" name="ids" value="${vmId}">
                        </c:forEach>
                    <!--table body start-->
                    <div class="main_commontable max400 border_line clearfix" id="st_vm_price_div">
                        <jsp:include page="/mtVmPrice/page" flush="true"></jsp:include>
                    </div>
                    <!--table body end-->
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                    </form>
                </div>
                <!--选择策略 end-->
            </div>

        </div>
    </div>
<!-------------------------------------价格设置：#CtrlThePrice end ------------------------------------------------->

<script>
    $(function(){
        MtVmPrice.init();
    });
</script>