<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog">
    <div class="modal-content w800">
         <div class="modal-header">
               <button type="button" onclick="empty($('#addSku'))" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
               <h4 class="modal-title f16">新建商品</h4>
         </div>
         <form action="" name="" id="addSku_form">
          <div class="modal-body">
            <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuName">商品名称:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" maxlength="100" name="skuName" placeholder="请输入商品名称" required data-msg-required="请输入商品名称">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span for="skuTypeId" class="w100 pull-left text-right mr10">商品类型:</span>
                        <div class="form-group pull-left"">
                        	<select class="pull-left form-control input-sm w80 mr20" id="typeGroupAdd" onchange="refreshRenderType()" required data-msg-required="请选择商品类型"/>
                            <select name="skuTypeId" class="form-control input-sm w80" id="typeAdd" required data-msg-required="请选择商品类型"/>
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuColor">商品颜色:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuColor" placeholder="请输入商品颜色" required data-msg-required="请输入商品颜色" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="offerPrice">建议零售价/分:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="offerPrice" placeholder="分为单位" required data-msg-required="请输入零售价" data-rule-di9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuLength">深度/毫米:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuLength" placeholder="毫米为单位" required data-msg-required="请输入商品深度/毫米" data-rule-di9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuWidth">宽度/毫米:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuWidth" placeholder="毫米为单位" required data-msg-required="请输入商品宽度/毫米" data-rule-di9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuHeight">高度/毫米:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuHeight" placeholder="毫米为单位" required data-msg-required="请输入商品高度/毫米" data-rule-di9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuShelfLife">保质期:</span>
                        <div class="form-group pull-left">
                            <div class="input-group w180">
                                <input type="text" class="form-control input-sm" name="skuShelfLife" required data-msg-required="请输入保质期" data-rule-di9="true">
                                <div class="input-group-btn">
                                    <select name="validityType" class="dropdown-toggle input-sm" style="width: 50px; border: 1px solid #ccc; border-left:0;">
                                    <option value="0">日</option>
                                    <option value="1">月</option>
                                </select>
                                </div><!-- /btn-group -->
                            </div>
                            <%--<input type="text" class="form-control pull-left input-sm w100 mr10" name="skuShelfLife"  required data-msg-required="请输入保质期" data-rule-di9="true">
                            <select name="validityType" class="form-control input-sm w40">
                                <option value="0">日</option>
                                <option value="1">月</option>
                            </select>--%>
                        </div>
                    </section>
                  <%-- old 备份 by tonney 20180103 1530
                  <section class="col-md-6">
                      <span class="w100 pull-left text-right mr10" for="skuShelfLife">保质期:</span>
                      <div class="form-group pull-left">
                          <input type="text" class="form-control pull-left input-sm w100 mr10" name="skuShelfLife"  required data-msg-required="请输入保质期" data-rule-di9="true">
                          <select name="validityType" class="form-control input-sm w40">
                              <option value="0">日</option>
                              <option value="1">月</option>
                          </select>
                      </div>
                  </section>--%>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuMadeArea">商品产地:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuMadeArea" placeholder="请输入商品产地" required data-msg-required="请输入商品产地" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuUnit">商品规格:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuUnit" placeholder="请输入商品规格" required data-msg-required="请输入商品规格" >
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuProducer">品牌厂商:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuProducer" placeholder="请输入品牌厂商" required data-msg-required="请输入品牌厂商" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="storageMethod">存储方式:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="storageMethod" placeholder="请输入存储方式" required data-msg-required="请输入存储方式" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="packingSpecification">箱规:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="packingSpecification" placeholder="请输入箱规" required data-msg-required="请输入箱规" data-rule-di9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="dbCode">国标码:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="dbCode" placeholder="请输入国标码" required data-msg-required="请输入国标码" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="saleArea">销售区域:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="saleArea" placeholder="请输入销售区域" required data-msg-required="请输入销售区域" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="inventoryType">存货类别:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="inventoryType" placeholder="请输入存货类别" required data-msg-required="请输入存货类别" minlength="2" data-rule-Au9="true">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuCode">商品编码:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="skuCode" placeholder="请输入商品编码" required data-msg-required="请输入商品编码">
                        </div>
                    </section>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10">预售设置:</span>
                        <div class="form-group pull-left">
                            <label class="mr20" style="line-height:20px; font-weight: normal;">
                                <input style="vertical-align: -4px; margin-right: 5px;" type="radio" name="reserve" value="0" checked><span>即售</span>
                            </label>
                            <label class="pl10" style="line-height:20px; font-weight: normal;">
                                <input style="vertical-align: -4px; margin-right: 5px;" type="radio" name="reserve" value="1"><span>预售</span>
                            </label>
                        </div>
                    </section>
                    <!-- 2017/12/21 移除押金入口 -->
                    <%--<section class="col-md-6">
                        <span class="w100 pull-left text-right mr10">押金:</span>
                        <div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w180" name="deposit" placeholder="请输入押金" digits="true" data-msg-digits="只能输入整数">
                        </div>
                    </section>--%>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10">效期监控:</span>
                        <div class="form-group pull-left">
                            <label class="mr20" style="line-height:20px; font-weight: normal;">
                                <input style="vertical-align: -4px; margin-right: 5px;" type="radio" name="validityMonitor" value="1"><span>是</span>
                            </label>
                            <label class="pl10" style="line-height:20px; font-weight: normal;">
                                <input style="vertical-align: -4px; margin-right: 5px;" type="radio" name="validityMonitor" value="0" checked="checked"><span>否</span>
                            </label>
                        </div>
                    </section>
                    <%--20171228 10:45 隐藏推广属性--%>
                    <section class="col-md-6">
                        <span class="w100 pull-left text-right mr10" for="skuShelfLife">推广属性:</span>
                        <div class="form-group pull-left">
                            <select name="generalizeType" class="form-control input-sm w80">
                                <option value="0">常规</option>
                                <option value="1">积分</option>
                                <option value="2">公众号</option>
                                <option value="3">免费</option>
                            </select>
                        </div>
                    </section>
                </div>
                <!--基本信息 end-->

        	</div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2 confirmbtn">提交</button>
                <button type="button" onclick="empty($('#addSku'))" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
            </div>
         </form>
    </div>
</div>
<script>
	var sku = '${u:getSkuJson()}';
	// 将字符串转成JSON对象
	var skuJson = eval("(" + sku + ")");
	$(function(){
	    renderTypeGroup();
		var groupId = $("#typeGroupAdd").val();
	    renderType(groupId);
       	$("#addSku_form").validate({
	        submitHandler:function(form){
	        	addSku();
	        }    
	    });
	});
	/*//重置效验
    $.extend($.validator.messages, {
        required: ' ',
        equalTo: " ",
        min:$.validator.format( " " )
    });*/
</script>
