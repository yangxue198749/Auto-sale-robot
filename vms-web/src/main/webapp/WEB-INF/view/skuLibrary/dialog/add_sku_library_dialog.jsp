<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w1000">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择商品</h4>
        </div>
        <form class="unKeyCode">
            <div class="modal-body">
                <!--货道设置信息 start-->
                <div class="modal_commonbody clearfix">
                    <div class="main_commonhead hl30">
        </form>
        <section>
            <strong>商品分类:</strong>
            <div class="form-group w120 pull-left mr10">
                <select name="skuTypePid" class="pull-left form-control input-sm" id="typeGroupAdd" onchange="refreshRenderType()" >
                </select>
            </div>
            <div class="form-group w120 pull-left" name="skuType">
                <select name="skuTypeId" class="form-control input-sm" id="typeAdd" >
                </select>
            </div>
        </section>
        <section>
            <strong>商品名称:</strong>
            <div class="form-group w120 pull-left"><input class="form-control input-sm" id="dialogSkuName" name="skuName" type="text" placeholder="请输入商品名称"></div>
        </section>
        <section>
            <strong>商品编码:</strong>
            <div class="form-group w120 pull-left"><input class="form-control input-sm" id="dialogSkuCode" name="skuCode" type="text" placeholder="请输入商品编码"></div>
        </section>
        <button type="button" id="searchSkuBtn" class="btn btn-default f12 btn-lg2 mr10">查询</button>
        <%--<a href="#skuInfo" id="searchSkuBtn" class="btn btn-default f12 btn-lg2 mr10">查询</a>--%>
    </div>
    <!--商品 box start-->
    <div class="col_relation mt15 small choiceTheGoods" id="skuInfo">
        <ul id="skuUl" style="overflow: hidden;"></ul>
        <%--<jsp:include page="/skuLibrary/skuListPage" flush="true"/>--%>
    </div>
    <!--商品 box end-->
    <script>
        $(function(){
            $(".choiceTheGoods").on("click","li", function(){
                $(this).toggleClass("on");
            })
        })
    </script>
</div>
</div>
<!--按钮-->
    <div class="modal-footer">
        <button id="addToSkuLibraryBtn" type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
    </div>
</div>
</div>
<script type="text/javascript" src="${path}/vmsjs/skuType.js"></script>
<%--<script type="text/javascript">--%>
    <%--var sku = '${u:getSkuJson()}';--%>
    <%--// 将字符串转成JSON对象--%>
    <%--var skuJson = eval("(" + sku + ")");--%>

    <%--// 页面加载完成后执行的方法--%>
    <%--$(function(){--%>
        <%--renderTypeGroup();--%>

        <%--var groupId = $("#typeGroupAdd").val();--%>

        <%--renderType(groupId);--%>

        <%--$("[data-toggle='tooltip']").tooltip();--%>
        <%--SkuLibrary.addSkuLibrary();--%>
    <%--});--%>

<%--</script>--%>

<script type="text/javascript">
    var sku = '${u:getSkuJson()}';
    // 将字符串转成JSON对象
    var skuJson = eval("(" + sku + ")");
    // 页面加载完成后执行的方法

    var pageNo = 1;
    var loading = false;
    function load(){
        loading = true;
        pageNo = loadAllSku(null,$('#dialogSkuName').val(),$('#dialogSkuCode').val(),$('#typeGroupAdd').find("option:selected").val(),$('#typeAdd').find("option:selected").val(),pageNo);
        loading = false;
    }

    // 下拉加载
    $('#skuInfo').scroll(function(){
        totalheight = parseFloat($('#skuInfo').height()) + parseFloat($('#skuInfo').scrollTop()) + 120;
        if($('#skuUl').height() <= totalheight){
            if(!loading){
                load();
            }
        }
    });


    $(function(){
        renderTypeGroup();
        var groupId = $("#typeGroupAdd").val();
        renderType(groupId);
        $("[data-toggle='tooltip']").tooltip();
        load();
        SkuLibrary.addSkuLibrary();
        $('#searchSkuBtn').bind('click',function(){
            //$('#skuUl').html("");
            $('#skuUl').empty();
            $('#skuInfo').animate({scrollTop:0});
            pageNo = 1;
            load();
        });
    });


</script>