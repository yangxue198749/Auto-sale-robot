<%@ page contentType="text/html;charset=UTF-8" %><meta http-equiv="Content-Type" content="text/html;charset=utf-8" />

<link rel="stylesheet" href="${path}/mp-static/plus/lib/weui.min.css">
<link rel="stylesheet" href="${path}/mp-static/plus/css/jquery-weui.css">
<script type="text/javascript" src="${path}/plus/jQuery/jquery-2.2.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="${path}/mp-static/css/mohe.mp-v1.01.css?v=20180326" />
<link rel="stylesheet" type="text/css" href="${path}/mp-static/css/mohe.addNew.css?v=20180522" />
<script type="text/javascript">var path = '${path}';</script>
<script>
    $(function(){
        //解决ios微信页面回退不刷新
        var isPageHide = false;
        window.addEventListener('pageshow', function(){
            if(isPageHide) window.location.reload();
        });
        window.addEventListener('pagehide', function(){
            isPageHide = true;
        });
    });
</script>