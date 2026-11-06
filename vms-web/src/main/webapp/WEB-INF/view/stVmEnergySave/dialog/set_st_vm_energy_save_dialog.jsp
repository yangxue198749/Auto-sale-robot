<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal-dialog w600">
         <div class="modal-content">
             <div class="modal-header">
                 <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                 <h4 class="modal-title f16">新增节能策略</h4>
             </div>
             <div class="modal-body">
                     <div class="modal_commonbody mt10 clearfix">
                         <section class="col-md-12">
                             <div class="btn-group pull-left" data-toggle="buttons">
                                 <button type="button" class="btn btn-default btn-lg2" id="addEnergyDialogBtn" href="/stVmEnergySave/addEnergyDialog" data-toggle="modal" data-target="#energy">添加</button>
                                 <button type="button" class="btn btn-default btn-lg2 btn_tableDelete">删除</button>
                                 <button type="button" class="btn btn-default btn-lg2">修改</button>
                             </div>
                         </section>
                     </div>
                 <div class="main_commontable mt20">
                     <!--theadscroll start-->
                     <div class="theadscroll">
                         <ul>
                             <li class="wh3"><input type="checkbox" value=""></li>
                             <li class="wh17">节能类型</li>
                             <li class="wh20">开始时间</li>
                             <li class="wh20">结束时间</li>
                             <li class="wh20">保温箱编号</li>
                             <li class="wh20">温度目标值</li>
                         </ul>
                     </div>
                     <!--theadscroll end-->
                     <div id="editEnergySaveDetailDiv">
                     ${html }
                     </div>
                 </div>
             </div>
             <div class="modal-footer">
                 <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
             </div>
         </div>
</div>