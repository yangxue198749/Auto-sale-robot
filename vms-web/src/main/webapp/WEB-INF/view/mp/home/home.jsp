<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
    <meta http-equiv="Content-Type">
    <meta content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <title>个人主页</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="个人主页">
    <meta name="HandheldFriendly" content="True">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="msapplication-tap-highlight" content="no">
    <%@ include file="/WEB-INF/view/include/mp-head.jsp" %>
</head>
<body>
<div class="mh_container">

    <div class="myhome">
        <section class="avatar_wrap">
            <img src="${headImg}">
        </section>
        <div class="weui_cells weui_cells_access">
            <a class="weui_cell" href="javascript:;">
                <div class="weui_cell_hd">
                    <p class="f12 cf9">关联账号</p>
                </div>
                <div class="weui_cell_bd weui_cell_primary">
                    <p class="f14 cf3 text_right">${account}</p>
                </div>
            </a>
        </div>
        <div class="relievebtn"><a class="weui_btn weui_btn_warn" id="relieveBtn" href="javascript:">解除绑定</a></div>
    </div>

</div>


<!--watermark-->
<div class="watermark"></div>
<!----------------Common js start---------------->
<%@ include file="/WEB-INF/view/include/mp-foot.jsp" %>
<script>
    $(function() {
        FastClick.attach(document.body);
        //解除绑定
        $('#relieveBtn').click(function(){
            $.confirm("解除绑定", function() {
                $.ajax({
                    type : 'post',
                    url :'/mp/home/unbundle',
                    async : false,
                    dataType : 'json',
                    cache : false,
                    success : function(data) {
                        if(data.type=='error'){
                            $.toast("解绑失败", "text");
                        }
                        else{
                            window.location.href = data.url;
                        }
                    }
                });
            }, function() {
                // 点击取消后的回调函数
                // $.toast("暂无操作", "text");
            });
        })
    });
</script>
<!----------------Common js end------------------>

</body>
</html>
