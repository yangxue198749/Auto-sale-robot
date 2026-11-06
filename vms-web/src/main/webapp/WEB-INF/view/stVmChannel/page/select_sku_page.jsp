<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<ul id="skuUl" style="overflow: hidden;">
	<%--<c:if test="${! empty skuList }">--%>
		<%--<c:forEach items="${skuList }" var="sku">--%>
		    <%--<li>--%>
		        <%--<section><img src="${sku.imgUrl }"></section><i></i>--%>
		        <%--&lt;%&ndash;<p>${sku.skuName }</p>&ndash;%&gt;--%>
				<%--<p><a href="#" data-toggle="tooltip" data-placement="top" title="${sku.skuName}${sku.skuUnit}" data-original-title="${sku.skuName}${sku.skuUnit}"><span>${sku.skuName}</span></a></p>--%>
				<%--<input type="hidden" value="${sku.id }">--%>
		    <%--</li>--%>
		<%--</c:forEach>--%>
	<%--</c:if>--%>
</ul>
<div id="addLibraryDiv" style="display:none; text-align: center; line-height: 40px; margin: 50px auto 10px;">
	<p>您的商品库还没有商品，请先添加商品</p>
	<a href="/skuLibrary/list" target="_blank" class="btn btn-success btn-lg2">添加商品</a>
</div>
<script>
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
	$('#skuDiv').scroll(function(){
		totalheight = parseFloat($('#skuDiv').height()) + parseFloat($('#skuDiv').scrollTop()) + 120;
		if($('#skuUl').height() <= totalheight){
			if(!loading){
				load();
			}
		}
	});
	$(function () {
		$("[data-toggle='tooltip']").tooltip();
		load();
	});

</script>