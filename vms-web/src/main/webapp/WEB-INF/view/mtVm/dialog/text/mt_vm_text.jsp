<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script src="${path}/plus/datepicker/js/function.datepicker.js"></script>
<script type="text/javascript" src="${path}/vmsjs/mt_vm_text.js"></script>
<!--
**
****    模态框包含如下：
****    走马灯设置所需模态框：
#CtrlTheLight  走马灯设置
#viewTheLight  查看走马灯配置
****
-->

<!-------------------------------------走马灯设置：#CtrlTheLight start------------------------------------------------>
    <div class="modal-dialog w800">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">走马灯设置</h4>
            </div>
            <div class="modal-body">
                <!---Tab 选择策略 与 直接设置 切换 start--->
                <div class="tabheader">
                    <ul>
                        <li class="on"><span>选择策略</span></li>
                        <li><span>直接设置</span></li>
                    </ul>
                </div>
                <!--控制tab切换-->
                <script>
                    $(function(){
                        $('#CtrlTheLight .tabheader').on('click','li',function(){
                            $(this).addClass('on').siblings().removeClass('on');
                            $('#CtrlTheLight .modal_commonbody').hide().eq($('#CtrlTheLight .tabheader li').index(this)).show();
                        });
                    });
                </script>
                <!---Tab 选择策略 与 直接设置 切换 end--->
                <!--选择策略 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
                        <form action="${path}/mtVmText/page" name="" id="st_vm_text_search_form" method="post" class="unKeyCode">
                            <section>
                                <strong>策略名称:</strong>
                                <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="strategyName" placeholder="请输入策略名称"></div>
                            </section>
                            <button type="button" class="btn btn-default f12 btn-lg2 pull-left mr20" id="st_vm_text_search_btn">查询</button>
                        </form>
                    </div>
                    <div class="clearfix"></div>
                    <!--table body start-->
                    <form action="${path}/mtVmText/save/st" name="" id="mt_vm_text_form_st" method="post">
                        <c:forEach items="${ids}" var="vmId" >
                            <input type="hidden" name="ids" value="${vmId}">
                        </c:forEach>
                     <div class="clearfix mt20">
                        <section class="col-md-6">
                            <strong>开始时间:</strong>
                            <div class="form-group"><input class="form-control input-sm" id="DateStartOne" type="text" placeholder="开始时间" name="beginDate" required></div>
                        </section>
                        <section class="col-md-6">
                            <strong>结束时间:</strong>
                            <div class="form-group"><input class="form-control input-sm" id="DateEndOne" type="text" placeholder="结束时间" name="endDate" required></div>
                        </section>
                     </div>
                    <div class="main_commontable max400 border_line clearfix" id="st_vm_text_div">
                        <jsp:include page="/mtVmText/page" flush="true"></jsp:include>
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
                <!--直接设置 start-->
                <div class="modal_commonbody clearfix" style="display:none">
                    <form action="${path}/mtVmText/save" name="" id="mt_vm_text_form" method="post">
                        <c:forEach items="${ids}" var="vmId" >
                            <input type="hidden" name="ids" value="${vmId}">
                        </c:forEach>
                        <div class="clearfix mt20">
                            <section class="col-md-6">
                                <strong>开始时间:</strong>
                                <div class="form-group"><input class="form-control input-sm" id="DateStart" type="text" placeholder="开始时间" required name="beginDate" value="<fmt:formatDate value="${mtVm.mtText.beginDate}" type="date" pattern="yyyy/MM/dd"/>"></div>
                            </section>
                            <section class="col-md-6">
                                <strong>结束时间:</strong>
                                <div class="form-group"><input class="form-control input-sm" id="DateEnd" type="text" placeholder="结束时间" required  name="endDate" value="<fmt:formatDate value="${mtVm.mtText.endDate}" type="date" pattern="yyyy/MM/dd"/>"></div>
                            </section>
                            <section class="col-md-12">
                                <strong for="moretext">文案:</strong>
                                <div class="form-group"><textarea class="form-control input-sm" type="text" placeholder="请输入文案内容" name="mtText" required data-msg-required="请输入文案内容" minlength="2" data-msg-minlength="最少输入2个字" maxlength="1000" data-msg-maxlength="最多只能输入1000个字">${mtVm.mtText.textContents}</textarea></div>
                            </section>
                        </div>
                        <!--按钮-->
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-success btn-lg2">确定</button>
                            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                        </div>
                    </form>
                </div>
                <!--直接设置 end-->
            </div>

        </div>
    </div>
<!-------------------------------------走马灯设置：#CtrlTheLight end------------------------------------------------>

<script>
    $(function(){
        MtVmText.init();
    });
</script>
