<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>我的商品库 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="/vmsjs/sku_library.js"></script>
    <script type="text/javascript" src="/vmsjs/sku.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=sku-library&code=data-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=data-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">我的商品库</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('sku-library-add')}">
                    <button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#addStock" href="/skuLibrary/addSkuLibraryDialog">添加</button>
                </c:if>
                <c:if test="${u:isPermitted('sku-library-delete')}">
                    <button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" href="${path}/skuLibrary/delete" >删除</button>
                </c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <div class="main_commonhead hl30">
            <form action="/skuLibrary/listPage" method="post" id="search-form" class="unKeyCode">
                <section>
                    <strong>商品名称:</strong>
                    <div class="form-group w160 pull-left"><input name="skuName" class="form-control input-sm" type="text" placeholder="请输入商品名称"></div>
                </section>
                <section>
                    <strong>商品编码:</strong>
                    <div class="form-group w160 pull-left"><input name="skuCode" class="form-control input-sm" type="text" placeholder="请输入商品编码"></div>
                </section>
                <section>
                    <strong>类型组:</strong>
                    <div class="form-group w120 pull-left">
                        <select name="skuTypePid" class="pull-left form-control input-sm" id="skuTypeGroup" onchange="refreshRenderSkuType()" >
                        </select>
                    </div>
                </section>
                <section>
                    <strong>类型:</strong>
                    <div class="form-group w120 pull-left">
                        <select name="skuTypeId" class="form-control input-sm" id="skuType" >
                        </select>
                    </div>
                </section>
                <div class="diyBtnGroup pull-left">
                    <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                    <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>
            </form>
        </div>

        <!--Main CommonTable-->
        <div class="main_commontable tablePlaceholder">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh8">商品图片</li>
                    <li class="wh14">商品名称</li>
                    <li class="wh5">商品规格</li>
                    <li class="wh5">商品编码</li>
                    <li class="wh10">建议零售价(分)</li>
                    <li class="wh5">商品大类</li>
                    <li class="wh5">商品小类</li>
                    <li class="wh10">尺寸(毫米)</li>
                    <li class="wh15">商品描述</li>
                    <li class="wh5">销售区域</li>
                    <li class="wh5">其他信息</li>
                    <li class="wh10">创建信息</li>
                </ul>
            </div>
            <div id="pagediv">
                <jsp:include page="/skuLibrary/listPage" flush="true"/>
            </div>

        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!--其他信息 -->
    <div class="modal fade" id="checkInfo" tabindex="-1" role="dialog" aria-labelledby="查看其他信息" aria-hidden="true"></div>
    <!-----选择商品 #addStock 模态框 start----->
    <div class="modal fade" id="addStock" tabindex="-1" role="dialog" aria-labelledby="选择商品" aria-hidden="true"></div>
    <!-----选择商品 #addStock 模态框 end----->

</div>
</body>
</html>
<script>
    var sku = '${u:getSkuJson()}';
    // 将字符串转成JSON对象
    var skuJson = eval("(" + sku + ")");
    renderSkuTypeGroup();
    renderSkuType("");
    SkuLibrary.init();
</script>

