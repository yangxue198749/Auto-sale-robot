<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/22
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>销售订单 | 贩售机管理系统 4.2</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
    <script src="./plus/textSum/textSum.min.js" type="text/javascript"></script>

</head>

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
            <jsp:include page="/loadMenu?menuCode=company_home&code=system-manage"/>
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
            <h2><strong id="MainHeaderTitle">销售订单</strong></h2>
            <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form>
                <section>
                    <strong>搜索类型:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="">
                            <option value="1">售货机编号</option>
                            <option value="2">流水号</option>
                            <option value="3">点位名称</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>搜索内容:</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" name="" type="text" placeholder="请输入内容">
                    </div>
                </section>
                <section>
                    <strong>商品名称:</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" name="" type="text" placeholder="不包含商品规格">
                    </div>
                </section>
                <section>
                    <strong>订单状态:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="">
                            <option value="">请选择</option>
                            <option value="1">正常</option>
                            <option value="2">失败</option>
                            <option value="3">未知</option>
                            <option value="4">未取货</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>业务类型:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="">
                            <option value="">请选择</option>
                            <option value="1">即售订单</option>
                            <option value="2">预定订单</option>
                            <option value="3">预售订单</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>支付方式:</strong>
                    <div class="form-group w100 pull-left">
                        <select class="form-control input-sm" name="">
                            <option value="">请选择</option>
                            <option value="1">微信</option>
                            <option value="2">支付宝</option>
                            <option value="3">康得新储值卡</option>
                            <option value="4">银联支付</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>支付日期:</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" value="2017-09-12" id="DateStartOne" placeholder="开始日期" name="beginDate">
                    </div>
                    <strong class="ml5">-</strong>
                    <div class="form-group w100 pull-left">
                        <input class="form-control input-sm" type="text" value="2017-09-12" id="DateEndOne" placeholder="结束日期" name="endDate">
                    </div>
                    <div class="clearfix"></div>
                </section>
                <button type="button" class="btn btn-default f12 btn-lg2">查询</button>
                <button type="reset" class="btn btn-default f12 btn-lg2">重置</button>
                <div class="clearfix"></div>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh5">点位名称</li>
                    <li class="wh5">售货机编号</li>
                    <li class="wh10">订单编号</li>
                    <li class="wh10">流水号</li>
                    <li class="wh10">外部订单号</li>
                    <li class="wh5">业务类型</li>
                    <li class="wh5">商品ID</li>
                    <li class="wh10">商品名称</li>
                    <li class="wh5">支付方式</li>
                    <li class="wh5">商品金额(分)</li>
                    <li class="wh5">支付金额(分)</li>
                    <li class="wh5">卡包支付金额(分)</li>
                    <li class="wh5">下单时间</li>
                    <li class="wh5">取货有效期</li>
                    <li class="wh5">订单状态</li>
                    <li class="wh5">出货状态</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <table>
                <tbody class="auto">
                <tr>
                    <td class="wh5">
                        <span>青青家园</span>
                    </td>
                    <td class="wh5">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh5">
                        <span class="icon_state green">即售</span>
                        <!--<span class="icon_state blue">预定</span>
                        <span class="icon_state red">预售</span>-->
                    </td>
                    <td class="wh5">
                        <span>1263</span>
                    </td>
                    <td class="wh10">
                        <span>三元鲜奶200ml</span>
                    </td>
                    <td class="wh5">
                        <span>支付宝</span>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <span>2017-07-19 18:15:26</span>
                    </td>
                    <td class="wh5">
                        <span>2017-07-19 18:15:26</span>
                    </td>
                    <td class="wh5">
                        <span class="icon_state blue">待配送</span>
                        <!--<span class="icon_state red">已取消</span>
                        <span class="icon_state green">配送中</span>
                        <span class="icon_state yellow">已送达</span>
                        <span class="icon_state red">未送达</span>
                        <span class="icon_state gray">已完成</span>-->
                    </td>
                    <td class="wh5">
                        <span>-</span>
                    </td>
                </tr>
                <tr>
                    <td class="wh5">
                        <span>青青家园</span>
                    </td>
                    <td class="wh5">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh5">
                        <!--<span class="icon_state green">即售</span>-->
                        <span class="icon_state blue">预定</span>
                        <!--<span class="icon_state red">预售</span>-->
                    </td>
                    <td class="wh5">
                        <span>1263</span>
                    </td>
                    <td class="wh10">
                        <span>三元鲜奶200ml</span>
                    </td>
                    <td class="wh5">
                        <span>支付宝</span>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <span>2017-07-19 18:15:26</span>
                    </td>
                    <td class="wh5">
                        <span>2017-07-19 18:15:26</span>
                    </td>
                    <td class="wh5">
                        <span class="icon_state red">已取消</span>
                        <!--
                        <span class="icon_state blue">待配送</span>
                        <span class="icon_state green">配送中</span>
                        <span class="icon_state yellow">已送达</span>
                        <span class="icon_state red">未送达</span>
                        <span class="icon_state gray">已完成</span>-->
                    </td>
                    <td class="wh5">
                        <span>-</span>
                    </td>
                </tr>
                <tr>
                    <td class="wh5">
                        <span>青青家园</span>
                    </td>
                    <td class="wh5">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh5">
                        <span class="icon_state red">预售</span>
                        <!--<span class="icon_state blue">预定</span>
                        <span class="icon_state green">即售</span>-->
                    </td>
                    <td class="wh5">
                        <span>1263</span>
                    </td>
                    <td class="wh10">
                        <span>三元鲜奶200ml</span>
                    </td>
                    <td class="wh5">
                        <span>支付宝</span>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <b class="cf_green">300</b>
                    </td>
                    <td class="wh5">
                        <span>2017-07-19 18:15:26</span>
                    </td>
                    <td class="wh5">
                        <span>2017-07-19 18:15:26</span>
                    </td>
                    <td class="wh5">
                        <span class="icon_state gray">已完成</span>
                        <!--
                        <span class="icon_state blue">待配送</span>
                        <span class="icon_state red">已取消</span>
                        <span class="icon_state green">配送中</span>
                        <span class="icon_state yellow">已送达</span>
                        <span class="icon_state red">未送达</span>-->
                    </td>
                    <td class="wh5">
                        <span>-</span>
                    </td>
                </tr>
                </tbody>

            </table>
            <!--table footer start-->
            <div class="tfootscroll">
                <ul class="pagination pull-right mtb10 mr20">
                    <li><a href="#">&laquo;</a></li>
                    <li class="active"><a href="#">1</a></li>
                    <li class="disabled"><a href="#">2</a></li>
                    <li><a href="#">3</a></li>
                    <li><a href="#">4</a></li>
                    <li><a href="#">5</a></li>
                    <li><a href="#">&raquo;</a></li>
                </ul>
            </div>
            <!--table footer end-->
        </div>
    </div>


</div>
</body>
</html>
