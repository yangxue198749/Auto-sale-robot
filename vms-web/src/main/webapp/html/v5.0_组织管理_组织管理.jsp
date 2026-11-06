<%--
  Created by IntelliJ IDEA.
  User: Tonney
  Date: 2017/11/1
  Time: 15:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>组织管理 | 贩售机管理系统 5.0</title>
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
            <h2><strong id="MainHeaderTitle">组织管理</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addUser">新增</button>
                <button type="button" class="btn btn-default btn-lg2 " data-toggle="modal" data-target="#deleteUser">删除</button>
                <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#changUser">修改</button>
            </div>
        </div>
        <!--Main CommonHead-->

        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!-- body -->
            <div id="companyTree" class="ztree one"></div>
            <script type="text/javascript">
                var intSetting = {};
                //测试数据
                var intNodes =[
                    { name:"父节点1 - 展开", open:true,
                        children: [
                            { name:"父节点11 - 折叠",
                                children: [
                                    { name:"叶子节点111"},
                                    { name:"叶子节点112"},
                                    { name:"叶子节点113"},
                                    { name:"叶子节点114"}
                                ]},
                            { name:"父节点12 - 折叠",
                                children: [
                                    { name:"叶子节点121"},
                                    { name:"叶子节点122"},
                                    { name:"叶子节点123"},
                                    { name:"叶子节点124"}
                                ]},
                            { name:"父节点13 - 没有子节点", isParent:true}
                        ]},
                    { name:"父节点2 - 折叠",
                        children: [
                            { name:"父节点21 - 展开", open:true,
                                children: [
                                    { name:"叶子节点211"},
                                    { name:"叶子节点212"},
                                    { name:"叶子节点213"},
                                    { name:"叶子节点214"}
                                ]},
                            { name:"父节点22 - 折叠",
                                children: [
                                    { name:"叶子节点221"},
                                    { name:"叶子节点222"},
                                    { name:"叶子节点223"},
                                    { name:"叶子节点224"}
                                ]},
                            { name:"父节点23 - 折叠",
                                children: [
                                    { name:"叶子节点231"},
                                    { name:"叶子节点232"},
                                    { name:"叶子节点233"},
                                    { name:"叶子节点234"}
                                ]}
                        ]},
                    { name:"父节点3 - 没有子节点", isParent:true}

                ];

                $(document).ready(function(){
                    $.fn.zTree.init($("#companyTree"), intSetting, intNodes);
                    //全部展开
                    var treeObj = $.fn.zTree.getZTreeObj("companyTree");
                    treeObj.expandAll(true);
                });
            </script>
            <!-- /body -->
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建组织 模态框 start----->
    <div class="modal fade" id="addUser" tabindex="-1" role="dialog" aria-labelledby="新建组织" aria-hidden="true">
        <div class="modal-dialog w400">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">新建组织</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <div class="step_info">
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">组织名称:</span>
                                    <div class="form-group pull-left">
                                        <input type="text"class="w220 form-control input-sm" name="" placeholder="请输入组织名称" required minlength="2" maxlength="50">
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">所属组织:</span>
                                    <div class="form-group pull-left">
                                        <input class="w220 form-control input-sm" name="" placeholder="请输入所属组织" id="citySel" type="text" readonly value="" onclick="showMenu();" />
                                        <div id="menuContent" class="menuContent">
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
                                                var cityObj = $("#citySel");
                                                cityObj.attr("value", v);
                                                hideMenu();
                                            }

                                            function showMenu() {
                                                var cityObj = $("#citySel");
                                                var cityOffset = $("#citySel").offset();
                                                $("#menuContent").slideDown("fast");
                                                $("body").bind("mousedown", onBodyDown);
                                            }
                                            function hideMenu() {
                                                $("#menuContent").fadeOut("fast");
                                                $("body").unbind("mousedown", onBodyDown);
                                            }
                                            function onBodyDown(event) {
                                                if (!(event.target.id == "menuBtn" || event.target.id == "citySel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
                                                    hideMenu();
                                                }
                                            }
                                            $(document).ready(function(){
                                                $.fn.zTree.init($("#addTree"), addSetting, addNodes);
                                            });
                                        </script>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">组织类型:</span>
                                    <div class="form-group pull-left">
                                        <span class="mr10"><input type="radio" class="input_v4" name="radio" value="1">自营</span>
                                        <span class="mr10"><input type="radio" class="input_v4" name="radio" value="2">代理</span>
                                        <span class="mr10"><input type="radio" class="input_v4" name="radio" value="3">销售</span>
                                    </div>
                                </div>
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


    <!-----修改组织 模态框 start----->
    <div class="modal fade" id="changUser" tabindex="-1" role="dialog" aria-labelledby="修改组织" aria-hidden="true">
        <div class="modal-dialog w400">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">修改组织</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <div class="step_info">
                                <div class="col-md-12">
                                    <span class="pull-left w60 text-right mr20">组织名称:</span>
                                    <div class="form-group pull-left">
                                        <input type="text"class="w220 form-control input-sm" name="" placeholder="请输入组织名称" required minlength="2" maxlength="50">
                                    </div>
                                </div>
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

    <!-----删除组织 模态框 start----->
    <div class="modal fade" id="deleteUser" tabindex="-1" role="dialog" aria-labelledby="删除组织" aria-hidden="true">
        <div class="modal-dialog w400">
            <form method="post" action="" name="form" id="" class="unKeyCode">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title f16">删除组织</h4>
                    </div>
                    <div class="modal-body">
                        <div class="modal_commonbody clearfix">
                            <div class="step_info text-center">
                                <p>该组织关联了有效数据，无法删除</p>
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

