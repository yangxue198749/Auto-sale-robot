<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w700">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择点位</h4>
        </div>
        <div class="modal-body">
            <div class="main_commonhead hl30">
            <form action="/mtVm/searchNodeListPage" id="search-node-form" method="POST">
<!--                 <section> -->
<!--                     <strong>区域:</strong> -->
<!--                     <div class="form-group pull-left"> -->
<!-- 	                   <select class="form-control input-sm w120 pull-left mr5" name="province" id="provinceSelect" onchange="refreshCitySelect()"></select> -->
<!-- 	                   <select class="form-control input-sm w120 pull-left mr5" name="city" id="citySelect" onchange="refreshCountrySelect()"></select> -->
<!-- 	                   <select class="form-control input-sm w120 pull-left" name="area" id="countrySelect"></select> -->
<!--                     </div> -->
<!--                 </section> -->
                <section>
                    <strong>点位:</strong>
                    <div class="form-group w120 pull-left"><input class="form-control input-sm" name="searchNodeName" type="text" placeholder="请输入关键字"></div>
                </section>
                <button type="button" id="search-node-btn" class="btn btn-default f12 btn-lg2">查询</button>
            </form>
            </div>
            <div class="main_commontable max300 border_line" id="nodeDiv">
            	<jsp:include page="/mtVm/searchNodeListPage" flush="true"/>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="button" id="select-node-btn" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
// var area = '${u:getAreaJson()}';
// 将字符串转成JSON对象
// var areaJson = eval("(" + area + ")");
$(function(){
	MtVm.selectNode();
    // 页面加载完成后执行的方法
//     renderProvince();

//     var selectedProvinceId = $("#provinceSelect").val();

//     renderCity(selectedProvinceId);

//     var selectedCityeId = $("#citySelect").val();

//     renderCountry(selectedCityeId);
})
</script>
