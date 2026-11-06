<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/21
  Time: 11:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>商品效期监控 | 贩售机管理系统 4.2</title>
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
            <h2><strong id="MainHeaderTitle">商品效期监控</strong></h2>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <section>
                    <strong>搜索字段:</strong>
                    <div class="form-group w120 pull-left">
                        <select class="form-control input-sm">
                            <option value="0">商品名称</option>
                            <option value="1">商品ID</option>
                            <option value="2">售货机编号</option>
                            <option value="3">点位</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>搜索内容:</strong>
                    <div class="form-group w120 pull-left">
                        <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入内容"></div>
                    </div>
                </section>
                <button type="button" class="btn btn-default f12 btn-lg2">查询</button>
                <button type="reset" class="btn btn-default f12 btn-lg2">重置</button>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh10">商品ID</li>
                    <li class="wh15">商品名称</li>
                    <li class="wh5">商品规格</li>
                    <li class="wh5">剩余天数</li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh5">货道编号</li>
                    <li class="wh5">货道余量</li>
                    <li class="wh10">生产日期</li>
                    <li class="wh10">有效期至</li>
                    <li class="wh10">生产批号</li>
                    <li class="wh10">点位</li>
                    <li class="wh5">配送员</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <table>
                <tbody class="auto">
                <tr>
                    <td class="wh10">
                        <span>10251625</span>
                    </td>
                    <td class="wh15">
                        <span>复方氨酚烷胺胶囊</span>
                    </td>
                    <td class="wh5">
                        <span>10粒/盒</span>
                    </td>
                    <td class="wh5">
                        <span>180</span>
                    </td>
                    <td class="wh10">
                        <span>100260001</span>
                    </td>
                    <td class="wh5">
                        <span>1-2</span>
                    </td>
                    <td class="wh5">
                        <span>10</span>
                    </td>
                    <td class="wh10">
                        <span>2016-10-22</span>
                    </td>
                    <td class="wh10">
                        <span>2019-10-22</span>
                    </td>
                    <td class="wh10">
                        <span>161010</span>
                    </td>
                    <td class="wh10">
                        <span>点位</span>
                    </td>
                    <td class="wh5">
                        <span>配送员</span>
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

