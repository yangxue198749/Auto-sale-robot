<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script type="text/javascript" src="${path}/vmsjs/mt_vm_channel.js"></script>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">列表页设置</h4>
        </div>
        <form>
            <div class="modal-body">

                <!--货道设置信息 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
                        <button type="button" class="btn btn-default btn-lg2" id="changeSortBtn" href="/mtVmShelf/changeSort">交换</button>
                        <button type="button" class="btn btn-default btn-lg2" id="copySortBtn" href="/mtVmShelf/copySort">复制</button>
                        <button type="button" class="btn btn-default btn-lg2" id="deleteSortBtn" href="/mtVmShelf/deleteSort">删除</button>
                    </div>
                    <!--商品 box start-->
                    <div class="col_relation mt15 small setList" id="showSortDiv">
                        ${html}
                    </div>
                    <!--商品 box end-->
                    <script>
                        $(function(){
                            $(".setList").on("click","li", function(){
                                $(this).toggleClass("on");
                            })
                            MtVmChannel.init();
                            MtVmChannel.changeSort();
                            MtVmChannel.copySort();
                            MtVmChannel.deleteSort();
                            MtVmChannel.submitShelf();
                        })
                    </script>
                </div>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-lg2" id="submitShelfBtn" href="/mtVmShelf/saveSort">确认</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">关闭</button>
            </div>
        </form>
    </div>
</div>

<script>
    $(function () { $("[data-toggle='tooltip']").tooltip(); });
</script>