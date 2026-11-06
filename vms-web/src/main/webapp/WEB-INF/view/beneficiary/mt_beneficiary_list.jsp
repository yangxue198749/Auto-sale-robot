<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>收款参数 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path}/vmsjs/vendout.js?v=20171229"></script>
    <!--[if lt IE 9]>
    <script src="${path}/plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
    <script type="text/javascript" src="${path}/plus/datepicker/js/function.datepicker.js"></script>
    <script type="text/javascript" src="${path}/vmsjs/beneficiary.js"></script>
</head>

<body>
<div class="mp_container">
    <!-- 公用左侧导航 start -->
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=mt-beneficiary-list&code=finance-manage"/>
        </div>
    </div>
    <!------ 公用左侧导航 end ------>
    <!------ 公用头部导航 start ------>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=finance-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <!-- 公用头部导航 end -->
    <!------ 公用主体部分 start ------>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">收款参数</strong></h2>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">

            <div class="commonTable">
                <table>
                    <thead>
                    <tr>
                        <td>支付渠道</td>
                        <td>状态</td>
                        <td>已配置ID</td>
                        <td width="200">操作</td>
                    </tr>
                    </thead>
                    <tbody id="collectionTable">
                    <tr>
                        <td>
                            <span>微信</span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${wxStatus eq '已启用'}">
                                    <span class="icon_state green">${wxStatus}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="icon_state gray">${wxStatus}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><b>${wxAppId}</b></td>
                        <td>
                            <div class="btn-group" data-toggle="buttons">
                                <button type="button" class="btn btn-default btn-sm setCollectionBtn" href="/mtBeneficiary/addWxInfoDialog" data-toggle="modal" data-target="#setCollectionWeChat">配置</button>
                                <input type="hidden" class="AppIdVal" value="${wxAppId}">
                                <c:if test="${!empty wxInfoId}">
                                <button type="button" class="btn btn-default btn-sm deleteCollection btn_delete_pay" href="/mtBeneficiary/deletePayInfo" payInfoId="${wxInfoId}" data-toggle="modal" data-target="#deleteCollection">删除</button>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <span>支付宝</span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${aliStatus eq '已启用'}">
                                    <span class="icon_state green">${aliStatus}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="icon_state gray">${aliStatus}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><b>${aliAppId}</b></td>
                        <td>
                            <div class="btn-group" data-toggle="buttons">
                                <button type="button" class="btn btn-default btn-sm setCollectionBtn" href="/mtBeneficiary/addAliInfoDialog" data-toggle="modal" data-target="#setCollectionAlipay">配置</button>
                                <input type="hidden" class="AppIdVal" value="${aliAppId}">
                                <c:if test="${!empty aliInfoId}">
                                <button type="button" class="btn btn-default btn-sm deleteCollection btn_delete_pay" href="/mtBeneficiary/deletePayInfo" payInfoId="${aliInfoId}" data-toggle="modal" data-target="#deleteCollection">删除</button>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <script>
                    $(function () {
                        $('#collectionTable .setCollectionBtn').click(function () {
                            var AppIdVal = $(this).siblings('.AppIdVal').val();
                            if(AppIdVal != ''){
                                layer.msg("已配置不允许修改",{shade:[0.6,'#fff'],time:2000});
                                return false;
                            }
                        })
                    })
                </script>
            </div>

        </div>
    </div>
    <!------------- 收款参数-支付宝 setCollectionAlipay ------------->
    <div class="modal fade" id="setCollectionAlipay" tabindex="-1" role="dialog" aria-labelledby="支付宝收款参数" aria-hidden="true">
    </div>
    <!------------- / 收款参数-支付宝 setCollectionAlipay ------------->

    <!------------- 收款参数-微信 setCollectionWeChat ------------->
    <div class="modal fade" id="setCollectionWeChat" tabindex="-1" role="dialog" aria-labelledby="微信收款参数" aria-hidden="true">
    </div>
    <!------------- / 收款参数-微信 addOpenInterface ------------->




</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
        Beneficiary.deletePayInfo();
    })
</script>