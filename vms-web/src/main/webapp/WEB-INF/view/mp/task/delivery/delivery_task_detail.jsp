<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>配送工单-详情</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="配送工单">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
    <%--<link rel="stylesheet" type="text/css" href="${path}/mp-static/css/moHe.li.css?v=20171202" />--%>
</head>
<body>
<div class="mh_container">
    <div class="detailBox">
        <aside>
            <p>${task.mtVm.node.nodeName}</p>
            <strong>${task.mtVm.innerCode}</strong>
        </aside>
        <article>
            <section>
                <p><strong><span>${task.mtVm.vmDesc}</span></strong></p>
                <p class="moretext">${task.mtVm.node.nodeAddress}</p>
            </section>
        </article>
    </div>
    <!-- task the key -->
    <c:if test="${task.taskVmStatus==2||task.taskVmStatus==1}">
        <div class="detailBox head key">
            <div class="title_news"><button type="button" class="touchbtn">生成开门密码</button><b class="f24 fr keycode" id="pwd"></b></div>
        </div>
    </c:if>
    <input type="hidden" value="${task.taskVmId}" id="taskVmId">
    <!-- / task the key -->
    <!-- boxModel -->
    <div class="boxmodel">
        ${html}
    </div>
    <!-- / boxModel -->
    <!---- taskDetailGroup ---->
    <div class="taskDetailGroup" id="taskDetailGroup">
        <!-- tabHeader -->
        <div class="tabHead">
            <ul>
                <li class="active">补货</li>
                <li>清货</li>
                <li>调量</li>
            </ul>
        </div>
        <!-- / tabHeader -->
        <!-- taskDetailList -->
        <div id="taskDetailList">
            <div class="detailGroup fillList" id="fillList"></div>
            <div class="detailGroup cleanList hide" id="cleanList"></div>
            <div class="detailGroup adjustList hide" id="adjustList"></div>
        </div>
        <!-- / taskDetailList -->
    </div>
    <!---- / taskDetailGroup ---->
</div>

<!--watermark-->
<div class="watermark"></div>
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        FastClick.attach(document.body);
        sessionStorage.setItem("need-refresh", true);
    });
</script>
<script type="text/javascript" src="${path}/mp-static/js/WorkOrder.js?v=201801021040"></script>
<%--<script type="text/javascript" src="${path}/mp-static/js/SendWorkOrder_detail.js"></script>--%>
<%--<script type="text/javascript" src="${path}/mp-static/js/ShippingOrder_detail.js?v=201712271728"></script>--%>
<script type="text/javascript" src="${path}/mp-static/js/taskDetailMethods.js?v=20180607"></script>

</body>
</html>
