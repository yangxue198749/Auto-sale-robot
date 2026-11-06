<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<script src="${path}/plus/datepicker/js/function.datepicker.js"></script>
<script src="${path}/plus/datetimepicker/function.datetimepicker.js"></script>
<div class="modal-dialog">
    <form id="set-sku-event-form" action="${path}/mtVmEvent/save/sku" method="POST" class="unKeyCode">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">应用商品促销策略</h4>
        </div>
        <div class="modal-body">
            <div class="modal_commonbody clearfix">

                <section class="col-md-12 mb15">
                    <span class="w80 pull-left text-right mr10">售货机:</span>
                    <button type="button" class="btn btn-default btn-sm2 pull-left btn-block w160" style="font-size:12px; padding:5px 15px;" data-toggle="modal" href="${path}/mtVmEvent/table/sku" li-set="#add_mtVm_sku input:hidden"  data-target="#selectSkubox">选择售货机</button>
                    <div class="clearfix"></div>
                    <div class="addvmbox" id="add_mtVm_sku"></div>
                </section>
                <%--<section class="col-md-12">
                    <button type="button" class="btn btn-default btn-sm ml90" data-toggle="modal" href="${path}/mtVmEvent/table/sku" li-set="#add_mtVm_sku input:hidden"  data-target="#selectSkubox">选择售货机</button>
                </section>--%>
                <section class="col-md-12">
                    <span class="pull-left text-right w80 mr10">商品促销策略:</span>
                    <div class="form-group pull-left">
                        <input type="hidden" id="sku_st_input" value="" name="strategyId">
                        <button type="button" class="btn btn-default btn-sm2 pull-left btn-block w160 mr10" style="font-size:12px; padding:5px 15px;" data-toggle="modal" href="${path}/mtVmEvent/table/st/sku" data-target="#selectShop">选择商品策略</button>
                        <span id="sku_st_span" class="cf_green"></span>
                    </div>
                </section>
                <section class="col-md-12">
                    <span class="pull-left text-right w80 mr10">生效日期:</span>
                    <div class="form-group w160 pull-left"><input class="form-control input-sm onChange_error" type="text" name="beginDate" id="DateStartOne" placeholder="开始日期" required data-msg-required="请选择开始日期" readonly></div>
                    <strong class="ml5 pull-left mr5">-</strong>
                    <div class="form-group w160 pull-left"><input class="form-control input-sm onChange_error" type="text" name="endDate" id="DateEndOne" placeholder="结束日期" required data-msg-required="请选择结束日期" readonly></div>
                </section>
                <section class="col-md-12" id="checkTimeGrid">
                    <span class="pull-left text-right w80 mr10">生效时段:</span>
                    <div class="form-group w160 pull-left"><input class="form-control input-sm form_time onChange_error startTime" type="text" name="startTime" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii" required data-msg-required="请选择开始时间" readonly></div>
                    <span class="ml5 pull-left mr5">-</span>
                    <div class="form-group w160 pull-left"><input class="form-control input-sm form_time onChange_error endTime" type="text" name="endTime" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii" required data-msg-required="请选择结束时间" readonly></div>
                </section>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" id="btnSubmit" class="btn btn-success btn-lg2">提交</button>
            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
        </div>
    </div>
    </form>
</div>
<script>
    $(function(){
        $('#set-sku-event-form .btn-success').click(function(){
           var mtVmSpan = $('#add_mtVm_sku').find('input[type="hidden"]').length;
           console.log(mtVmSpan);
           var skuStInput = $('#sku_st_input').val();
           if(mtVmSpan == 0 || mtVmSpan == '' || mtVmSpan == undefined){
               layer.msg('请选择售货机',{shade: [0.5, '#fff'],time:2000});
               return false;
           }
           if(skuStInput == ''){
               layer.msg('请选择商品促销策略',{shade: [0.5, '#fff'],time:2000});
               return false;
           }else{
               // 20180404 14:50 效验开始和结束时间
               var _this = $('#checkTimeGrid');
               var _startTime = _this.find('.startTime').val();
               var _endTime = _this.find('.endTime').val();
               var _d = new Date();
               var sYMD = _d.getFullYear()+'/'+(_d.getMonth()+1)+'/'+_d.getDay()+' '+_startTime;
               var eYMD = _d.getFullYear()+'/'+(_d.getMonth()+1)+'/'+_d.getDay()+' '+_endTime;
               var _time = Date.parse(eYMD)-Date.parse(sYMD);
               if(_time > 0){
                   $('#btnSubmit').attr('type','submit');
                   MtEvent.addSkuEvent();
               }else{
                   layer.msg('结束时间应大于开始时间',{shade: [0.5, '#fff'],time:2000});
                   return false;
               }
           }
        });
        //MtEvent.addSkuEvent();
        $(document).on("change",".onChange_error", function(){
            var tm = $(this).val();
            if(tm != ""){
                $(this).parents(".form-group").removeClass("has-error");
            }
        });
        $('#add_mtVm_sku').on("click",".deleteBtn",function(){
            $(this).parents('.mtVmSpan').remove();
        });
    });
</script>