<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<a href="${path}/${url}">
    <div class="block_box">
        <div class="tophead">今日配送工单</div>
        <section class="col-md-4 plr5">
            <strong class="cf_blue">${counts[1]}</strong>
            <p>待完成</p>
        </section>
        <section class="col-md-4 plr5">
            <strong class="cf_green">${counts[4]}</strong>
            <p>已完成</p>
        </section>
        <section class="col-md-4 plr5">
            <strong class="cf_yellow">${counts[7]}</strong>
            <p>已忽略</p>
        </section>
        <%--<footer><p><i class="iconfont icon-lvyoutehui cf_gray f24"></i></p><p><span class="cf_gray">更多详情</span></p></footer>--%>
    </div>
</a>