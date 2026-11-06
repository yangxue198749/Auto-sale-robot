<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/1
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>线路管理 | 贩售机管理系统 5.0</title>
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
            <h2><strong id="MainHeaderTitle">线路管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addLine">新增</button>
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#deleteLine">删除</button>
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#changeLine">修改</button>
            </div>

        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <section>
                    <strong>线路名称:</strong>
                    <div class="form-group w120 pull-left">
                        <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" placeholder="请输入搜索内容"></div>
                    </div>
                </section>
                <section>
                    <strong>所属组织:</strong>
                    <div class="form-group w220 pull-left">
                        <input class="w220 form-control input-sm" name="" placeholder="请选择所属组织" id="citySelect" type="text" readonly value="" onclick="showSelectMenu();" />
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
                <button type="button" class="btn btn-default f12 btn-lg2">查询</button>
                <button type="reset" class="btn btn-default f12 btn-lg2">查询</button>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh20">线路名称</li>
                    <li class="wh20">所属组织</li>
                    <li class="wh10">配送员</li>
                    <li class="wh9">点位数量</li>
                    <li class="wh8">贩售机数量</li>
                    <li class="wh15">创建信息</li>
                    <li class="wh15">修改信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <table>
                <tbody class="auto">
                <tr>
                    <td class="wh3"><input type="checkbox" value=""></td>
                    <td class="wh20">
                        <span>北京新网聚联信息科技有限公司</span>
                    </td>
                    <td class="wh20">
                        <span>北京新网聚联信息科技有限公司</span>
                    </td>
                    <td class="wh10">
                        <span>Jack</span>
                    </td>
                    <td class="wh9">
                        <span>20</span>
                    </td>
                    <td class="wh8">
                        <span>20</span>
                    </td>
                    <td class="wh15">
                        <p><span>尧舜</span></p>
                        <p><span>2016/11/16 11:36:56</span></p>
                    </td>
                    <td class="wh15">
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
    <!-----新建线路 模态框 start----->
    <div class="modal fade" id="addLine" tabindex="-1" role="dialog" aria-labelledby="新建线路" aria-hidden="true">
        <div class="modal-dialog w400">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">新建线路</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <!--填写基本信息-->
                            <div class="step_info">
                                <div class="col-md-12">
                                    <span class="pull-left w80 text-right mr20">组织名称:</span>
                                    <div class="form-group pull-left">
                                        <input type="text"class="w220 form-control input-sm" name="" placeholder="请输入组织名称" required minlength="2" maxlength="50">
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w80 text-right mr20">所属组织:</span>
                                    <div class="form-group pull-left">
                                        <input class="w220 form-control input-sm" name="" placeholder="请输入所属组织" id="addCitySel" type="text" readonly value="" onclick="addShowMenu();" />
                                        <div id="addMenuContent" class="menuContent">
                                            <ul id="addTree" class="ztree"></ul>
                                        </div>
                                        <script type="text/javascript">
                                            var addSetting = {
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
                                            var addNodes =[
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
                                                var zTree = $.fn.zTree.getZTreeObj("addTree");
                                                zTree.checkNode(treeNode, !treeNode.checked, null, true);
                                                return false;
                                            }

                                            function onCheck(e, treeId, treeNode) {
                                                var zTree = $.fn.zTree.getZTreeObj("addTree"),
                                                    nodes = zTree.getCheckedNodes(true),
                                                    v = "";
                                                for (var i=0, l=nodes.length; i<l; i++) {
                                                    v += nodes[i].name + ",";
                                                }
                                                if (v.length > 0 ) v = v.substring(0, v.length-1);
                                                var cityObj = $("#addCitySel");
                                                cityObj.attr("value", v);
                                                addHideMenu();
                                            }

                                            function addShowMenu() {
                                                var cityObj = $("#addCitySel");
                                                var cityOffset = $("#addCitySel").offset();
                                                $("#addMenuContent").slideDown("fast");
                                                $("body").bind("mousedown", onBodyDown);
                                            }
                                            function addHideMenu() {
                                                $("#addMenuContent").fadeOut("fast");
                                                $("body").unbind("mousedown", onBodyDown);
                                            }
                                            function onBodyDown(event) {
                                                if (!(event.target.id == "menuBtn" || event.target.id == "addCitySel" || event.target.id == "addMenuContent" || $(event.target).parents("#addMenuContent").length>0)) {
                                                    addHideMenu();
                                                }
                                            }
                                            $(document).ready(function(){
                                                $.fn.zTree.init($("#addTree"), addSetting, addNodes);
                                            });
                                        </script>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w80 text-right mr20">设置配送员:</span>
                                    <div class="form-group pull-left">
                                        <button type="button" class="w220 form-control input-sm" data-toggle="modal" data-target="#selectCourier">选择配送员</button>
                                        <p><span class="mr5">Jack的配送员</span></p>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确认</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>


    <!--修改线路 -->
    <div class="modal fade" id="changeLine" tabindex="-1" role="dialog" aria-labelledby="修改线路" aria-hidden="true">
        <div class="modal-dialog w400">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">修改线路</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <!--填写基本信息-->
                            <div class="step_info">
                                <div class="col-md-12">
                                    <span class="pull-left w80 text-right mr20">组织名称:</span>
                                    <div class="form-group pull-left">
                                        <input type="text"class="w220 form-control input-sm" name="" placeholder="请输入组织名称" required minlength="2" maxlength="50">
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w80 text-right mr20">所属组织:</span>
                                    <div class="form-group pull-left">
                                        <input class="w220 form-control input-sm" name="" placeholder="请输入所属组织" id="changeCitySel" type="text" readonly value="" onclick="changeShowMenu();" />
                                        <div id="changeMenuContent" class="menuContent">
                                            <ul id="changeTree" class="ztree"></ul>
                                        </div>
                                        <script type="text/javascript">
                                            var changeSetting = {
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
                                            var changeNodes =[
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
                                                var zTree = $.fn.zTree.getZTreeObj("changeTree");
                                                zTree.checkNode(treeNode, !treeNode.checked, null, true);
                                                return false;
                                            }

                                            function onCheck(e, treeId, treeNode) {
                                                var zTree = $.fn.zTree.getZTreeObj("changeTree"),
                                                    nodes = zTree.getCheckedNodes(true),
                                                    v = "";
                                                for (var i=0, l=nodes.length; i<l; i++) {
                                                    v += nodes[i].name + ",";
                                                }
                                                if (v.length > 0 ) v = v.substring(0, v.length-1);
                                                var cityObj = $("#changeCitySel");
                                                cityObj.attr("value", v);
                                                changeHideMenu();
                                            }

                                            function changeShowMenu() {
                                                var cityObj = $("#changeCitySel");
                                                var cityOffset = $("#changeCitySel").offset();
                                                $("#changeMenuContent").slideDown("fast");
                                                $("body").bind("mousedown", onBodyDown);
                                            }
                                            function changeHideMenu() {
                                                $("#changeMenuContent").fadeOut("fast");
                                                $("body").unbind("mousedown", onBodyDown);
                                            }
                                            function onBodyDown(event) {
                                                if (!(event.target.id == "menuBtn" || event.target.id == "changeCitySel" || event.target.id == "changeMenuContent" || $(event.target).parents("#changeMenuContent").length>0)) 				{
                                                    changeHideMenu();
                                                }
                                            }
                                            $(document).ready(function(){
                                                $.fn.zTree.init($("#changeTree"), changeSetting, changeNodes);
                                            });
                                        </script>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w80 text-right mr20">设置配送员:</span>
                                    <div class="form-group pull-left">
                                        <button type="button" class="w220 form-control input-sm" data-toggle="modal" data-target="#selectCourier">选择配送员</button>
                                        <p><span class="mr5">Jack的配送员</span></p>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确认</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>


    <!--选择配送员 -->
    <div class="modal fade" id="selectCourier" tabindex="-1" role="dialog" aria-labelledby="选择配送员" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">选择配送员</h4>
                </div>
                <div class="modal-body">
                    <div class="modal_commonbody clearfix">
                        <div class="main_commontable max300">
                            <table>
                                <thead>
                                <tr>
                                    <th width="30" class="text-center"><input type="checkbox" class="input_v4" value=""></th>
                                    <th class="text-center"><span>用户姓名</span></th>
                                    <th class="text-center"><span>登录账号</span></th>
                                    <th class="text-center"><span>手机号码</span></th>
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
                                    <td>
                                        <span>13666636363</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><input type="checkbox" value=""></td>
                                    <td>
                                        <span>搬砖的</span>
                                    </td>
                                    <td>
                                        <span>BanZhuanDe</span>
                                    </td>
                                    <td>
                                        <span>13666636363</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><input type="checkbox" value=""></td>
                                    <td>
                                        <span>搬砖的</span>
                                    </td>
                                    <td>
                                        <span>BanZhuanDe</span>
                                    </td>
                                    <td>
                                        <span>13666636363</span>
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


    <!-----删除线路 模态框 start----->
    <div class="modal fade" id="deleteLine" tabindex="-1" role="dialog" aria-labelledby="删除线路" aria-hidden="true">
        <div class="modal-dialog w400">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">删除线路</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <div class="step_info text-center">
                                <p>是否确认删除该线路？</p>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

</div>
</body>
</html>

