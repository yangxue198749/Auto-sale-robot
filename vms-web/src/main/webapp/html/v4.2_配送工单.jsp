<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/13
  Time: 10:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>配送工单 | 贩售机管理系统 4.2</title>
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
            <h2><strong id="MainHeaderTitle">配送工单</strong></h2>
            <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
            <div class="btn-group pull-right mr10" data-toggle="buttons">
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addWorkOrder">新增</button>
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#changeWorkOrder">修改</button>
                <button type="button" class="btn btn-default btn-lg2 btn_tableCancel">取消</button>
            </div>
            <script>
                $(function(){
                    //取消任务 demo
                    $(".btn_tableCancel").click(function(){
                        var chkBox = $(".workorder_list input[type='checkbox']:checked");
                        if(chkBox.length == 0){
                            layer.msg("请先选中需要取消的任务",{shade: [0.5, '#fff'],time:2000,});
                            return false;
                        }else{
                            //处理table单选
                            $(".workorder_list td").on("click","input[type='checkbox']",function(){
                                if($(this).prop("checked")){
                                    $(this).parents().siblings().find("input[type='checkbox']").prop("checked",false);
                                }
                            });
                            layer.confirm('是否确定取消该任务？', {
                                btn: ['确定','取消'],
                                title: '提示',
                            }, function(){ //成功后执行
                                //这里ajax处理服务端取消

                                chkBox.parents().find('.workorder_state').empty().append('<span class="icon_state gray">已取消</span>')
                                layer.msg('任务已取消',{shade: [0.5, '#fff'],time:2000,});
                            }, function(index){
                                layer.close(index);//关闭窗口
                            });
                        }
                    });
                })
            </script>

        </div> <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form>
                <section>
                    <strong>搜索字段:</strong>
                    <div class="form-group w120 pull-left">
                        <select class="form-control input-sm">
                            <option>请选择</option>
                            <option>售货机编号</option>
                            <option>点位名称</option>
                            <option>配送员</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>搜索内容:</strong>
                    <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入关键字"></div>
                </section>
                <section>
                    <strong>任务状态:</strong>
                    <div class="form-group w120 pull-left">
                        <select class="form-control input-sm">
                            <option>请选择</option>
                            <option>未开始</option>
                            <option>进行中</option>
                            <option>已完成</option>
                            <option>已忽略</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>时间范围:</strong>
                    <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" id="DateStart" placeholder="开始日期"></div>
                    <strong class="ml5">-</strong>
                    <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" id="DateEnd" placeholder="结束日期"></div>
                </section>
                <button type="button" class="btn btn-default btn-sm2">查询</button>
                <button type="reset" class="btn btn-default btn-sm2">重置</button>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh10">售货机编号</li>
                    <li class="wh10">点位</li>
                    <li class="wh10">线路</li>
                    <li class="wh10">任务状态</li>
                    <li class="wh7">补货模式</li>
                    <li class="wh7">配送员</li>
                    <li class="wh12">执行日期</li>
                    <li class="wh12">完成时间</li>
                    <li class="wh12">创建记录</li>
                    <li class="wh7">详情</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <table>
                <tbody class="auto workorder_list">
                <tr>
                    <td class="wh3"><input type="checkbox" value=""></td>
                    <td class="wh10">
                        <span>100966512</span>
                    </td>
                    <td class="wh10">
                        <span>万达广场</span>
                    </td>
                    <td class="wh10">
                        <span>北区一线</span>
                    </td>
                    <td class="wh10 workorder_state">
                        <span class="icon_state blue">未开始</span>
                        <!-- 其它状态 blue green gray yellow - class不同
                        <span class="icon_state green">进行中</span>
                        <span class="icon_state gray">已完成</span>
                        <span class="icon_state yellow">已忽略</span>
                        -->
                    </td>
                    <td class="wh7">
                        <span>补满</span>
                    </td>
                    <td class="wh7">
                        <span>巴顿</span>
                    </td>
                    <td class="wh12">
                        <p><span>2016/11/14</span></p>
                    </td>
                    <td class="wh12">
                        <span>2016/11/15 15:20:06</span>
                    </td>
                    <td class="wh12">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
                    </td>
                    <td class="wh7">
                        <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#viewDialog">查看</button>
                    </td>
                </tr>
                <tr>
                    <td class="wh3"><input type="checkbox" value=""></td>
                    <td class="wh10">
                        <span>100966512</span>
                    </td>
                    <td class="wh10">
                        <span>万达广场</span>
                    </td>
                    <td class="wh10">
                        <span>北区一线</span>
                    </td>
                    <td class="wh10 workorder_state">
                        <span class="icon_state green">进行中</span>
                    </td>
                    <td class="wh7">
                        <span>补满</span>
                    </td>
                    <td class="wh7">
                        <span>巴顿</span>
                    </td>
                    <td class="wh12">
                        <p><span>2016/11/14</span></p>
                    </td>
                    <td class="wh12">
                        <span>2016/11/15 15:20:06</span>
                    </td>
                    <td class="wh12">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
                    </td>
                    <td class="wh7">
                        <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#viewDialog">查看</button>
                    </td>
                </tr>
                <tr>
                    <td class="wh3"><input type="checkbox" value=""></td>
                    <td class="wh10">
                        <span>100966512</span>
                    </td>
                    <td class="wh10">
                        <span>万达广场</span>
                    </td>
                    <td class="wh10">
                        <span>北区一线</span>
                    </td>
                    <td class="wh10 workorder_state">
                        <span class="icon_state gray">已完成</span>
                    </td>
                    <td class="wh7">
                        <span>补满</span>
                    </td>
                    <td class="wh7">
                        <span>巴顿</span>
                    </td>
                    <td class="wh12">
                        <p><span>2016/11/14</span></p>
                    </td>
                    <td class="wh12">
                        <span>2016/11/15 15:20:06</span>
                    </td>
                    <td class="wh12">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
                    </td>
                    <td class="wh7">
                        <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#viewDialog">查看</button>
                    </td>
                </tr>
                <tr>
                    <td class="wh3"><input type="checkbox" value=""></td>
                    <td class="wh10">
                        <span>100966512</span>
                    </td>
                    <td class="wh10">
                        <span>万达广场</span>
                    </td>
                    <td class="wh10">
                        <span>北区一线</span>
                    </td>
                    <td class="wh10 workorder_state">
                        <span class="icon_state yellow">已忽略</span>
                    </td>
                    <td class="wh7">
                        <span>补满</span>
                    </td>
                    <td class="wh7">
                        <span>巴顿</span>
                    </td>
                    <td class="wh12">
                        <p><span>2016/11/14</span></p>
                    </td>
                    <td class="wh12">
                        <span>2016/11/15 15:20:06</span>
                    </td>
                    <td class="wh12">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
                    </td>
                    <td class="wh7">
                        <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#viewDialog">查看</button>
                    </td>
                </tr>
                </tbody>

            </table>
            <!--table footer start-->
            <div class="tfootscroll">
                <ul class="pagination pull-right mr20">
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

    <!---窗口 模态框--->
    <!-------------新增配送工单 addWorkOrder start------------->
    <div class="modal fade" id="addWorkOrder" tabindex="-1" role="dialog" aria-labelledby="新增任务" aria-hidden="true">
        <div class="modal-dialog w400">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">新增任务</h4>
                </div>
                <form method="post" action="" name="form" id="" class="unKeyCode">
                    <div class="modal-body">
                        <!--基本信息 start-->
                        <div class="modal_commonbody clearfix">
                            <!--填写基本信息-->
                            <div class="clearfix">
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">售货机:</span>
                                    <div class="pull-left">
                                        <button type="button" class="w220 form-control input-sm" data-toggle="modal" data-target="#choiceAutoMat">选择售货机</button>
                                        <p><span class="mr5">10000010026</span></p>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">配送员:</span>
                                    <div class="pull-left">
                                        <button type="button" class="w220 form-control input-sm" data-toggle="modal" data-target="#selectCourier">选择配送员</button>
                                    </div>
                                </div>
                                <div class="clearfix"></div>
                                <div class="otherInfoTips"><span class="tipsBtn" id="otherInfoBtn">更多选项</span></div>
                                <div class="otherInfoWrap" id="otherInfoWrap">
                                    <div class="col-md-12">
                                        <span class="pull-left w60 text-right mr20">补货方式:</span>
                                        <div class="form-group pull-left" id="handBtnGroup">
                                            <label class="w70 mr10"><input class="input_v4 mr5" type="radio" name="goodsRadio" value="1"><span>补满</span></label>
                                            <label class="w100"><input class="input_v4 mr5" type="radio" name="goodsRadio" value="2"><span>手动</span></label>
                                            <button type="button" class="w220 form-control input-sm handBtn" style="display: none" data-toggle="modal" data-target="#handleDialog">手动调量</button>
                                        </div>
                                    </div>
                                    <div class="clearfix"></div>
                                </div>
                                <script>
                                    $(function(){
                                        $('#otherInfoBtn').click(function(){
                                            $('#otherInfoWrap').toggle();
                                        });
                                        $('#handBtnGroup label').click(function(){
                                            var inputVal = $(this).find('input[type=radio]:checked').val();
                                            if(inputVal == '2'){
                                                $(this).siblings('.handBtn').show();
                                            }else{
                                                $(this).siblings('.handBtn').hide();
                                            }
                                        })
                                    })
                                </script>
                            </div>
                        </div>
                    </div>
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success btn-lg2">发布</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>

            </div>
        </div>
    </div>
    <!-------------新增配送工单 addWorkOrder end------------->

    <!-------------修改配送工单 changeWorkOrder start------------->
    <div class="modal fade" id="changeWorkOrder" tabindex="-1" role="dialog" aria-labelledby="修改" aria-hidden="true">
        <div class="modal-dialog w400">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">修改</h4>
                </div>
                <form method="post" action="" name="form" id="" class="unKeyCode">
                    <div class="modal-body">
                        <!--基本信息 start-->
                        <div class="modal_commonbody clearfix">
                            <!--填写基本信息-->
                            <div class="clearfix">
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">售货机:</span>
                                    <div class="form-group pull-left">
                                        <p><span class="mr5">10000010026</span><span class="mr5">10000010026</span></p>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">配送员:</span>
                                    <div class="form-group pull-left">
                                        <button type="button" class="w220 form-control input-sm" data-toggle="modal" data-target="#selectCourier">选择配送员</button>
                                        <p><span class="mr5">巴顿</span></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success btn-lg2">发布</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>

            </div>
        </div>
    </div>
    <!-------------修改配送工单 changeWorkOrder end------------->

    <!-------------选择售货机 choiceAutoMat start------------>
    <div class="modal fade" id="choiceAutoMat" tabindex="-1" role="dialog" aria-labelledby="选择售货机" aria-hidden="true">
        <div class="modal-dialog w600">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">选择售货机</h4>
                </div>
                <div class="modal-body">
                    <div class="main_commonhead hl30">
                        <form method="post" action="" name="form" id="" class="unKeyCode">
                            <section>
                                <strong>售货机编号:</strong>
                                <div class="form-group w180 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入售货机编号"></div>
                            </section>
                            <button type="submit" class="btn btn-default f12 btn-lg2 pull-left mr5">查询</button>
                            <button type="reset" class="btn btn-default f12 btn-lg2 pull-left">重置</button>
                        </form>
                    </div>
                    <div class="clearfix"></div>
                    <div class="main_commontable max300 mt15 border_line">
                        <table>
                            <thead>
                            <tr>
                                <td width="30"></td>
                                <td>售货机编号</td>
                                <td>点位</td>
                            </tr>
                            </thead>
                            <tbody class="auto">
                            <tr>
                                <td><input type="checkbox" value=""></td>
                                <td>231150005</td>
                                <td>大望路126号</td>
                            </tr>
                            <tr>
                                <td><input type="checkbox" value=""></td>
                                <td>231150005</td>
                                <td>大望路126号</td>
                            </tr>
                            <tr>
                                <td><input type="checkbox" value=""></td>
                                <td>231150005</td>
                                <td>大望路126号</td>
                            </tr>
                            <tr>
                                <td><input type="checkbox" value=""></td>
                                <td>231150005</td>
                                <td>大望路126号</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>

                </div>
            </div>
        </div>
    </div>
    <!-------------选择售货机 choiceAutoMat end--------------->

    <!--选择配送员 -->
    <div class="modal fade" id="selectCourier" tabindex="-1" role="dialog" aria-labelledby="选择配送员" aria-hidden="true">
        <div class="modal-dialog w600">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">选择配送员</h4>
                </div>
                <div class="modal-body">
                    <div class="main_commonhead hl30">
                        <form method="post" action="" name="form" id="" class="unKeyCode">
                            <section>
                                <strong>用户姓名:</strong>
                                <div class="form-group w180 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入用户姓名"></div>
                            </section>
                            <button type="submit" class="btn btn-default f12 btn-lg2 pull-left mr5">查询</button>
                            <button type="reset" class="btn btn-default f12 btn-lg2 pull-left">重置</button>
                        </form>
                        <div class="clearfix"></div>
                    </div>
                    <div class="modal_commonbody clearfix">
                        <div class="main_commontable max300">
                            <table>
                                <thead>
                                <tr>
                                    <th width="30" class="text-center"></th>
                                    <th class="text-center"><span>用户姓名</span></th>
                                    <th class="text-center"><span>登录账号</span></th>
                                </tr>
                                </thead>
                                <tbody class="auto">
                                <tr>
                                    <td><input type="checkbox" value=""></td>
                                    <td>
                                        <span>搬砖的</span>
                                    </td>
                                    <td>
                                        <span>BanZhuanDe</span>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success">确定</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>

    <!-----取消任务 模态框 start----->
    <div class="modal fade" id="deleteOrder" tabindex="-1" role="dialog" aria-labelledby="取消" aria-hidden="true">
        <div class="modal-dialog w400">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16">取消</h4>
                </div>
                <div class="modal-body">
                    <div class="modal_commonbody clearfix">
                        <div class="step_info text-center">
                            <p>是否确定取消该任务？</p>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2">确定</button>
                    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>

    <!--手动调量-->
    <div class="modal fade" id="handleDialog" tabindex="-1" role="dialog" aria-labelledby="手动调量" aria-hidden="true">
        <div class="modal-dialog w800">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16">手动调量</h4>
                </div>
                <div class="modal-body">
                    <div class="main_commontable max400">
                        <table>
                            <thead>
                            <tr>
                                <td>图片</td>
                                <td>商品</td>
                                <td>货道</td>
                                <td>满量</td>
                                <td>余量</td>
                                <td>清</td>
                                <td>补</td>
                            </tr>
                            </thead>
                            <tbody class="auto">
                            <tr>
                                <td>
                                    <img src="http://cdn-s1.touchfound.net/1508379183829_5375.png" style="width:40px; height:auto">
                                </td>
                                <td>统一阿萨姆原味奶茶500ml</td>
                                <td>1-1</td>
                                <td>2</td>
                                <td>2</td>
                                <td><input class="form-control w60 text-center auto input-sm" type="text" value="0"></td>
                                <td><input class="form-control w60 text-center auto input-sm" type="text" value="3"></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <!--查看详情 table end-->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2">确定</button>
                </div>
            </div>
        </div>
    </div>

    <!--查看详情-->
    <div class="modal fade" id="viewDialog" tabindex="-1" role="dialog" aria-labelledby="查看" aria-hidden="true">
        <div class="modal-dialog w800">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16">查看</h4>
                </div>
                <div class="modal-body">
                    <div class="main_commontable max400">
                        <table>
                            <thead>
                            <tr>
                                <td>图片</td>
                                <td>商品</td>
                                <td>货道</td>
                                <td>补前</td>
                                <td>清</td>
                                <td>应补</td>
                                <td>实补</td>
                                <td>补后</td>
                            </tr>
                            </thead>
                            <tbody class="auto">
                            <tr>
                                <td>
                                    <img src="http://cdn-s1.touchfound.net/1508379183829_5375.png" style="width:40px; height:auto">
                                </td>
                                <td>统一阿萨姆原味奶茶500ml</td>
                                <td>1-1</td>
                                <td>2</td>
                                <td>2</td>
                                <td>2</td>
                                <td>2</td>
                                <td>5</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <!--查看详情 table end-->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2">确定</button>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
</html>
