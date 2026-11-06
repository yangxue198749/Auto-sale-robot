<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html;charset=utf-8">
    <meta charset="utf-8">
    <title>出货异常</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="defult">
    <meta name="apple-mobile-web-app-title" content="出货异常">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<!-- body 顶部加上如下代码 -->
<div class="weui-pull-to-refresh-layer">
    <div class="pull-to-refresh-arrow"></div> <!-- 上下拉动的时候显示的箭头 -->
    <div class="pull-to-refresh-preloader"></div> <!-- 正在刷新的图标 -->
    <div class="down">下拉刷新</div><!-- 下拉过程显示的文案 -->
    <div class="up">释放刷新</div><!-- 下拉超过50px显示的文案 -->
    <div class="refresh">正在刷新...</div><!-- 正在刷新时显示的文案 -->
</div>
<!-- 5.0.0a 置顶搜索 -->
<div class="searchFixed">
    <div class="topSearch">
        <div class="searchInput" id="searchForm">
            <input type="text" name="innerCode" id="innerCode" value="">
            <input type="hidden" name="date" id="formDate" value="">
            <div class="searchBtn">搜索</div>
        </div>
    </div>
</div>
<div class="mh_container">
    <!-- / 5.0.0a 置顶搜索 -->
    <%--<!-- 5.0.0a 置顶搜索 -->
    <div class="searchFixed">
        <div class="topSearch">
            <div class="searchInput">
                <form action="" id="searchForm">
                    <input type="text" name="name" placeholder="搜索" value="">
                    <input type="hidden" name="date" id="formDate" value="">
                </form>
            </div>
        </div>
    </div>
    <div class="mh_container mt60">
        <!-- / 5.0.0a 置顶搜索 -->--%>
    <!--tab date start-->
    <div class="listHeader date mt20" id="topmenu">
        <section onclick="setTime(-1)" class="boxlf weui_cell_fl2">前一天</section>
        <span class="weui_cell_down2">
                 <input class="dateinput" id="dateToday" type="text" readonly>
             </span>
        <section onclick="setTime(1)" class="boxrt weui_cell_ft2">后一天</section>
    </div>
    <!--tab date end-->

    <div class="detailWrap outErrorDetail mt10">


    </div>
    <%--<div id="alreadyFinish" class="detailWrap mt10">
        <p> 已全部加载完毕 </p>
    </div>--%>
    <div id="alreadyFinish" class="alreadyFinish">
        <p> 已全部加载完毕 </p>
    </div>
    <div class="weui-infinite-scroll mb20">
        <div class="infinite-preloader"></div><!-- 加载 -->
        正在加载... <!-- 文案，可以自行修改 -->
    </div>

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<script type="text/javascript" src="/mp-static/plus/lib/jquery-2.2.3.min.js"></script>
<script type="text/javascript" src="/mp-static/plus/js/jquery-weui.js"></script>
<script type="text/javascript" src="/mp-static/plus/lib/fastclick.js"></script>
<script>
    var hasNext = true;
    var pageNo = 1;
    var loading = false;  //状态标记
    $(function () {
        $('#dateToday').val(todayDate());
        $('#formDate').val(todayDate());
        $(".weui-infinite-scroll").hide();//显示正在加载标签
        $("#alreadyFinish").hide();
        FastClick.attach(document.body);
        $(document.body).pullToRefresh().on("pull-to-refresh", function () {
            setTimeout(function () {
                window.location.reload();
                $(document.body).pullToRefreshDone(); // 重置下拉刷新
            }, 500);   //模拟延迟
        });

        infinite();

        getOutErrorList();

        $('#searchForm .searchBtn').click(function () {
            var innerCode = $('#innerCode').val();
            $(".outErrorDetail").html('');
            getOutErrorList(innerCode);
        });

    });
    function getOutErrorList(innerCode) {
        loading = true;
        $.ajax({
            url: "/mp/outSkuError/listPage",
            data: {"pageNo": pageNo, "innerCode": innerCode, "time": $("#dateToday").val()},
            type: "POST",
            success: function (data) {
                loading = false;
                $(".outErrorDetail").append(data.html);
                if (data.hasNextPage) {
                    $(".weui-infinite-scroll").hide();//隐藏正在加载标签
                    pageNo++;
                    $("#alreadyFinish").hide();
                } else {
                    if (pageNo == 1 && innerCode.length>0 && data.html == '') {
                        $(".outErrorDetail").html("<p class=\"searchEmpty\">未找到相关内容，请确认搜索内容</p>");
                    }
                    hasNext = false;
                    $(document.body).destroyInfinite();//销毁加载事件
                    $(".weui-infinite-scroll").hide();//隐藏正在加载标签
                    $("#alreadyFinish").show();
                }
            }
        });
    }
    function setTime(day) {
        $("#dateToday").val(addDate($("#dateToday").val(), day));
        $(".outErrorDetail").html("");
        pageNo = 1;
        hasNext = true;
        loading = false;
        getOutErrorList();
        infinite();
        $("#innerCode").val('');
        $(".outErrorDetail").html('');
        console.log($("#dateToday").val());
        $('#formDate').val($("#dateToday").val());

    }
    function infinite() {
        $(document.body).infinite().on("infinite", function () {
            if (loading) return;
            if (!hasNext)return;
            loading = true;
            $(".weui-infinite-scroll").show();//显示正在加载标签
            setTimeout(function () {
                loading = false;
                var innerCode = $('#innerCode').val();
                getOutErrorList(innerCode);
            }, 500);   //模拟延迟
        });
    }
    function addDate(date, days) {
        var d = new Date(date);
        d.setDate(d.getDate() + days);
        var month = d.getMonth() + 1;
        var day = d.getDate();
        if (month < 10) {
            month = "0" + month;
        }
        if (day < 10) {
            day = "0" + day;
        }
        var val = d.getFullYear() + "-" + month + "-" + day;
        return val;
    }
    //calendar
    $("#dateToday").calendar({
        //value: ['2016-12-12'],
        onChange: function (p, values, displayValues) {
            console.log(values, displayValues);
            console.log('。。。');
            $("#innerCode").val('');
            $("#dateToday").val(values);
            $(".outErrorDetail").html('');
            getOutErrorList();

        }
    });
    //设置日期默认值
    function todayDate() {
        var mydate = new Date();
        var str = "" + mydate.getFullYear() + "-";
        str += (mydate.getMonth() < 9 ? "0" + (mydate.getMonth() + 1) : (mydate.getMonth() + 1)) + "-";
        str += mydate.getDate() < 10 ? "0" + mydate.getDate() : mydate.getDate();
        console.log('1:' + str);
        return str;
    }
</script>
<!----------------Common js end------------------>

<!---SalesPipeline_detail 交互js start--->
<%--<script type="text/javascript" src="/mp-static/js/AbnormalShipment.js"></script>--%>
<!---SalesPipeline_detail 交互js end--->
</body>
</html>