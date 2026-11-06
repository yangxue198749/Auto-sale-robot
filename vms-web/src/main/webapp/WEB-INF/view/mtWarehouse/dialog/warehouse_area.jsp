<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w400">
    <div class="modal-content">
    	<form action="" id="uniteArea_form">
    		<input name="warehouseId" value="${warehouseId}" type="hidden" >
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title f16" id="">关联区域</h4>
	        </div>
	        <div class="modal-body">
	            <div class="main_commonhead hl30">
	                <div class="form-group clearfix">
	                   <select class="form-control input-sm w110 pull-left mr5" name="province_warehouse" id="provinceSelect" onchange="refreshCitySelect()"></select>
	                   <select class="form-control input-sm w110 pull-left mr5" name="city_warehouse" id="citySelect" onchange="getCountry();"></select>
	                   <!-- <select class="form-control input-sm w110 pull-left" name="area_warehouse" id="countrySelect"></select> -->
	                </div>
	            </div>
	            <div class="main_commontable max300 border_line">
	                <table>
	                     <thead>
	                         <tr>
	                			<td class="wh20"><!-- <input type="checkbox" value=""> --></td>
	                            <td class="wh80">区</td>
	                         </tr>
	                     </thead>
	                     <tbody class="auto" id="country">
	                         <!-- <tr>
	                			<td class="wh20"><input type="checkbox" value=""></td>
	                            <td class="wh80">东城区</td>
	                         </tr> -->
	                         
	                     </tbody>
	                </table>
	            </div>
	        </div>
	        <!--按钮-->
	        <div class="modal-footer">
	            <button type="submit" class="btn btn-success btn-lg2 confirmbtn">提交</button>
	            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
	        </div>
        </form>
    </div>
</div>

<script>
	var area = '${u:getAreaJson()}';
	// 将字符串转成JSON对象
	var areaJson = eval("(" + area + ")");
	
	//var usedIdList = "${usedIdList}";
	//usedIdList = eval("(" + usedIdList + ")");//所有被应用的区域
	
	var usedIdList = ${usedIdList};
	
	
	var warehouseId = ${warehouseId};//仓库id
	
	// 页面加载完成后执行的方法
	$(function(){
	    renderProvince();
		var provinceId = $("#provinceSelect").val();
		renderCity(provinceId);
		/* var cityId = $("#citySelect").val();
		renderCountry(cityId); */
	    
	    
       	$("#uniteArea_form").validate({
	        submitHandler:function(form){
	        	uniteArea();
	        }    
	    });
	});

</script>
