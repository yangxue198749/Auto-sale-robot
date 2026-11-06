<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>账号设置 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/account.js"></script>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
<body>
<!--[if lt IE 9]>
<div class="goodbye_group">
    <div class="gbwrap">
        <div class="gbbox">
            <h2>为了提供更好的服务，请升级您的浏览器</h2>
            <p>尊敬的用户，您现在使用的浏览器版本过低，请升级后使用VMS相关服务。</p>
            <h4>您可以选择以下浏览器：</h4>
            <ul>
                <li><a class="chrome" target="_blank" href="http://www.google.cn/chrome/browser/desktop/index.html">谷歌浏览器(推荐)</a></li>
                <li><a class="flash-360" target="_blank" href="http://se.360.cn/">360安全浏览器</a></li>
            </ul>
        </div>
    </div>
</div>
<![endif]-->
<div class="mp_container">
    <!------ 公用左侧导航 start ------>
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=account-home&code=system-manage"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=system-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!------ 公用头部导航 end ------>
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">账号设置</strong></h2>
        </div>

        <div class="modal_commonbody" style="background: #fff; border-radius: 5px;">
            <div class="text-left" style="width: 360px; margin: 0 auto; padding: 50px 0;">
                <div class="form-group">
                    <div class="input-group wh100">
                        <span class="input-group-addon w75 f12">登录账号</span>
                        <input type="text" class="form-control f12" value="${user.userAccount}" placeholder="请输入登录账号" readonly style="background: #fff;">
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group wh100">
                        <span class="input-group-addon w75 f12">用户姓名</span>
                        <input type="text" class="form-control f12" id="userName" value="${user.userName}" placeholder="请输入用户姓名" readonly style="background: #fff;">
                        <c:if test="${u:isPermitted('account-updateUserNamePage')}">
                        <span class="input-group-addon f12" href="updateUserNamePage" data-toggle="modal" data-target="#changeUserName" style="cursor: pointer">修改</span>
                        </c:if>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group wh100">
                        <span class="input-group-addon w75 f12">手机号码</span>
                        <input type="text" class="form-control f12" id="userPhone" value="${user.userPhone}" placeholder="请输入手机号码" readonly style="background: #fff;">
                        <c:if test="${u:isPermitted('account-updateUserPhonePage')}">
                            <span class="input-group-addon f12" href="updateUserPhonePage" data-toggle="modal" data-target="#verifyChangePhone" style="cursor: pointer">修改</span>
                        </c:if>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group wh100">
                        <span class="input-group-addon w75 f12">账号密码</span>
                        <input type="text" class="form-control f12" value="如需修改，请点击右侧修改按钮" readonly style="background: #fff;">
                        <c:if test="${u:isPermitted('account-updateUserPasswordPage')}">
                            <span class="input-group-addon f12" href="updateUserPasswordPage" data-toggle="modal" data-target="#verifyChangePassword" style="cursor: pointer">修改</span>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----修改用户名 模态框 start----->
    <div class="modal fade" id="changeUserName" tabindex="-1" role="dialog" aria-labelledby="修改用户名" aria-hidden="true"></div>

    <!-----验证身份更改手机 模态框 start----->
    <div class="modal fade" id="verifyChangePhone" tabindex="-1" role="dialog" aria-labelledby="验证身份更改手机号码" aria-hidden="true"></div>

    <!-----验证身份更改密码 模态框 start----->
    <div class="modal fade" id="verifyChangePassword" tabindex="-1" role="dialog" aria-labelledby="验证身份更改密码" aria-hidden="true"></div>


</div>
</body>
</html>
