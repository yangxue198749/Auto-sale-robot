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

    <div class="detailWrap w160">
        <header>
            <span>商品名称</span>
            <section>
                <ul>
                    <li>提货量(件)</li>
                    <li>操作</li>
                </ul>
            </section>
        </header>
        <hgroup class="p0">
    <c:choose>
        <c:when test="${empty orderMap}">
            <div class="unview tips">
                <section class="txtgroup">
                    <p>当前无需提货商品，请先添加任务。</p>
                </section>
          </div>
     </c:when>
    <c:otherwise>
        <c:forEach items="${orderMap}" var="item">
            <div class="goodslist">
                <strong><span>${item.key.skuName}</span></strong>
                <input type="hidden" value="${item.key.id}" name="skuIds">
                <section class="dialogWidget">
                    <ul>
                        <li><b class="dialogNum">${item.value}</b></li>
                        <li><a href="javascript:;" class="weui_btn weui_btn_mini weui_btn_default dialogBtn">调整</a></li>
                    </ul>
                </section>
            </div>
        </c:forEach>
    </c:otherwise>
    </c:choose>

        </hgroup>
    </div>
    <%--<div class="weui_btn_area">
        <a class="weui_btn weui_btn_default2" href="javascript:">历史记录</a>
    </div>--%>
    <!--button fixedbtm-->
    <a class="weui_btn weui_btn_primary fixedbtm" id="pickUpBtn" href="javascript:">确认提货</a>

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        FastClick.attach(document.body);
        sessionStorage.setItem("need-refresh", true);
        $(document).on("click","#pickUpBtn",function(){
             var skuIdsInput = $('.goodslist').find('input[name="skuIds"]');
             var dialogNumInput = $('.goodslist').find('.dialogNum');
            var skuArray = new Array();
            var countArray = new Array();
            skuIdsInput.each(function () {
                skuArray.push($(this).val());
            });
            dialogNumInput.each(function () {
                countArray.push($(this).text());
            });
            if (skuIdsInput.length > 0) {
                $.ajax({
                    type: 'post',
                    data: {"skuIds": skuArray.join(","),"counts":countArray.join(",")},
                    url: '/mp/mpTask/delivery/order/pickup',
                    async: false,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        if(data.type=='error'){
                            $.toast("提货失败", "text");
                        }
                        else{
                            window.history.back();
                        }
                    }
                });
            }
            else{
                $.toast("暂无可提货物", "text");
            }
        })
    });
</script>
<!----------------Common js end------------------>

<!--- WorkOrder 交互js start--->
<script type="text/javascript" src="${path}/mp-static/js/WorkOrder_bill.js"></script>
<!--- WorkOrder 交互js end --->


</body>
</html>
