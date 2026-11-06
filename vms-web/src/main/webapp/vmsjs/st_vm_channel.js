var StVmChannel = function(){
	var searchEvent = function(){
		var options = { 
	    		type: 'POST',
	    		beforeSubmit:function(){
//	    			Metronic.blockUI({target: "#pagediv",boxed: true});
	    		},
		        success:function (data)  { 
		        	var hasLogin = Metronic.hasLogin();
		        	if(hasLogin==false){
		        		window.location.href="/";
		        		return;
		        	}
		    		$("#pagediv").html(data);
//		    		Metronic.init() ;
				}   
		    }; 
	    $("#search-form").ajaxSubmit(options);
	}
	var searchSkuEvent=function(){
		var options = { 
	    		type: 'POST',
	    		beforeSubmit:function(){
	    		},
		        success:function (data)  { 
		    		$("#skuDiv").html(data);
				}   
		    }; 
	    $("#search-sku-form").ajaxSubmit(options);
	}
	var bindSeaechSku = function(){
		$("#search-sku-button").bind("click", function(){
			searchSkuEvent();
		});
	}
	var reset = function(){
		$('#resetButton').bind('click',function(){
			$("#search-form input:text").val("");
			$('.select2me').val(null).trigger("change");
			$('.select2me').val("请选择").trigger("change");
				
		});
	}
	var bindSearch = function(){
		$("#searchButton").bind("click", function(){
			searchEvent();
		});
	}
	var addStVmNextDialog = function(){
		$('#addNextButton').bind('click',function(){
			$('#addChannelDetailDiv').html("");
			var addStVmName = $("#addStVmName").val();
			var addVmModelId = $("#addVmModelId").val();
			var addStVmDesc = $("#addStVmDesc").val();
			if(addStVmName == ''){
				layer.msg("请填写策略名称",{shade: [0.5, '#fff'],time:1000,});
				return false;
			}
			if(addVmModelId == ''){
				layer.msg("请选择售货机型号",{shade: [0.5, '#fff'],time:1000,});
				return false;
			}
			var url = $(this).attr("data-href");
			$.ajax({
			    type : 'post',
			    url : url,
			    data : {"addStVmName":addStVmName, "addVmModelId":addVmModelId,"addStVmDesc":addStVmDesc},
			    async : true,
			    dataType : 'json',
			    success : function(data) {
			    	$('#addStVmChannel').modal('hide');
			    	$('#addChannelDetailDiv').html(data.html);
                    Metronic.imgEmpty('addChannelDetailDiv');
			    	var sortBtn = $('#addSetSort');
			    	sortBtn.attr('href',sortBtn.attr('href')+"?strategyId="+$("#addChannelDetailDiv input[name='relateStrategyId']").val());
			    	searchEvent();
			    }
			});
			
		});
	}
	var submitEditMergeChannel = function(){
		$('#editMergeButton').bind('click',function(){
			var url = $(this).attr("href");
			var checks = $('#editChannelDetailDiv li.on input');
			if(checks.length ==0){
				layer.msg("请选择两个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length == 1){
				layer.msg("请选择两个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length > 2){
				layer.msg("请选择两个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length == 2){
				var rols = "";
				var indexs = "";
				var channelIds = "";
				checks.each(function(){
					rols += $(this).attr('rol') + ",";
					indexs += $(this).attr('index') + ",";
					channelIds += $(this).val()+",";
				});
				channelIds = channelIds.substring(0,channelIds.length-1);
				if(rols.split(",")[0]!=rols.split(",")[1]){
					layer.msg("请选择同一行货道进行合并",{shade: [0.5, '#fff'],time:1000,});
					return ;
				}
				if(indexs.split(",")[1]-indexs.split(",")[0]!=1){
					layer.msg("请选择相邻货道进行合并",{shade: [0.5, '#fff'],time:1000,});
					return ;
				}
				var firstChannelId = channelIds.split(",")[0];
				var secondChannelId = channelIds.split(",")[1];
				$.ajax({
				    type : 'post',
				    url : url,
				    data : {"firstChannelId":firstChannelId,"secondChannelId":secondChannelId},
				    async : true,
				    dataType : 'json',
				    success : function(data) {
				    	if(data.type=="success"){
				    		$('#editChannelDetailDiv').html(data.html);
                            Metronic.imgEmpty('editChannelDetailDiv');
				    	}else{
				    		layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
				    	}
				    }
				});
			}
		});
	}
	var submitAddMergeChannel = function(){
		$('#addMergeChannel').bind('click',function(){
			var url = $(this).attr("href");
			var checks = $('#addChannelDetailDiv li.on input');
			if(checks.length ==0){
				layer.msg("请选择两个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length == 1){
				layer.msg("请选择两个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length > 2){
				layer.msg("请选择两个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length == 2){
				var rols = "";
				var indexs = "";
				var channelIds = "";
				checks.each(function(){
					rols += $(this).attr('rol') + ",";
					indexs += $(this).attr('index') + ",";
					channelIds += $(this).val()+",";
				});
				channelIds = channelIds.substring(0,channelIds.length-1);
				if(rols.split(",")[0]!=rols.split(",")[1]){
					layer.msg("请选择同一行货道进行合并",{shade: [0.5, '#fff'],time:1000,});
					return ;
				}
				if(indexs.split(",")[1]-indexs.split(",")[0]!=1){
					layer.msg("请选择相邻货道进行合并",{shade: [0.5, '#fff'],time:1000,});
					return ;
				}
				var firstChannelId = channelIds.split(",")[0];
				var secondChannelId = channelIds.split(",")[1];
				$.ajax({
				    type : 'post',
				    url : url,
				    data : {"firstChannelId":firstChannelId,"secondChannelId":secondChannelId},
				    async : true,
				    dataType : 'json',
				    success : function(data) {
				    	if(data.type=="success"){
				    		$('#addChannelDetailDiv').html(data.html);
                            Metronic.imgEmpty('addChannelDetailDiv');
				    	}else{
				    		layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
				    	}
				    }
				});
			}
		});
	}
	var submitAddCancelMerge = function(){
		$("#addCancelMerge").bind('click',function(){
			var url = $(this).attr('href');
			var checks = $('#addChannelDetailDiv li.on input');
			if(checks.length !=1){
				layer.msg("请选择一个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length == 1){
				$.ajax({
				    type : 'post',
				    url : url,
				    data : {"channelId":checks.val()},
				    async : true,
				    dataType : 'json',
				    success : function(data) {
				    	if(data.type=="success"){
				    		$('#addChannelDetailDiv').html(data.html);
                            Metronic.imgEmpty('addChannelDetailDiv');
				    	}else{
				    		layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
				    	}
				    }
				});
				
			}
		});
	}
	var submitEditCancelMerge = function(){
		$("#editCancelMerge").bind('click',function(){
			var url = $(this).attr('href');
			var checks = $('#editChannelDetailDiv li.on input');
			if(checks.length !=1){
				layer.msg("请选择一个货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			if(checks.length == 1){
				$.ajax({
				    type : 'post',
				    url : url,
				    data : {"channelId":checks.val()},
				    async : true,
				    dataType : 'json',
				    success : function(data) {
				    	if(data.type=="success"){
				    		$('#editChannelDetailDiv').html(data.html);
                            Metronic.imgEmpty('editChannelDetailDiv');
				    	}else{
				    		layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
				    	}
				    }
				});
				
			}
		});
	}
	var submitSelectSku = function(){
		$('#relateButton').bind('click',function(){
			
			var checks = $("#skuDiv li.on input[name='skuId']");
			if(checks.length!=1){
				layer.msg("请选择一个商品",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}else{
				var skuId = checks.val();
				$.ajax({
				    type : 'post',
				    url : '/stVmChannel/relateSku',
				    data : {"channelIds":$('#relateChannelIds').val(),"skuId":skuId},
				    async : true,
				    dataType : 'json',
				    success : function(data) {
				    	if(data.type=="success"){
				    		layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
				    		
				    		if('add'==$('#relateType').val()){
				    			getChannelDetail('addChannelDetailDiv');
				    		}else{
				    			getChannelDetail('editChannelDetailDiv');
				    		}
				    		$('#SelectSku').modal('hide');
				    		$('#SelectSku').html("");
				    	}else{
				    		layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
				    	}
				    }
				});
			}
		});
	}
	var getChannelDetail = function (ele){
		var strategy = $('#'+ele+" input[name='relateStrategyId']");
		if('undefined' != strategy){
			$.ajax({
			    type : 'post',
			    url : '/stVmChannel/getChannelDetail',
			    data : {"strategyId":strategy.val()},
			    async : true,
			    dataType : 'json',
			    success : function(data) {
			    	if(data.type=="success"){
			    		$("#"+ele).html(data.html);
                        Metronic.imgEmpty(ele);
			    	}
			    }
			});
		}
	}
	var submitAddCancelRelate = function(){
		$('#addCancelRelate').bind('click',function(){
			var url = $(this).attr("href");
			var checks = $('#addChannelDetailDiv li.on input');
			if(checks.length ==0){
				layer.msg("请选择货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			
			var channelIds = "";
			checks.each(function(){
				channelIds += $(this).val()+",";
			});
			channelIds = channelIds.substring(0,channelIds.length-1);
			
			$.ajax({
			    type : 'post',
			    url : url,
			    data : {"channelIds":channelIds},
			    async : true,
			    dataType : 'json',
			    success : function(data) {
			    	if(data.type=="success"){
			    		$("#addChannelDetailDiv").html(data.html);
                        Metronic.imgEmpty('addChannelDetailDiv');
			    	}
			    }
			});
			
		});
	}
	var submitEditCancelRelate = function(){
		$('#editCancelRelate').bind('click',function(){
			var url = $(this).attr("href");
			var checks = $('#editChannelDetailDiv li.on input');
			if(checks.length ==0){
				layer.msg("请选择货道",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			
			var channelIds = "";
			checks.each(function(){
				channelIds += $(this).val()+",";
			});
			channelIds = channelIds.substring(0,channelIds.length-1);
			
			$.ajax({
			    type : 'post',
			    url : url,
			    data : {"channelIds":channelIds},
			    async : true,
			    dataType : 'json',
			    success : function(data) {
			    	if(data.type=="success"){
			    		$("#editChannelDetailDiv").html(data.html);
                        Metronic.imgEmpty('editChannelDetailDiv');
			    	}
			    }
			});
			
		});
	}
	var submitChangeSort = function(){
		$('#chengeSortBtn').bind('click',function(){
			var url = $(this).attr("href");
			var checks = $('#showSortDiv li.on input');
			if(checks.length !=2){
				layer.msg("请选择两个列表页",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			
			var sortIds = "";
			checks.each(function(){
				sortIds += $(this).val()+",";
			});
			sortIds = sortIds.substring(0,sortIds.length-1);
			var firstSortId = sortIds.split(",")[0];
			var secondSortId = sortIds.split(",")[1];
			$.ajax({
			    type : 'post',
			    url : url,
			    data : {"firstSortId":firstSortId,"secondSortId":secondSortId},
			    async : true,
			    dataType : 'json',
			    success : function(data) {
			    	layer.msg(data.message,{shade: [0.5, '#fff'],time:500,});
			    	if(data.type=="success"){
			    		$("#showSortDiv").html(data.html);
			    	}
			    }
			});
			
		});
	}
	var submitCopySort = function () {
		$("#copySortBtn").bind("click",function () {
			var url = $(this).attr("href");
			var checks = $('#showSortDiv li.on input');
			if(checks.length !=1){
				layer.msg("请选择一个货架",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var sortIds = "";
			checks.each(function(){
				sortIds += $(this).val()+",";
			});
			sortIds = sortIds.substring(0,sortIds.length-1);
			var sortId = sortIds.split(",")[0];
			$.ajax({
				type : 'post',
				url : url,
				data : {"sortId":sortId},
				async : true,
				dataType : 'json',
				success : function(data) {
					layer.msg(data.message,{shade: [0.5, '#fff'],time:500,});
					if(data.type=="success"){
						$("#showSortDiv").html(data.html);
					}
				}
			});

		});
	}

	var submitDeleteSort = function () {
		$("#deleteSortBtn").bind("click",function () {
			var url = $(this).attr("href");
			var checks = $('#showSortDiv li.on input');
			if(checks.length !=1){
				layer.msg("请选择一个货架",{shade: [0.5, '#fff'],time:1000,});
				return ;
			}
			var sortIds = "";
			checks.each(function(){
				sortIds += $(this).val()+",";
			});
			sortIds = sortIds.substring(0,sortIds.length-1);
			var sortId = sortIds.split(",")[0];
			$.ajax({
				type : 'post',
				url : url,
				data : {"sortId":sortId},
				async : true,
				dataType : 'json',
				success : function(data) {
					layer.msg(data.message,{shade: [0.5, '#fff'],time:500,});
					if(data.type=="success"){
						$("#showSortDiv").html(data.html);
					}
				}
			});

		});
	}
	var bindEditFullSku = function () {
		$(".editbtn").bind('click',function(){
			var value = $("#editSkuFullForm input[name='fullval']").val();
			if(value==""){
				layer.msg("请输入数字",{shade: [0.5, '#fff'],time:1000,});
				return false;
			}else{
				if(isNaN(value)){
					layer.msg("请输入数字",{shade: [0.5, '#fff'],time:1000,});
					value="";
				}else{
					var r = /^([1-9]\d{0,1}|100)$/;　　//0-100的正整数
					if(!r.test(value)){
						layer.msg("请填写1-100的正整数",{shade: [0.5, '#fff'],time:1000,});
						return false;
					}
					var channelId = $(".modal-body [name='channelId']").val();
					var fullval = $(".textSum [name='fullval']").val();
					$.ajax({
						type : 'post',
						url : '/stVmChannel/editSkuFull',
						data : {"channelId":channelId,
							"fullval":fullval},
						async : true,
						dataType : 'json',
						cache : false,
						success : function(data) {
							layer.msg(data.message,{shade: [0.5, '#fff'],time:1000,});
							if(data.type=="success"){
								getChannelDetail('addChannelDetailDiv');
								getChannelDetail('editChannelDetailDiv');
								$('#setFullSku').modal('hide');
								$('#setFullSku').html("");
							}
						}
					});
				}
				return true;
			}

		});
	}
	return{
		init:function(){
			bindSearch();
			reset();
		},
		addStVmNext:function(){
			addStVmNextDialog();
		},
		editMergeChannel:function(){
			submitEditMergeChannel();
		},
		addMergeChannel:function(){
			submitAddMergeChannel();
		},
		addCancelMerge:function(){
			submitAddCancelMerge();
		},
		editCancelMerge:function(){
			submitEditCancelMerge();
		},
		searchSku:function(){
			bindSeaechSku();
			submitSelectSku();
		},
		addCancelRelate:function(){
			submitAddCancelRelate();
		},
		editCancelRelate:function(){
			submitEditCancelRelate();
		},
		changeSort:function(){
			submitChangeSort();
		},
		copySort:function () {
			submitCopySort();
		},
		deleteSort:function () {
			submitDeleteSort();
		},
		editFullSku:function () {
			bindEditFullSku();
		}
	}
}();