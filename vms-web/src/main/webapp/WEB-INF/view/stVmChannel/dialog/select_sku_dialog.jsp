<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w1000">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">选择商品</h4>
                </div>
                <div class="modal-body">
                        <!--货道设置信息 start-->
                        <div class="modal_commonbody clearfix">
                             <div class="main_commonhead hl30">
                                <form action="/stVmChannel/skuList" id="search-sku-form">
                                    <section>
								        <strong>商品类型:</strong>
								        <div class="form-group w120 pull-left mr10">
								            <select class="form-control input-sm" name="skuTypeGroupId" id="typeGroupAdd" onchange="refreshRenderType()">
								            </select>
								        </div>
								        <div class="form-group w120 pull-left" >
								            <select class="form-control input-sm" name="skuTypeId" id="typeAdd">
								            </select>
								        </div>
								    </section>
                                    <section>
                                        <strong>商品名称:</strong>
                                        <div class="form-group w120 pull-left">
                                        	<input class="form-control input-sm" name="skuName" type="text" placeholder="请输入商品名称">
                                        </div>
                                    </section>
                                    <section>
                                        <strong>商品编码:</strong>
                                        <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="skuCode" placeholder="请输入商品编码"></div>
                                    </section>
                                    <button type="button" id="search-sku-button" class="btn btn-default f12 btn-lg2 mr10">查询</button>
                                </form>
                            </div>
                            <!--商品 box start-->
                            <input name="relateChannelIds" id="relateChannelIds" value="${relateChannelIds }" type="hidden">
                            <input id="relateType" value="${relateType }" type="hidden">
                            <div id="skuDiv" class="col_relation mt15 small choiceTheGoods">
                            	<jsp:include page="/stVmChannel/skuList" flush="true"/>
                            </div>
                        </div>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" id="relateButton" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
                    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
 <script type="text/javascript">
 	 var sku = '${u:getSkuJson()}';
	//将字符串转成JSON对象
	var skuJson = eval("(" + sku + ")");
    $(function(){
    	 $(".choiceTheGoods").on("click","li", function(){
             $(this).addClass("on").siblings().removeClass("on");
			});
			renderTypeGroup();

	var groupId = $("#typeGroupAdd").val();

    renderType(groupId);
    	StVmChannel.searchSku();
    })
</script>