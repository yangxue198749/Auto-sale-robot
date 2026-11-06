<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>工单列表</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="工单列表">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">
    <!--选择工单类型 start-->
    <div class="weui_cells_title">选择工单类型</div>
    <div class="weui_cells weui_cells_access mt15">
        <!--配送工单-->
        <a class="weui_cell" href="${path}/mp/mpTask/delivery/list">
            <div class="weui_cell_hd"></div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf6">配送工单</p>
            </div>
            <div class="weui_cell_ft"></div>
        </a>
        <!--维护工单-->
        <a class="weui_cell" href="${path}/mp/mpTask/repair/list">
            <div class="weui_cell_hd"></div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf6">维护工单</p>
            </div>
            <div class="weui_cell_ft"></div>
        </a>
        <!--投放工单-->
        <a class="weui_cell" href="${path}/mp/mpTask/deploy/list">
            <div class="weui_cell_hd"></div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf6">投放工单</p>
            </div>
            <div class="weui_cell_ft"></div>
        </a>
        <!--撤机工单-->
        <a class="weui_cell" href="${path}/mp/mpTask/withdraw/list">
            <div class="weui_cell_hd"></div>
            <div class="weui_cell_bd weui_cell_primary">
                <p class="f14 cf6">撤机工单</p>
            </div>
            <div class="weui_cell_ft"></div>
        </a>
    </div>
    <!--选择工单类型 end-->

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        FastClick.attach(document.body);
    });
</script>
<!----------------Common js end------------------>

</body>
</html>
