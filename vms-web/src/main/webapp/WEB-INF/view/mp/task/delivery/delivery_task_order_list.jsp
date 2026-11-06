<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>提货单</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="提货单">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container pb60">

    <div class="detailWrap mt10">
        <!---->
        <div class="weui_cells weui_cells_access mt0 bg_green">
            <a class="weui_cell p15" href="javascript:;">
                <div class="weui_cell_bd weui_cell_primary">
                    <p class="cf_white f14">商品名称</p>
                </div>
                <div class="weui_cell_bd"><span class="cf_white f12">建议提货量(件)</span></div>
            </a>
        </div>

        <div class="listBody ver2">
                <c:choose>
                    <c:when test="${empty orderMap}">
                        <div class="unview tips">
                            <section class="txtgroup">
                                <p>当前无需提货商品，请先添加任务。</p>
                            </section>
                        </div>
                    </c:when>
                    <c:otherwise>
            <ul>
                <c:forEach items="${orderMap}" var="item">
                    <li>
                        <div class="weui_cells weui_cells_access mt0">
                            <a class="weui_cell p15" href="javascript:;">
                                <div class="weui_cell_bd weui_cell_primary">
                                    <p class="cf6 f12">${item.key.skuName}</p>
                                </div>
                                <div class="weui_cell_bd"><b class="cf_blue">${item.value}</b></div>
                            </a>
                        </div>
                    </li>
                </c:forEach>

            </ul>
                    </c:otherwise>
                </c:choose>
        </div>



    </div>

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

<!--- WorkOrder 交互js start--->
<script type="text/javascript" src="${path}/mp-static/js/WorkOrder_bill.js"></script>
<!--- WorkOrder 交互js end --->


</body>
</html>
