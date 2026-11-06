<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<div class="modal-dialog w800">
    <div class="modal-content">
        <form id="add-shortcuts-form-2" action="${path}/stShortcuts/addShortcuts" method="POST">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="addFlashBuyLabel">添加闪购键配置</h4>
        </div>
        <div class="modal-body" id="setFlashBuy">
            <!--配置闪购键信息 start-->
            <div class="modal_commonbody max400 clearfix mt20 step_choice" >
                <!--配置闪购商品信息-->
                <div class="clearfix">
                  <input type="hidden" name="strategyName" value="${strategyName}" >
                  <input type="hidden" name="strategyDesc" value="${strategyDesc}" >
                  <input type="hidden" name="modelId"      value="${modelId}" >
                     <c:forEach var="item" varStatus="status" begin="1" end="${model.shortcutsQty}">
                       <section class="col-md-3 text-center pb20">
                         <div class="col_choicebox">
                            <header><span>闪购键${status.count}</span></header>
                            <hgroup>
                                <section><img src="" id="${status.count}_img_add"></section>
                            </hgroup>
                            <footer id="${status.count}_name_add"></footer>
                             <input type="hidden" name="skuIds" id="${status.count}_id_add">
                             <input type="hidden" name="shortcutsCodes" value="${status.count}">
                             <input type="hidden" name="bindTypes" id="${status.count}_bind_type_add">
                             <input type="hidden" name="resourceIds" id="${status.count}_resource_Id_add">
                            <%--<aside><button type="button" class="btn btn-success btn-sm2 f12 btn-block addbtn">添加商品</button></aside>--%>
                             <%--<aside><button type="button" class="btn btn-success f12 btn-sm2" data-toggle="modal" data-target="#select-sku-div" href="${path}/stShortcuts/sku?shortcutsCode=${status.count}">添加商品</button></aside>--%>
                             <aside class="buttons">
                                 <div class="btn-group" data-toggle="buttons">
                                     <button type="button" class="btn btn-success f12 btn-sm2" data-toggle="modal" data-target="#select-sku-div" href="${path}/stShortcuts/sku?shortcutsCode=${status.count}">商品</button>
                                     <button type="button" class="btn btn-success f12 btn-sm2" data-toggle="modal" href="/stShortcuts/selectMtVmResourceDialog?shortcutsCode=${status.count}" data-target="#selectLink">链接</button>
                                 </div>
                             </aside>
                         </div>
                       </section>
                     </c:forEach>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" id="submit_add" class="btn btn-success btn-lg2 confirmbtn">确定</button>
                    <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
                </div>
            </div>
            <!--配置闪购键信息 end-->
        </div>
      </form>
    </div>
</div>



<script type="text/javascript">
    $(function () {
        Shortcuts.bindAddShortcuts2();
    })
</script>
