var VmStatus = function() {
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
    var bindSearch = function() {
        $("#searchButton").bind("click", function() {
            searchEvent();
        });
    }
    return {
        init: function() {
            bindSearch();
        },
    }
}();
