<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/22
  Time: 16:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>销售流水 | 贩售机管理系统 4.2</title>
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
            <h2><strong id="MainHeaderTitle">销售流水</strong></h2>
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
                    <strong>出货状态:</strong>
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
                    <strong>订单日期:</strong>
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
                    <li class="wh10">点位名称</li>
                    <li class="wh5">售货机编号</li>
                    <li class="wh10">流水号</li>
                    <li class="wh5">货道编号</li>
                    <li class="wh5">商品名称</li>
                    <li class="wh5">商品ID</li>
                    <li class="wh5">支付方式</li>
                    <li class="wh5">商品金额(分)</li>
                    <li class="wh5">支付金额(分)</li>
                    <li class="wh5">卡包支付金额(分)</li>
                    <li class="wh5">扣款时间</li>
                    <li class="wh5">出货时间</li>
                    <li class="wh5">出货状态</li>
                    <li class="wh5">失败原因</li>
                    <li class="wh5">退款状态</li>
                    <li class="wh10">备注</li>
                    <li class="wh5">操作</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <table>
                <tbody class="auto">
                <tr>
                    <td class="wh10">
                        <span>青青家园</span>
                    </td>
                    <td class="wh5">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh5">
                        <span>1-1</span>
                    </td>
                    <td class="wh5">
                        <span>三元鲜奶200ml</span>
                    </td>
                    <td class="wh5">
                        <span>1263</span>
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
                        <span class="icon_state green">成功</span>
                        <!--<span class="icon_state red">失败</span>-->
                    </td>
                    <td class="wh5">
                        <span>-</span>
                    </td>
                    <td class="wh5">
                        <!--<button type="button" class="btn btn-danger f12 btn-sm refundBtn">退款</button>-->
                        <span>退款中</span>
                    </td>
                    <td class="wh10">
                        <span title="已退款无需再次操作">已退款无需再次操作</span>
                    </td>
                    <td class="wh5">
                        <button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#addRemarkModel">备注</button>
                    </td>
                </tr>
                <tr>
                    <td class="wh10">
                        <span>青青家园</span>
                    </td>
                    <td class="wh5">
                        <span>01000001</span>
                    </td>
                    <td class="wh10">
                        <span>01000001</span>
                    </td>
                    <td class="wh5">
                        <span>1-1</span>
                    </td>
                    <td class="wh5">
                        <span>三元鲜奶200ml</span>
                    </td>
                    <td class="wh5">
                        <span>1263</span>
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
                        <span class="icon_state red">失败</span>
                        <!--<span class="icon_state green">成功</span>-->
                    </td>
                    <td class="wh5">
                        <span>-</span>
                    </td>
                    <td class="wh5">
                        <button type="button" class="btn btn-danger f12 btn-sm refundBtn">退款</button>
                    </td>
                    <td class="wh10">
                        <span title="已退款无需再次操作">已退款无需再次操作</span>
                    </td>
                    <td class="wh5">
                        <button type="button" class="btn btn-default f12 btn-sm" data-toggle="modal" data-target="#addRemarkModel">备注</button>
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
    <script>
        $(function(){
            //退款操作 询问
            $(document).on('click','.refundBtn',function(){
                var thisBtn = $(this);
                layer.confirm('该笔消费是否确认需要退款？', {
                    title: '提示',
                    btn: ['确定','取消']
                }, function(){
                    //确认操作
                    layer.msg('操作成功！',{shade:[0.6,'#fff'],time:1000});
                    // todo 确认退款后的其他相关操作

                }, function(){
                    //取消操作
                });
            });
        })
    </script>


    <!-------------添加备注 addRemarkModel start------------->
    <div class="modal fade" id="addRemarkModel" tabindex="-1" role="dialog" aria-labelledby="备注" aria-hidden="true">
        <div class="modal-dialog w400">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">备注</h4>
                </div>
                <form method="post" action="" name="form" id="" class="unKeyCode">
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <div class="clearfix">
                                <div class="col-md-12">
                                    <div class="form-group textSum">
                                        <textarea class="form-control input-sm" name="" placeholder="请输入备注描述" required maxlength="100"></textarea>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>

            </div>
        </div>
    </div>
    <!-------------添加备注 addRemarkModel end------------->


</div>
</body>
</html>
