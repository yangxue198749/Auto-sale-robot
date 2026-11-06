$(function(){	
	
	    //默认
		$('#DateDefault').fdatepicker({
		    format: 'yyyy-mm-dd'
		});
		
		//附带时间戳
		$('#DateTime').fdatepicker({
			format: 'yyyy-mm-dd hh:ii',
			pickTime: true
		});
		
		//只有时间
		$('.DateOnlyTime').fdatepicker({
			format: 'hh:ii',
			pickTime: true
		});
		
		//富选不常用
		$('#DateFull').fdatepicker();
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
		
		//默认日期区间
		var checkin = $('#DateStart').fdatepicker({
			format: 'yyyy-mm-dd',
			//onRender: function (date) {
			//	return date.valueOf() < now.valueOf() ? 'disabled' : '';
			//}
		}).on('changeDate', function (ev) {
			if (ev.date.valueOf() > checkout.date.valueOf()) {
				var newDate = new Date(ev.date)
				newDate.setDate(newDate.getDate() + 1);
				checkout.update(newDate);
			}
			checkin.hide();
			$('#DateEnd')[0].focus();
		}).data('datepicker');
		var checkout = $('#DateEnd').fdatepicker({
			format: 'yyyy-mm-dd',
			onRender: function (date) {
				return date.valueOf() <= checkin.date.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function (ev) {
			checkout.hide();
		}).data('datepicker');
		
		
		//新增 DateStart_top - DateEnd_top 日期区间
		var checkstart = $('#DateStart_top').fdatepicker({
			format: 'yyyy-mm-dd',
			//onRender: function (date) {
			//	return date.valueOf() < now.valueOf() ? 'disabled' : '';
			//}
		}).on('changeDate', function (ev) {
			if (ev.date.valueOf() > checkend.date.valueOf()) {
				var newDate = new Date(ev.date)
				newDate.setDate(newDate.getDate() + 1);
				checkend.update(newDate);
			}
			checkstart.hide();
			$('#DateEnd_top')[0].focus();
		}).data('datepicker');
		var checkend = $('#DateEnd_top').fdatepicker({
			format: 'yyyy-mm-dd',
			onRender: function (date) {
				return date.valueOf() <= checkstart.date.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function (ev) {
			checkend.hide();
		}).data('datepicker');
	
	
})
