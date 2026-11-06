var page = 1;
var init = false;

$(function () {
    //处理设备导航置顶
    /*	var navH = $("#topmenu").offset().top;
     $(window).scroll(function(){
     var scroH = $(this).scrollTop();
     if(scroH>=navH){
     $("#topmenu").addClass("fixed");
     }else if(scroH<navH){
     $("#topmenu").removeClass("fixed");
     }
     });*/


})

function loadData(innerCode) {
    $.ajax({
        url: "/mp/vmStatus/load",
        data:{innerCode:innerCode},
        type: "POST",
        success: function (data) {
            var json = eval(data);
            if (json.type == 'ok') {
                if(json.summary.total==0){
                    $('#vm_ul').html("<p class=\"searchEmpty\">未找到相关内容，请确认搜索内容</p>");
                }else{
                    $("#total").html(json.summary.total);
                    $("#bad").html(json.summary.bad);
                    $('#vm_ul').empty();
                    $.each(json.data, function (index, item) {
                        $('#vm_ul').append('<li>' +
                            '<div class="weui_cells weui_cells_access mt0">' +
                            '<a class="weui_cell p15" href="/mp/vmStatus/detail?vmId=' + item.vmId + '">' +
                            '<div class="weui_cell_bd weui_cell_primary">' +
                            '<ul class="listgroup">' +
                            '<li><span>' + item.innerCode + '</span></li>' +
                            '<li><i class="icon_radius ' + item.vmcStatus + '"></i></li>' +
                            '<li><i class="icon_radius ' + item.networkStatus + '"></i></li>' +
                            '<li><i class="icon_radius ' + item.stockStatus + '"></i></li>' +
                            '</ul>'+
                            '<p class="cf9 f12">' + item.nodeName + '</p>'+
                            '</div>' +
                            '<div class="weui_cell_ft"></div></a></div></li>');
                    });
                }
            } else {
                $.toast(json.message)
            }
        }
    });

}


