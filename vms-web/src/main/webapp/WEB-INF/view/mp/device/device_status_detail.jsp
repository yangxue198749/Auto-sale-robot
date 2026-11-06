<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>设备状态详情</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="设备状态详情">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="weui-pull-to-refresh-layer">
    <div class="pull-to-refresh-arrow"></div> <!-- 上下拉动的时候显示的箭头 -->
    <div class="pull-to-refresh-preloader"></div> <!-- 正在刷新的图标 -->
    <div class="down">下拉刷新</div><!-- 下拉过程显示的文案 -->
    <div class="up">释放刷新</div><!-- 下拉超过50px显示的文案 -->
    <div class="refresh">正在刷新...</div><!-- 正在刷新时显示的文案 -->
</div>
<div class="mh_container">

    <!--设备状态详情detailBox-->
    <div class="detailBox">
        <aside>
            <p>${mtVm.node.nodeName}</p>
            <strong>${mtVm.innerCode}</strong>
        </aside>
        <article>
            <section>
                <p><strong><span>${mtVm.vmDesc}</span></strong></p>
                <p class="moretext">${mtVm.node.nodeAddress}</p>
            </section>
        </article>
    </div>

    <!--设备-->
    <div class="detailWrap">
        <header><span>设备</span></header>
        <hgroup>
            <section>
                <c:choose>
                    <c:when test="${mtVm.vmcStatus eq '0'}">
                        <p>设备状态：<span>正常</span></p>
                    </c:when>
                    <c:when test="${mtVm.vmcStatus eq '1'}">
                        <p>设备状态：<span class="cf_red">故障</span></p>
                    </c:when>
                    <c:when test="${mtVm.vmcStatus eq '2'}">
                        <p>设备状态：<span class="cf_red">忙碌</span></p>
                    </c:when>
                    <c:otherwise>
                        <p>设备状态：<span class="cf_red">未知</span></p>
                    </c:otherwise>
                </c:choose>
                <p>设备问题：<span>${faultInfo}</span></p>
                <p>故障发生时间：<span>${dateTime}</span></p>
                <p>故障时长：<span>${passTime}</span></p>
                <c:if test="${isReserve}">
                    <p>开启回瓶柜：<span><a href="javascript:;"
                                      style="display: inline-block; border-radius: 4px; padding: 0 14px; background: #1fc38b; color: #fff; line-height: 24px; font-size: 12px;"
                                      id="openMat">开柜</a></span></p>
                </c:if>
            </section>
        </hgroup>
    </div>

    <!--网络-->
    <div class="detailWrap">
        <header><span>网络</span></header>
        <hgroup>
            <section>
                <c:choose>
                    <c:when test="${mtVm.networkStatus eq '0'}">
                        <p>网络状态：<span>正常</span></p>
                    </c:when>
                    <c:when test="${mtVm.networkStatus eq '1'}">
                        <p>网络状态：<span class="cf_red">故障</span></p>
                    </c:when>
                    <c:otherwise>
                        <p>网络状态：<span class="cf_red">未知</span></p>
                    </c:otherwise>
                </c:choose>
                <p>故障发生时间：<span>${netDateTime}</span></p>
                <p>故障时长：<span>${netPassTime}</span></p>
            </section>
        </hgroup>
    </div>

    <!--温度-->
    <div class="detailWrap">
        <header><span>温度</span></header>
        <hgroup>
            <section>
                <c:choose>
                    <c:when test="${mtVm.tempStatus eq '1'}">
                        <p>温度状态：<span>正常</span></p>
                    </c:when>
                    <c:when test="${mtVm.tempStatus eq '2'}">
                        <p>温度状态：<span class="cf_red">异常</span></p>
                    </c:when>
                    <c:when test="${mtVm.tempStatus eq '3'}">
                        <p>温度状态：<span class="cf_blue">未知</span></p>
                    </c:when>
                    <c:otherwise>
                        <p>温度状态：<span>正常</span></p>
                    </c:otherwise>
                </c:choose>
                <p>实际温度：<span>${mtVm.nowTempByMp}</span></p>
                <p>设置温度：<span>${mtVm.targetTemp}</span></p>
            </section>
        </hgroup>
    </div>

    <div class="detailWrap">
        <header>
            <span>商品名称</span>
            <section>
                <ul>
                    <li>总量</li>
                    <li>余量</li>
                </ul>
            </section>
        </header>
        <hgroup class="p0">
            <c:forEach items="${stockList}" var="stock">
                <label>
                    <strong><span>${stock.name}</span></strong>
                    <section>
                        <ul>
                            <li><b>${stock.maxCount}</b></li>
                            <li><b>${stock.currCount}</b></li>
                        </ul>
                    </section>
                </label>
            </c:forEach>
        </hgroup>
    </div>
    <!--设备状态详情 end-->

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function () {
        FastClick.attach(document.body);
        $(document.body).pullToRefresh().on("pull-to-refresh", function () {
            setTimeout(function () {
                window.location.reload();
                $(document.body).pullToRefreshDone(); // 重置下拉刷新
            }, 500);   //模拟延迟
        });
        //openMat
        $('#openMat').click(function () {
            $.confirm("是否确认开启全部柜门", function () {
                $.ajax({
                    type: 'post',
                    url: '/mp/vmStatus/open',
                    data:{"vmId":${mtVm.id}},
                    async: false,
                    dataType: 'json',
                    cache: false,
                    success: function (data) {
                        $.toast(data.message, "text");
                    }
                });
            }, function () {
                // 点击取消后的回调函数
                // $.toast("暂无操作", "text");
            });
        })
    });
</script>
<!----------------Common js end------------------>


</body>
</html>
