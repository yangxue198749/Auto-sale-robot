$(function(){	
	
	    //默认
		$('#DateDefault').fdatepicker({
		    format: 'yyyy-mm-dd'
		});
		
		//使用class
		$('.DateDefault').fdatepicker({
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
		//限制可选最小时间。today
		$('.DateStartTody').fdatepicker({
			format: 'yyyy-mm-dd',
			onRender: function (date) {
				return date.valueOf() < now.valueOf() ? 'disabled' : '';
			}
			//startDate: new Date()
		});
		//日期区间
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
				//return date.valueOf() <= checkin.date.valueOf() ? 'disabled' : '';
                return date.valueOf() < checkin.date.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function (ev) {
			checkout.hide();
		}).data('datepicker');
		
		//日期区间
		var checkinOne = $('#DateStartOne').fdatepicker({
			format: 'yyyy-mm-dd',
			//onRender: function (date) {
			//	return date.valueOf() < now.valueOf() ? 'disabled' : '';
			//}
		}).on('changeDate', function (ev) {
			if (ev.date.valueOf() > checkoutOne.date.valueOf()) {
				var newDate = new Date(ev.date)
				newDate.setDate(newDate.getDate() + 1);
				checkoutOne.update(newDate);
			}
			checkinOne.hide();
			$('#DateEndOne')[0].focus();
		}).data('datepicker');
		var checkoutOne = $('#DateEndOne').fdatepicker({
			format: 'yyyy-mm-dd',
			onRender: function (date) {
				return date.valueOf() < checkinOne.date.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function (ev) {
			checkoutOne.hide();
		}).data('datepicker');

		//日期区间
		var checkinTwo = $('#DateStartTwo').fdatepicker({
			format: 'yyyy-mm-dd',
			//onRender: function (date) {
			//	return date.valueOf() < now.valueOf() ? 'disabled' : '';
			//}
		}).on('changeDate', function (ev) {
			if (ev.date.valueOf() > checkoutTwo.date.valueOf()) {
				var newDate = new Date(ev.date)
				newDate.setDate(newDate.getDate() + 1);
				checkoutTwo.update(newDate);
			}
            checkinTwo.hide();
			$('#DateEndTwo')[0].focus();
		}).data('datepicker');
		var checkoutTwo = $('#DateEndTwo').fdatepicker({
			format: 'yyyy-mm-dd',
			onRender: function (date) {
				return date.valueOf() < checkinTwo.date.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function (ev) {
			checkoutTwo.hide();
		}).data('datepicker');
	
	
})
