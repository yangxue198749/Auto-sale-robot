$(function () {
     //三级联动初始化
	'use strict';
	var $distpicker = $('#distpicker');
	$distpicker.distpicker({
		province: '北京市',
		city: '北京市市辖区',
		district: '东城区'
	});
	//重置
	$('#ResetDistpicker').click(function () {
		$distpicker.distpicker('reset');
	});
	
	$('#ResetDeepDistpicker').click(function () {
		$distpicker.distpicker('reset', true);
	});
	
	$('#destroy').click(function () {
		$distpicker.distpicker('destroy');
	});
	
	$('#distpicker1').distpicker({
	    province: '省份',
		city: '城市',
		district: '地区',
		autoSelect: false
	});
	
	$('#distpicker2').distpicker({
		province: '省',
		city: '市',
		district: '区'
	});
	
	$('#distpicker3').distpicker({
		province: '浙江省',
		city: '杭州市',
		district: '西湖区'
	});
	
	$('#distpicker4').distpicker({
		placeholder: false
	});
	
	$('#distpicker5').distpicker({
		autoSelect: false
	});

});