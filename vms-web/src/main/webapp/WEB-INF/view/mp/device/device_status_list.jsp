<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>设备状态</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="设备状态">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
    <script type="text/javascript" src="${path}/mp-static/js/DeviceStatus.js?v=20171019"></script>
</head>
<body>
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
            <div class="searchBtn">搜索</div>
        </div>
    </div>
</div>
<div class="mh_container">
<!-- / 5.0.0a 置顶搜索 -->

    <!--设备状态列表 start-->
    <div class="detailBox ver2">
        <aside>
            <section>
                <strong id="total"><div class="loader">Loading...</div></strong>
                <p>在运营设备</p>
            </section>
        </aside>
        <aside>
            <strong id="bad"><div class="loader">Loading...</div></strong>
            <p>问题设备</p>
        </aside>
    </div>

    <div class="listHeader ver2" id="topmenu">
        <ul>
            <li>售货机</li>
            <li>设备状态</li>
            <li>网络状态</li>
            <li>商品状态</li>
        </ul>
    </div>

    <!--list body-->
    <div class="listBody ver2">
        <ul id="vm_ul"></ul>
    </div>
</div>

<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        //搜索
        $('#searchForm .searchBtn').click(function(){
            var innerCode = $('#innerCode').val();
            loadData(innerCode);
        });


        FastClick.attach(document.body);
        loadData();
        $(document.body).pullToRefresh().on("pull-to-refresh", function() {
            setTimeout(function() {
                loadData();
                $(document.body).pullToRefreshDone(); // 重置下拉刷新
            }, 500);   //模拟延迟
        });
    });
</script>
<!----------------Common js end------------------>

</body>
</html>
