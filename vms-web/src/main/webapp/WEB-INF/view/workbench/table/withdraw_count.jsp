<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<a href="${path}/${url}">
    <div class="block_box">
        <div class="tophead">今日撤机工单</div>
        <section class="col-md-6">
            <strong class="cf_blue">${counts[1]}</strong>
            <p>待完成</p>
        </section>
        <section class="col-md-6">
            <strong class="cf_green">${counts[4]}</strong>
            <p>已完成</p>
        </section>
        <%--<footer><p><i class="iconfont icon-lvyoutehui cf_gray f24"></i></p><p><span class="cf_gray">更多详情</span></p></footer>--%>
    </div>
</a>