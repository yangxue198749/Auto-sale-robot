<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/13
  Time: 13:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>售货机库存 | 贩售机管理系统 4.2</title>
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
            <h2><strong id="MainHeaderTitle">售货机库存</strong></h2>
        </div>
        <div class="col-md-12">
            <!--Main CommonHead-->
            <div class="main_commonhead hl30">
                <form method="post" action="" name="form" id="" class="unKeyCode">
                    <section>
                        <strong>搜索字段:</strong>
                        <div class="form-group w120 pull-left">
                            <select class="form-control input-sm">
                                <option value="0">请选择</option>
                                <option value="1">售货机编号</option>
                                <option value="2">点位</option>
                            </select>
                        </div>
                    </section>
                    <section>
                        <strong>搜索内容:</strong>
                        <div class="form-group w120 pull-left">
                            <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入搜索内容"></div>
                        </div>
                    </section>
                    <button type="button" class="btn btn-default f12 btn-lg2">查询</button>
                    <button type="reset" class="btn btn-default f12 btn-lg2">重置</button>
                </form>
            </div>

            <!--Main CommonTable-->
            <div class="main_commontable">
                <div class="wh60 pull-left" style="padding-right: 10px;">
                    <table class="bgf border_line">
                        <thead>
                        <tr>
                            <td width="30"><input type="checkbox" value=""></td>
                            <td><span>售货机编号</span></td>
                            <td>
                                <span>点位名称</span>
                            </td>
                            <td>
                                <span>机存余量</span>
                            </td>
                            <td>
                                <span>缺货商品</span>
                            </td>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>01000001</span></td>
                            <td>
                                <span>北京市建国路93号万达广场</span>
                            </td>
                            <td>
                                <span>60%</span>
                            </td>
                            <td>
                                <span>可口可乐300ml，雪碧300ml</span>
                            </td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>01000001</span></td>
                            <td>
                                <span>北京市建国路93号万达广场</span>
                            </td>
                            <td>
                                <span>60%</span>
                            </td>
                            <td>
                                <span>可口可乐300ml，雪碧300ml</span>
                            </td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>01000001</span></td>
                            <td>
                                <span>北京市建国路93号万达广场</span>
                            </td>
                            <td>
                                <span>60%</span>
                            </td>
                            <td>
                                <span>可口可乐300ml，雪碧300ml</span>
                            </td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>01000001</span></td>
                            <td>
                                <span>北京市建国路93号万达广场</span>
                            </td>
                            <td>
                                <span>60%</span>
                            </td>
                            <td>
                                <span>可口可乐300ml，雪碧300ml</span>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="wh40 pull-left">
                    <table class="bgf">
                        <thead>
                        <tr>
                            <td width="30"><input type="checkbox" value=""></td>
                            <td><span>商品</span></td>
                            <td>
                                <span>货道</span>
                            </td>
                            <td>
                                <span>满量</span>
                            </td>
                            <td>
                                <span>余量</span>
                            </td>
                            <td>
                                <span>剩余天数</span>
                            </td>
                            <td>
                                <span>是否有货</span>
                            </td>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>可口可乐300ml</span></td>
                            <td>
                                <span>1-1</span>
                            </td>
                            <td>
                                <span>5</span>
                            </td>
                            <td>
                                <span>3</span>
                            </td>
                            <td>
                                <span>6</span>
                            </td>
                            <td>
                                <span class="icon_state green">有货</span>
                                <!-- 其它状态 gray green - class不同
                                <span class="icon_state gray">无货</span>
                                -->
                            </td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>可口可乐300ml</span></td>
                            <td>
                                <span>1-1</span>
                            </td>
                            <td>
                                <span>5</span>
                            </td>
                            <td>
                                <span>3</span>
                            </td>
                            <td>
                                <span>6</span>
                            </td>
                            <td>
                                <span class="icon_state gray">无货</span>
                            </td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>可口可乐300ml</span></td>
                            <td>
                                <span>1-1</span>
                            </td>
                            <td>
                                <span>5</span>
                            </td>
                            <td>
                                <span>3</span>
                            </td>
                            <td>
                                <span>6</span>
                            </td>
                            <td>
                                <span class="icon_state green">有货</span>
                            </td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" value=""></td>
                            <td><span>可口可乐300ml</span></td>
                            <td>
                                <span>1-1</span>
                            </td>
                            <td>
                                <span>5</span>
                            </td>
                            <td>
                                <span>3</span>
                            </td>
                            <td>
                                <span>6</span>
                            </td>
                            <td>
                                <span class="icon_state gray">无货</span>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </div>


</div>
</body>
</html>
