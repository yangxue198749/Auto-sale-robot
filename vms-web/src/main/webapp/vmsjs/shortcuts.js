var Shortcuts = function() {

    var searchEvent = function() {
        var options = {
            type: 'POST',
            beforeSubmit: function() {
            },
            success: function(data) {
            	var hasLogin = Metronic.hasLogin();
	        	if(hasLogin==false){
	        		window.location.href="/";
	        		return;
	        	}
                $("#pagediv").html(data);
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

    var shortcuts_sku_searchEvent = function(){
        var options = {
            type: 'POST',
            beforeSubmit:function(){
            },
            success:function (data)  {
                $("#st_vm_shortcuts_sku_div").html(data);
            }
        };
        $("#st_vm_shortcuts_sku_search_form").ajaxSubmit(options);
    }

    var bindSearch = function() {
        $("#searchButton").bind("click", function() {
            searchEvent();
        });
    }
    var bindSkuSearch = function(){
        $("#st_vm_shortcuts_sku_search_btn").bind("click", function(){
            shortcuts_sku_searchEvent();
        });
    }

    var addShortcuts1 = function() {
      $('#nextBtn_add').bind('click',function(){
        $('#select-sku-div').attr('tp','add');
        var select = $('#modelIdSelect').find("option:selected").val();
        if (select == "") {
            //alert("请选择售货机类型");
            layer.msg('请选择售货机类型', {shade:[0.6,'#fff'],time:2000});
            return false;
        }
        var strName = $('#strName').val();
        if (strName == "") {
            //alert("请输入策略名称");
            layer.msg('请输入策略名称', {shade:[0.6,'#fff'],time:2000});
            return false;
        }
        var options = {
            type: 'POST',
            success: function(data) {
                $("#add-shortcuts-div").html(data);
            }
        };
        $("#add-shortcuts-form-1").ajaxSubmit(options);
      });
    }

    var addShortcuts2 = function() {
      $('#submit_add').bind('click',function(){
        var options = {
            success: function(data) {
                $('#add-shortcuts-div').modal('hide');
                layer.msg(data.message, {
                    shade: [0.5, '#fff'],
                    time: 2000,
                });
                if (data.type = "success") {
                    searchEvent();
                }
            }
        };
        $("#add-shortcuts-form-2").ajaxSubmit(options);
      });
    }

    var editShortcuts1 = function() {
      $('#nextBtn_edit').bind('click',function(){
        $('#select-sku-div').attr('tp','edit');
        var strName = $('#strName_edit').val();
        if (strName == "") {
            //alert("请输入策略名称");
            layer.msg('请输入策略名称', {shade:[0.6,'#fff'],time:2000});
            return false;
        }
        var options = {
            type: 'POST',
            success: function(data) {
                $("#edit-shortcuts-div").html(data);
            }
        };
        $("#edit-shortcuts-form-1").ajaxSubmit(options);
      });
    }

		var editShortcuts2 = function() {
      $('#submit_edit').bind('click',function(){
        var options = {
            success: function(data) {
                $('#edit-shortcuts-div').modal('hide');
                layer.msg(data.message, {
                    shade: [0.5, '#fff'],
                    time: 2000,
                });
                if (data.type = "success") {
                    searchEvent();
                }
            }
        };
        $("#edit-shortcuts-form-2").ajaxSubmit(options);
      });
    }

    return {
        init: function() {
            bindSearch();
            reset();
        },
        bindAddShortcuts1: function() {
            addShortcuts1();
        },
        bindAddShortcuts2: function() {
            addShortcuts2();
        },
				bindEditShortcuts1: function() {
            editShortcuts1();
        },
				bindEditShortcuts2: function() {
            editShortcuts2();
        },
        searchSku:function(){
            bindSkuSearch();
        }
    }
}();
