//初始化浏览器公共对象
var Browser = getBrowserInfo();
//获得浏览器信息
function getBrowserInfo() {
    var Sys = {};
    var ua = navigator.userAgent.toLowerCase();
    var s;
    (s = ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1] :
    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
    (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
    (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
    (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
    (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
    return Sys;
}
//浏览器类型及版本
if (Browser.ie) console.log('IE: ' + Browser.ie);
if (Browser.firefox) console.log('Firefox: ' + Browser.firefox);
if (Browser.chrome) console.log('Chrome: ' + Browser.chrome);
if (Browser.opera) console.log('Opera: ' + Browser.opera);
if (Browser.safari) console.log('Safari: ' + Browser.safari);
$(function () {
    var version = '-v5.1.0';
    $('title').append(version);
    //兼容firefox
    if(Browser.firefox){
        $('.mp_container').addClass('firefox');
    }
    //兼容IE
    $('#LeftMenuGroup .iconfont.mt0').remove();
    switchMenu();
    $('#LeftMenuGroup a').each(function(){
        $(this).attr({'data-toggle':'tooltip','data-placement':'right','data-html':'true','data-original-title':$(this).text()});
    });
    $(document).on('click','#switchMenu',function(){
        $('.mp_container').toggleClass('responsiveLayout');
        if($('.mp_container').hasClass('responsiveLayout')){
            utils.setParam("switchMenu",true);
            $('#switchMenu').removeClass('close').addClass('open');
        }else{
            utils.setParam("switchMenu",false);
            $('#switchMenu').removeClass('open');
            $('#switchMenu').removeClass('open').addClass('close');
        }
    });
    //layer 初始化
    layer.config({shadeClose: true, anim: 6, offset: '100px'});
    /*//统计表格序号
    function tbodyNum() {
        var len = $('tbody tr').length;
        for (var i = 0; i < len; i++) {
            $('tbody tr:eq(' + i + ') td:first').text(i + 1);
        }
    }*/
    $("[data-toggle='tooltip']").tooltip();
    //处理table全选
    $(document).on("click", ".theadscroll li input[type='checkbox']", function () {
        if ($(this).prop("checked")) {
            $(this).parents(".main_commontable").find("table input[type='checkbox']").prop("checked", true);
        } else {
            $(this).parents(".main_commontable").find("table input[type='checkbox']").prop("checked", false);
        }
    });
    //table delete
    $(".btn_tableDelete").click(function () {
        var $this = $(this);
        var dataSet = $this.attr('data-set');
        var url = $this.attr('href');
        var chkBox = $(dataSet);
        var ids = "";
        if (chkBox.length == 0) {
            layer.msg("请先选中需要删除的内容", {shade: [0.5, '#fff'], time: 2000});
            return false;
        } else {
            $(dataSet).each(function () {
                ids += $(this).val() + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            layer.confirm('您确定要删除这' + chkBox.length + '条内容？', {
                btn: ['确定', '取消'],
                title: '提示',
            }, function () { //成功后执行
                //这里ajax处理服务端删除
                $.ajax({
                    type: 'post',
                    url: url,
                    data: {ids: ids},
                    async: true,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        if (data.type == "success") {
                            chkBox.parents("tr").remove();
                            layer.msg('已删除', {shade: [0.5, '#fff'], time: 2000});
                        } else {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                        }
                        tablePlaceholder();
                    }
                });
            }, function (index) {
                layer.close(index);//关闭窗口
            });
        }
    });

    $(".btn_tableUpdate").click(function () {
        var $this = $(this);
        var dataSet = $this.attr('data-set');
        var url = $this.attr('href');
        var chkBox = $(dataSet);
        var ids = "";
        if (chkBox.length == 0) {
            layer.msg("请先选中需要操作的数据", {shade: [0.5, '#fff'], time: 2000});
            return false;
        } else {
            $(dataSet).each(function () {
                ids += $(this).val() + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            layer.confirm('您确定要操作这' + chkBox.length + '条数据？', {
                btn: ['确定', '取消'],
                title: '提示',
            }, function () { //成功后执行
                //这里ajax处理服务端删除
                $.ajax({
                    type: 'post',
                    url: url,
                    data: {ids: ids},
                    async: true,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        if (data.type == "success") {
                            layer.msg('已处理', {shade: [0.5, '#fff'], time: 2000});
                            location.reload();
                        } else {
                            layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
                        }
                        tablePlaceholder();
                    }
                });
            }, function (index) {
                layer.close(index);//关闭窗口
            });
        }
    });
    theadScroll();
    //tablePlaceholder();
    //表格没有内容时 显示占位符
    /*function tablePlaceholder() {
        var theadLi = $(".tablePlaceholder .theadscroll li");
        var tbodyTd = $(".tablePlaceholder tbody td");
        var tbodyPlace = $(".tablePlaceholder tbody");
        if (tbodyTd.length == 0) {
            tbodyPlace.append('<tr><td style="padding:100px 0;" colspan="' + theadLi.length + '"><p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p><p>暂无内容</p></td></tr>');
            theadLi.find("input[type='checkbox']").prop("checked", false);
        }
    }*/
    //计算table tfoot colspan
    function tfootColspan() {
        var theadTd = $(".main_commontable>table>thead td");
        var tfootTd = $(".main_commontable>table>tfoot td");
        tfootTd.prop('colspan', theadTd.length);
    }
    //处理table滑动置顶 theadScroll
    function theadScroll() {
        $(window).scroll(function () {
            var scroH = $(this).scrollTop();
            var commonhead = $('.main_commonhead ').height();
            var boxH = commonhead + 27;
            var top = scroH - boxH;
            if (scroH > boxH) {
                $("#theadScroll").css({"position": "absolute", "top": top});
                $("#theadScroll").siblings("#pagediv").css({"margin-top": "47px"});
            } else {
                $("#theadScroll").css({"position": "static", "top": "auto"});
                $("#theadScroll").siblings("#pagediv").css({"margin-top": "0"});
            }
        })
    }
    //重置表单
    function reset(form) {
        $('#' + form + ' input').val("");
        $('#' + form).rest();
    }
    //转换日期格式 ：2014-11-19
    function transDate() {
        var tDate = $(".transDate").val();
        var newtDate = new Date(tDate);
        var str = newtDate.toLocaleDateString().replace(/(\d+)[^\d](\d+)[^\d](\d+)[^\d]?/, "$1-$2-$3");
        $(".transDate").val(str);
    }
    transDate();
    //处理阻止回车提交form
    document.onkeydown = function (e) {
        var theEvent = window.event || e;
        var code = theEvent.keyCode || theEvent.which;
        if (code == 13) {
            if ($('form').hasClass('unKeyCode')) {
                code = 0;
                return false;
            }
        }
    }
});
// 处理节能策略 如果选择了 “关闭屏幕”， 则隐藏 “保温箱编号 温度目标值 温控说明”。
function doWindow(id) {
    $(document).on('click', '.labelradio_group input[type="radio"]', function () {
        var idNew = $('#' + id);
        var energyType = idNew.find('.labelradio_group input[type="radio"]:checked').val();
        //console.log(energyType);
        if (energyType == 3) {
            idNew.find('.closewindow').hide();
            $('input[name="targetValue"]').val('');
        } else {
            idNew.find('.closewindow').show();
        }
    });
}

function loadSku(skuIds, skuName,skuCode, skuGroupTypeId, skuTypeId, pageNo) {
    var next = pageNo;
    $.ajax({
        url: "/sku/loadSku",
        type: "post",
        data: {skuIds: skuIds, skuName: skuName,skuCode:skuCode, skuGroupTypeId: skuGroupTypeId, skuTypeId: skuTypeId, pageNo: pageNo},
        async: false,
        datatype: "json",
        success: function (data) {
            if (data.type == "success") {
                if(data.emptyLibrary){
                    $('#addLibraryDiv').show();
                }else{
                    $("#skuUl").append(data.html);
                    next = data.next;
                    if (data.message != '') {
                        $("#skuUl").addClass('loadempty');
                        return false;
                    }
                }
            }
            else {
                layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
            }
        }
    });
    return next;
}
function loadAllSku(skuIds, skuName, skuCode, skuGroupTypeId, skuTypeId, pageNo) {
    //从公共商品库加载
    var next = pageNo;
    $.ajax({
        url: "/sku/loadAllSku",
        type: "post",
        data: {skuIds: skuIds, skuName: skuName, skuCode:skuCode, skuGroupTypeId: skuGroupTypeId, skuTypeId: skuTypeId, pageNo: pageNo},
        async: false,
        datatype: "json",
        success: function (data) {
            if (data.type == "success") {
                $("#skuUl").append(data.html);
                next = data.next;
                if (data.message != '') {
                    $("#skuUl").addClass('loadempty');
                    return false;
                }
            }
            else {
                layer.msg(data.message, {shade: [0.5, '#fff'], time: 2000});
            }
        }
    });
    return next;
}

utils = {
    setParam : function (name,value){
        localStorage.setItem(name,value)
    },
    getParam : function(name){
        return localStorage.getItem(name)
    }
};

var switchMenu = function(){
    var first = localStorage.getItem('switchMenu')==null?true:false;
    var bodyWidth = $('body').width();
    if(!first){
        var getSwitchMenu = utils.getParam("switchMenu");
        if(getSwitchMenu == true || getSwitchMenu == 'true'){
            $('#switchMenu').removeClass('close').addClass('open');
            $('.mp_container').addClass('responsiveLayout');
        }else{
            $('#switchMenu').removeClass('open').addClass('close');
            $('.mp_container').removeClass('responsiveLayout');
        }
    }else{
        if(bodyWidth < 1366){
            $('.mp_container').addClass('responsiveLayout');
            $('#switchMenu').removeClass('close').addClass('open');
            utils.setParam("switchMenu",true);
        }else{
            $('.mp_container').removeClass('responsiveLayout');
            $('#switchMenu').removeClass('open').addClass('close');
            utils.setParam("switchMenu",false);
        }
    }
};
