<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--商品类型搜索-->
<!--商品类型搜索-->
<form action="${path}/stSkuEvent/sku" method="POST" id ="sku-search-form"  class="unKeyCode">
<div class="main_commonhead hl30 b1 clearfix">

    <section>
        <strong>商品类型:</strong>
        <div class="form-group w120 pull-left mr10">
            <select class="form-control input-sm" id="typeGroupAdd" onchange="refreshRenderType()">
            </select>
        </div>
        <div class="form-group w120 pull-left" >
            <select class="form-control input-sm" id="typeAdd">
            </select>
        </div>
    </section>
    <section>
        <strong>商品名称:</strong>
        <div class="form-group w160 pull-left"><input name="skuName" id="skuName" class="form-control input-sm" type="text" placeholder="请输入商品名称"></div>
    </section>
    <section>
        <strong>商品编号:</strong>
        <div class="form-group w160 pull-left"><input name="skuCode" id="skuCode" class="form-control input-sm" type="text" placeholder="请输入商品编号"></div>
    </section>
    <button type="button" class="btn btn-success pull-left f12 btn-lg2 sku_search">查询</button>
</div>

</form>
<!--关联商品信息-->
<div class="col_relation choice_relation small" id="skuInfo">
    <ul id="skuUl" style="overflow: hidden;">
       <%-- <c:forEach items="${subList}" varStatus="vs" var="object">
            <c:choose>
                <c:when test="${vs.index==0}">
                    <li class="on">
                </c:when>
                <c:otherwise>
                    <li>
                </c:otherwise>
            </c:choose>
            <section><img src="${object.imgUrl}"></section><i></i>
            &lt;%&ndash;<p>${object.skuName}</p>&ndash;%&gt;
            <p><a href="#" data-toggle="tooltip" data-placement="top" title="${object.skuName}${object.skuUnit}" data-original-title="${object.skuName}${object.skuUnit}"><span>${object.skuName}</span></a></p>
            <input type="hidden" value="${object.id}" name="skuId">
            <input type="hidden" value="${object.skuName}${object.skuUnit}" name="skuName">
            </li>
        </c:forEach>--%>
    </ul>
    <div id="addLibraryDiv" style="display:none; text-align: center; line-height: 40px; margin: 50px auto 10px;">
        <p>您的商品库还没有商品，请先添加商品</p>
        <a href="/skuLibrary/list" target="_blank" class="btn btn-success btn-lg2">添加商品</a>
    </div>
</div>
<!--按钮-->
<div class="modal-footer">
    <button type="button" class="btn btn-success btn-lg2 confirmbtn">确定</button>
    <button type="button" class="btn btn-default btn-lg2 closebtn" >取消</button>
</div>
<script type="text/javascript" src="${path}/vmsjs/skuType.js"></script>
<script type="text/javascript">
var sku = '${u:getSkuJson()}';
// 将字符串转成JSON对象
var skuJson = eval("(" + sku + ")");

// 页面加载完成后执行的方法
var skuIds = '${skuIds}';
var skuName = '${skuName}';
var skuCode = '${skuCode}';
var skuGroupTypeId = '${skuGroupTypeId}';
var skuTypeId = '${skuTypeId}';
var pageNo = 1;
var loading = false;
function load(){
    loading = true;
    pageNo = loadSku(skuIds,skuName,skuCode,skuGroupTypeId,skuTypeId,pageNo);
    loading = false;
}
$(function(){
    renderTypeGroup();

var groupId = $("#typeGroupAdd").val();
    renderType(groupId);
    $("[data-toggle='tooltip']").tooltip();
    load();
});
// 下拉加载
$('#skuInfo').scroll(function(){
    totalheight = parseFloat($('#skuInfo').height()) + parseFloat($('#skuInfo').scrollTop()) + 120;
    if($('#skuUl').height() <= totalheight){
        if(!loading){
        load();
        }
    }
});
</script>