<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<a href="${path}/${url}">
    <div class="block_box">
        <div class="tophead">今日促销中设备</div>
        <section class="col-md-12">
            <strong class="cf_green">${count}</strong>
            <p>促销中设备数</p>
        </section>
        <%--<footer><p><i class="iconfont icon-lvyoutehui cf_gray f24"></i></p><p><span class="cf_gray">更多详情</span></p></footer>--%>
    </div>
</a>