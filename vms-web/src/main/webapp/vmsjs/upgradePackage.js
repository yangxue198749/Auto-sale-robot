var UpgradePackage = function() {
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
    return {
        init: function() {
            bindSearch();
            reset();
        },
        searchForm: function() {
            searchEvent();
        }
    }
}();
