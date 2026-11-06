<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>售货机库存 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
    <script src="./plus/textSum/textSum.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="${path}/vmsjs/vmStock.js"></script>
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
            <jsp:include page="/loadMenu?menuCode=vmStock-list&code=statistical-analysis"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=statistical-analysis"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">售货机库存</strong></h2>
            <button type="button" id="exportButton" class="btn btn-success btn-lg2 pull-right">导出</button>
        </div>
        <div class="col-md-12">
            <!--Main CommonHead-->
            <div class="main_commonhead hl30">
                <form method="post" action="${path}/vmStock/listPage" name="form" id="search-form" class="unKeyCode">
                    <section class="diySltInput">
                        <strong>搜索:</strong>
                        <div class="form-group w100 pull-left">
                            <select class="form-control input-sm" name="selectType">
                                <option value="1">售货机编号</option>
                                <option value="2">点位</option>
                            </select>
                        </div>
                        <div class="form-group w160 pull-left">
                            <input class="form-control input-sm" type="text" placeholder="请输入搜索内容" name="selectValue">
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
            <div class="main_commontable">
                <div class="wh60 pull-left" style="padding-right: 10px;">
                    <table class="bgf border_line" id="stockTable">
                        <thead>
                        <tr>
                           <%-- <td width="30"><input type="checkbox" value=""></td>--%>
                            <td class="w20"><span>售货机编号</span></td>
                            <td class="w20"><span>点位名称</span></td>
                            <td class="w15"><span>机存余量</span></td>
                            <td class="w45"><span>缺货商品</span></td>
                        </tr>
                        </thead>
                    </table>
                    <div id="pagediv"> <jsp:include page="/vmStock/listPage" flush="true"/> </div>
                    <%-- <script>
                         $(function(){
                             $('#stockTable thead input[type="checkbox"]').click(function(){
                                 if($(this).prop('checked')){
                                     $('#pagediv tbody input[type="checkbox"]').prop('checked',true);
                                 }else{
                                     $('#pagediv tbody input[type="checkbox"]').prop('checked',false);
                                 }
                             })
                         })
                     </script>--%>
                </div>
                <form action="/vmStock/stockList" method="POST" id="search-stack-form">
                    <input name="vmId" type="hidden">
                </form>
                <div class="wh40 pull-left">
                    <table class="bgf">
                        <thead>
                        <tr>
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
                    </table>
                    <div id="stock-pagediv"></div>
                </div>
            </div>
        </div>

    </div>


</div>
</body>
</html>
<script type="text/javascript">
    $(function () {
        VmStock.init();
    })
</script>