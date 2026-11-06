<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/2
  Time: 11:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>点位管理 | 贩售机管理系统 5.0</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
    <link href="./plus/jquery-ztree/3.5.12/css/zTreeStyle/zTreeStyle.min.css" rel="stylesheet" type="text/css">
    <script src="./plus/jquery-ztree/3.5.12/js/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>

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
            <h2><strong id="MainHeaderTitle">点位信息</strong></h2>
            <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
            <div class="btn-group pull-right mr10" data-toggle="buttons">
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addNode">新增</button>
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#changeNode">修改</button>
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#deleteNode">删除</button>
            </div>

        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <section>
                    <strong>所属组织:</strong>
                    <div class="form-group w180 pull-left">
                        <input class="form-control input-sm" name="" placeholder="请选择所属组织" id="citySelect" type="text" readonly value="" onclick="showSelectMenu();" />
                        <div id="selectMenuContent" class="menuContent">
                            <ul id="selectTree" class="ztree"></ul>
                        </div>
                        <script type="text/javascript">
                            var selectSetting = {
                                check: {
                                    enable: true,chkStyle: "radio",radioType: "all"
                                },
                                view: {
                                    dblClickExpand: false
                                },
                                data: {
                                    simpleData: {
                                        enable: true
                                    }
                                },
                                callback: {
                                    onClick: onClick,
                                    onCheck: onCheck
                                }
                            };
                            //测试数据
                            var selectNodes =[
                                {id:1, pId:0, name:"北京"},
                                {id:2, pId:0, name:"天津"},
                                {id:3, pId:0, name:"上海"},
                                {id:6, pId:0, name:"重庆"},
                                {id:4, pId:0, name:"河北省", open:true},
                                {id:41, pId:4, name:"石家庄"},
                                {id:42, pId:4, name:"保定"},
                                {id:43, pId:4, name:"邯郸"},
                                {id:44, pId:4, name:"承德"},
                                {id:5, pId:0, name:"广东省", open:true},
                                {id:51, pId:5, name:"广州"},
                                {id:52, pId:5, name:"深圳"},
                                {id:53, pId:5, name:"东莞"},
                                {id:54, pId:5, name:"佛山"},
                                {id:6, pId:0, name:"福建省", open:true, nocheck:true},
                                {id:61, pId:6, name:"福州"},
                                {id:62, pId:6, name:"厦门"},
                                {id:63, pId:6, name:"泉州"},
                                {id:64, pId:6, name:"三明"}
                            ];

                            function onClick(e, treeId, treeNode) {
                                var zTree = $.fn.zTree.getZTreeObj("selectTree");
                                zTree.checkNode(treeNode, !treeNode.checked, null, true);
                                return false;
                            }

                            function onCheck(e, treeId, treeNode) {
                                var zTree = $.fn.zTree.getZTreeObj("selectTree"),
                                    nodes = zTree.getCheckedNodes(true),
                                    v = "";
                                for (var i=0, l=nodes.length; i<l; i++) {
                                    v += nodes[i].name + ",";
                                }
                                if (v.length > 0 ) v = v.substring(0, v.length-1);
                                var cityObj = $("#citySelect");
                                cityObj.attr("value", v);
                                hideSelectMenu();
                            }

                            function showSelectMenu() {
                                var cityObj = $("#citySelect");
                                var cityOffset = $("#citySelect").offset();
                                $("#selectMenuContent").slideDown("fast");
                                $("body").bind("mousedown", onBodyDown);
                            }
                            function hideSelectMenu() {
                                $("#selectMenuContent").fadeOut("fast");
                                $("body").unbind("mousedown", onBodyDown);
                            }
                            function onBodyDown(event) {
                                if (!(event.target.id == "menuBtn" || event.target.id == "citySelect" || event.target.id == "selectMenuContent" || $(event.target).parents("#selectMenuContent").length>0)) {
                                    hideSelectMenu();
                                }
                            }
                            $(document).ready(function(){
                                $.fn.zTree.init($("#selectTree"), selectSetting, selectNodes);
                            });
                        </script>
                    </div>
                </section>
                <section>
                    <strong>所属线路:</strong>
                    <div class="form-group w120 pull-left">
                        <select class="form-control input-sm">
                            <option value="0">请选择</option>
                            <option value="1">线路1</option>
                            <option value="2">线路2</option>
                        </select>
                    </div>
                </section>
                <section>
                    <strong>点位名称:</strong>
                    <div class="form-group w120 pull-left">
                        <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入点位名称"></div>
                    </div>
                </section>
                <section>
                    <strong>所属区域:</strong>
                    <div class="form-group pull-left">
                        <select class="form-control input-sm w80 pull-left mr5" name="province">
                            <option value="0">请选择</option>
                        </select>
                        <select class="form-control input-sm w80 pull-left mr5" name="city">
                            <option value="0">请选择</option>
                        </select>
                        <select class="form-control input-sm w80 pull-left" name="area">
                            <option value="0">请选择</option>
                        </select>
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
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh10">点位名称</li>
                    <li class="wh7">售货机数量</li>
                    <li class="wh10">所属线路</li>
                    <li class="wh10">所属组织</li>
                    <li class="wh10">区域</li>
                    <li class="wh10">详细地址</li>
                    <li class="wh10">商圈</li>
                    <li class="wh10">更多信息</li>
                    <li class="wh10">创建信息</li>
                    <li class="wh10">修改信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <table>
                <tbody class="auto">
                <tr>
                    <td class="wh3"><input type="checkbox" value=""></td>
                    <td class="wh10">
                        <span>北京新网聚联信息科技有限公司</span>
                    </td>
                    <td class="wh7">
                        <span>20</span>
                    </td>
                    <td class="wh10">
                        <span>所属线路</span>
                    </td>
                    <td class="wh10">
                        <span>所属组织</span>
                    </td>
                    <td class="wh10">
                        <span>区域</span>
                    </td>
                    <td class="wh10">
                        <span>朝阳区来广营社区-诚盈中心</span>
                    </td>
                    <td class="wh10">
                        <span>诚盈中心商圈</span>
                    </td>
                    <td class="wh10">
                        <button type="button" class="btn btn-default f12 btn-sm2" data-toggle="modal" data-target="#moreInfoDialog">查看</button>
                    </td>
                    <td class="wh10">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
                    </td>
                    <td class="wh10">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
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

    <!----
      **  模态框 **
    ---->

    <!-----新建点位 模态框 start----->
    <div class="modal fade" id="addNode" role="dialog" aria-labelledby="新增点位" aria-hidden="true">
        <div class="modal-dialog w800">
            <div class="modal-content">
                <form method="post" action="" name="form" id="" class="unKeyCode">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16" id="">新增点位</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix mt20">
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5" for="location_name">点位名称:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入点位名称" name="" required minlength="2" maxlength="40"></div>
                            </section>
                            <section class="col-md-6">
                                <span class=" pull-left w100 text-right pr5">所属场景:</span>
                                <div class="form-group pull-left w250">
                                    <select class="form-control input-sm">
                                        <option value="0">场景1</option>
                                        <option value="1">场景2</option>
                                    </select>
                                </div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">区域:</span>
                                <div class="form-group pull-left">
                                    <select class="form-control input-sm w80 pull-left mr5" name="province">
                                        <option value="0">请选择</option>
                                    </select>
                                    <select class="form-control input-sm w80 pull-left mr5" name="city">
                                        <option value="0">请选择</option>
                                    </select>
                                    <select class="form-control input-sm w80 pull-left" name="area">
                                        <option value="0">请选择</option>
                                    </select>
                                </div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5" for="location_name">详细地址:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入详细地址" name="" required minlength="2" maxlength="40"></div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">经度:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入经度"></div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">纬度:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入纬度"></div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">所属线路:</span>
                                <div class="form-group w250 pull-left">
                                    <button type="button" class="form-control input-sm" data-toggle="modal" data-target="#selectLine">选择线路</button>
                                </div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">已选线路:</span>
                                <div class="form-group w250 pull-left">
                                    <p><b>高校延长线</b></p>
                                </div>
                            </section>
                            <div class="clearfix"></div>
                            <div class="otherInfoTips"><span class="tipsBtn" id="otherInfoBtn">更多选项</span></div>
                            <div class="otherInfoWrap" id="otherInfoWrap">
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">业务员姓名:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">业务员电话:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">物业名称:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">物业电话:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">物业联系人:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">经营时间:</span>
                                    <div class="form-group w110 pull-left"><input class="form-control input-sm form_time" type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                                    <span class="pull-left ml9">至</span>
                                    <div class="form-group w110 pull-left ml9"><input class="form-control input-sm form_time" type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                                    <div class="clearfix"></div>
                                </section>
                                <div class="clearfix"></div>
                            </div>
                            <script>
                                $(function(){
                                    $('#otherInfoBtn').click(function(){
                                        $('#otherInfoWrap').toggle();
                                    })
                                })
                            </script>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <!-----新增点位 模态框 end----->


    <!-----修改点位 模态框 start----->
    <div class="modal fade" id="changeNode" role="dialog" aria-labelledby="修改点位" aria-hidden="true">
        <div class="modal-dialog w800">
            <div class="modal-content">
                <form method="post" action="" name="form" id="" class="unKeyCode">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16" id="">修改点位</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix mt20">
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5" for="location_name">点位名称:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入点位名称" name="" required minlength="2" maxlength="40"></div>
                            </section>
                            <section class="col-md-6">
                                <span class=" pull-left w100 text-right pr5">所属场景:</span>
                                <div class="form-group pull-left w250">
                                    <select class="form-control input-sm">
                                        <option value="0">场景1</option>
                                        <option value="1">场景2</option>
                                    </select>
                                </div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">区域:</span>
                                <div class="form-group pull-left">
                                    <select class="form-control input-sm w80 pull-left mr5" name="province">
                                        <option value="0">请选择</option>
                                    </select>
                                    <select class="form-control input-sm w80 pull-left mr5" name="city">
                                        <option value="0">请选择</option>
                                    </select>
                                    <select class="form-control input-sm w80 pull-left" name="area">
                                        <option value="0">请选择</option>
                                    </select>
                                </div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5" for="location_name">详细地址:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入详细地址" name="" required minlength="2" maxlength="40"></div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">经度:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入经度"></div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">纬度:</span>
                                <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入纬度"></div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">所属线路:</span>
                                <div class="form-group w250 pull-left">
                                    <button type="button" class="form-control input-sm" data-toggle="modal" data-target="#selectLine">选择线路</button>
                                </div>
                            </section>
                            <section class="col-md-6">
                                <span class="pull-left w100 text-right pr5">已选线路:</span>
                                <div class="form-group w250 pull-left">
                                    <p><b>高校延长线</b></p>
                                </div>
                            </section>
                            <div class="clearfix"></div>
                            <div class="otherInfoTips"><span class="tipsBtn" id="otherInfoBtn">更多选项</span></div>
                            <div class="otherInfoWrap" id="otherInfoWrap">
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">业务员姓名:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">业务员电话:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">物业名称:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">物业电话:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">物业联系人:</span>
                                    <div class="form-group w250 pull-left"><input class="form-control input-sm" type="text" placeholder="非必填"></div>
                                </section>
                                <section class="col-md-6">
                                    <span class="pull-left w100 text-right pr5">经营时间:</span>
                                    <div class="form-group w110 pull-left"><input class="form-control input-sm form_time" type="text" placeholder="开始时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                                    <span class="pull-left ml9">至</span>
                                    <div class="form-group w110 pull-left ml9"><input class="form-control input-sm form_time" type="text" placeholder="结束时间" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii"></div>
                                    <div class="clearfix"></div>
                                </section>
                                <div class="clearfix"></div>
                            </div>
                            <script>
                                $(function(){
                                    $('#otherInfoBtn').click(function(){
                                        $('#otherInfoWrap').toggle();
                                    })
                                })
                            </script>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <!-----修改点位 模态框 end----->

    <!--选择线路 -->
    <div class="modal fade" id="selectLine" tabindex="-1" role="dialog" aria-labelledby="选择线路" aria-hidden="true">
        <div class="modal-dialog w800">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">选择线路</h4>
                </div>
                <div class="modal-body">
                    <div class="modal_commonbody clearfix">
                        <div class="main_commonhead hl30 b1 clearfix">
                            <form method="post" action="" name="form" id="" class="unKeyCode">
                                <section>
                                    <strong>线路名称:</strong>
                                    <div class="form-group w120 pull-left mr10">
                                        <input class="form-control input-sm" type="text" placeholder="请输入经度">
                                    </div>
                                </section>
                                <section>
                                    <strong>所属组织:</strong>
                                    <div class="form-group w180 pull-left">
                                        <input class="form-control input-sm" name="" placeholder="请选择所属组织" id="selectParty" type="text" readonly value="" onclick="showSelectPartyMenu();" />
                                        <div id="selectPartyMenuContent" class="menuContent">
                                            <ul id="selectPartyTree" class="ztree"></ul>
                                        </div>
                                        <script type="text/javascript">
                                            var partySetting = {
                                                check: {
                                                    enable: true,chkStyle: "radio",radioType: "all"
                                                },
                                                view: {
                                                    dblClickExpand: false
                                                },
                                                data: {
                                                    simpleData: {
                                                        enable: true
                                                    }
                                                },
                                                callback: {
                                                    onClick: onClick,
                                                    onCheck: onCheck
                                                }
                                            };
                                            //测试数据
                                            var partyNodes =[
                                                {id:1, pId:0, name:"北京"},
                                                {id:2, pId:0, name:"天津"},
                                                {id:3, pId:0, name:"上海"},
                                                {id:6, pId:0, name:"重庆"},
                                                {id:4, pId:0, name:"河北省", open:true},
                                                {id:41, pId:4, name:"石家庄"},
                                                {id:42, pId:4, name:"保定"},
                                                {id:43, pId:4, name:"邯郸"},
                                                {id:44, pId:4, name:"承德"},
                                                {id:5, pId:0, name:"广东省", open:true},
                                                {id:51, pId:5, name:"广州"},
                                                {id:52, pId:5, name:"深圳"},
                                                {id:53, pId:5, name:"东莞"},
                                                {id:54, pId:5, name:"佛山"},
                                                {id:6, pId:0, name:"福建省", open:true, nocheck:true},
                                                {id:61, pId:6, name:"福州"},
                                                {id:62, pId:6, name:"厦门"},
                                                {id:63, pId:6, name:"泉州"},
                                                {id:64, pId:6, name:"三明"}
                                            ];

                                            function onClick(e, treeId, treeNode) {
                                                var zTree = $.fn.zTree.getZTreeObj("selectPartyTree");
                                                zTree.checkNode(treeNode, !treeNode.checked, null, true);
                                                return false;
                                            }

                                            function onCheck(e, treeId, treeNode) {
                                                var zTree = $.fn.zTree.getZTreeObj("selectPartyTree"),
                                                    nodes = zTree.getCheckedNodes(true),
                                                    v = "";
                                                for (var i=0, l=nodes.length; i<l; i++) {
                                                    v += nodes[i].name + ",";
                                                }
                                                if (v.length > 0 ) v = v.substring(0, v.length-1);
                                                var cityObj = $("#selectParty");
                                                cityObj.attr("value", v);
                                                hideSelectPartyMenu();
                                            }

                                            function showSelectPartyMenu() {
                                                var cityObj = $("#selectParty");
                                                var cityOffset = $("#selectParty").offset();
                                                $("#selectPartyMenuContent").slideDown("fast");
                                                $("body").bind("mousedown", onBodyDown);
                                            }
                                            function hideSelectPartyMenu() {
                                                $("#selectPartyMenuContent").fadeOut("fast");
                                                $("body").unbind("mousedown", onBodyDown);
                                            }
                                            function onBodyDown(event) {
                                                if (!(event.target.id == "menuBtn" || event.target.id == "selectParty" || event.target.id == "selectPartyMenuContent" || $(event.target).parents("#selectPartyMenuContent").length>0)) {
                                                    hideSelectPartyMenu();
                                                }
                                            }
                                            $(document).ready(function(){
                                                $.fn.zTree.init($("#selectPartyTree"), partySetting, partyNodes);
                                            });
                                        </script>
                                    </div>
                                </section>
                                <button type="button" class="btn btn-default f12 btn-lg2">查询</button>
                                <button type="reset" class="btn btn-default f12 btn-lg2">重置</button>
                            </form>
                        </div>
                        <div class="main_commontable max300">
                            <table>
                                <thead>
                                <tr>
                                    <th width="30" class="text-center"><input type="checkbox" class="input_v4" value=""></th>
                                    <th class="text-center"><span>线路姓名</span></th>
                                    <th class="text-center"><span>所属组织</span></th>
                                    <th class="text-center"><span>配送员</span></th>
                                </tr>
                                </thead>
                                <tbody class="auto">
                                <tr>
                                    <td><input type="checkbox" value=""></td>
                                    <td>
                                        <span>高校延长线</span>
                                    </td>
                                    <td>
                                        <span>搬砖小分队</span>
                                    </td>
                                    <td>
                                        <span>巴顿</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><input type="checkbox" value=""></td>
                                    <td>
                                        <span>高校延长线</span>
                                    </td>
                                    <td>
                                        <span>搬砖小分队</span>
                                    </td>
                                    <td>
                                        <span>巴顿</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><input type="checkbox" value=""></td>
                                    <td>
                                        <span>高校延长线</span>
                                    </td>
                                    <td>
                                        <span>搬砖小分队</span>
                                    </td>
                                    <td>
                                        <span>巴顿</span>
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
                <div class="modal-footer">
                    <button type="button" class="btn btn-success">确定</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>


    <!-----删除点位 模态框 start----->
    <div class="modal fade" id="deleteNode" tabindex="-1" role="dialog" aria-labelledby="删除点位" aria-hidden="true">
        <div class="modal-dialog w400">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16">删除点位</h4>
                </div>
                <div class="modal-body">
                    <div class="modal_commonbody clearfix">
                        <div class="step_info text-center">
                            <p>该点位关联了有效数据，无法删除</p>
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


    <!-----更多信息 模态框 start----->
    <div class="modal fade" id="moreInfoDialog" tabindex="-1" role="dialog" aria-labelledby="更多信息" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h4 class="modal-title f16" id="">更多信息</h4>
                </div>
                <div class="modal-body">
                    <div class="main_commontable max400 border_line">
                        <table>
                            <tbody class="auto">
                            <tr>
                                <td>经度:</td>
                                <td class="bgGreen"><span>136.22566</span></td>
                                <td>纬度:</td>
                                <td class="bgGreen"><span>136.22566</span></td>
                            </tr>
                            <tr>
                                <td>业务员姓名:</td>
                                <td class="bgGreen"><span>巴顿</span></td>
                                <td>业务员电话:</td>
                                <td class="bgGreen"><span>13622566999</span></td>
                            </tr>
                            <tr>
                                <td>经营时间:</td>
                                <td class="bgGreen"><span>20:00-23:59</span></td>
                                <td>物业名称:</td>
                                <td class="bgGreen"><span>韦德物业</span></td>
                            </tr>
                            <tr>
                                <td>物业联系人:</td>
                                <td class="bgGreen"><span>巴顿</span></td>
                                <td>物业电话:</td>
                                <td class="bgGreen"><span>13622566999</span></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
</html>

