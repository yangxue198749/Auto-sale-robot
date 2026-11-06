var VmStock = function() {
    var searchEvent = function() {
        var options = {
            type: 'POST',
            beforeSubmit: function() {
                // Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function(data) {
            	var hasLogin = Metronic.hasLogin();
	        	if(hasLogin==false){
	        		window.location.href="/";
	        		return;
	        	}
                $("#pagediv").html(data);
                // Metronic.init() ;
                //reset rightBox
                $('#stock-pagediv').empty();
            }
        };
        $("#search-form").ajaxSubmit(options);
    }
    var reset = function() {
        $('#resetButton').bind('click', function() {
            $("#search-form input:text").val("");
            $('.select2me').val(null).trigger("change");
            $('.select2me').val("请选择").trigger("change");
        });
    }
    var bindSearch = function() {
        $("#searchButton").bind("click", function() {
            searchEvent();
        });
    }
    var bindExport = function() {
        $("#exportButton").bind("click", function() {
            exportEvent();
        });
    }
    var exportEvent = function() {
        $("#search-form").attr("action","/vmStock/export");
        var options = {
            type: 'POST',
            beforeSubmit: function() {
                // Metronic.blockUI({target: "#pagediv",boxed: true});
            },
            success: function(data) {
                var hasLogin = Metronic.hasLogin();
                if(hasLogin==false){
                    window.location.href="/";
                    return;
                }
                layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
            }
        };
        $("#search-form").ajaxSubmit(options);
    }
    var bindTr = function() {
        $('#pagediv table tbody tr').bind('click', function() {
        	$('#search-stack-form input[name="vmId"]').val($(this).attr('vmId'));
        	var options = {
                    type: 'POST',
                    beforeSubmit: function() {
                        // Metronic.blockUI({target: "#pagediv",boxed: true});
                    },
                    success: function(data) {
                        $("#stock-pagediv").html(data);
                    }
                };
                $("#search-stack-form").ajaxSubmit(options);
        });
    }
    return {
        init: function() {
            bindSearch();
            bindExport();
            reset();
        },
        stockTable: function() {
            bindTr();
        }

    }
}();
