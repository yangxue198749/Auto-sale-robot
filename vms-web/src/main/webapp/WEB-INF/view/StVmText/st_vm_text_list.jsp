<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>文字走马灯策略 | 贩售机管理系统</title>
    <%@ include file="/WEB-INF/view/include/head.jsp" %>
    <script type="text/javascript" src="${path }/vmsjs/st_vm_text.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=st-vm-text&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">文字走马灯策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('st-vm-text-add')}"><button type="button" class="btn btn-default btn-lg2" href="${path }/stVmText/addStVmTextDialog" data-toggle="modal" data-target="#addStVmText">新增</button></c:if>
                <c:if test="${u:isPermitted('st-vm-text-edit')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type=checkbox]:checked" href="${path }/stVmText/submitDeleteStVmText">删除</button></c:if>
                <c:if test="${u:isPermitted('st-vm-text-delete')}"><button type="button" class="btn btn-default btn-lg2" href="${path }/stVmText/editStVmTextDialog" data-toggle="modal" data-target="#editStVmText" data-must="true" data-set="#pagediv table tbody input:checkbox" select-one="true">修改</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
	    <form action="/stVmText/listPage" method="POST" id ="search-form">
            <div class="main_commonhead hl30">
                <section>
                    <strong>策略名称:</strong>
                    <div class="form-group w120 pull-left">
                        <input class="form-control input-sm" name="searchStVmName" type="text" placeholder="请输入策略名称">
                    </div>
                </section>
                <section>
                    <strong>走马灯文案:</strong>
                    <div class="form-group w120 pull-left">
                        <div class="form-group w120 pull-left">
                            <input class="form-control input-sm" name="searchStVmText" type="text" placeholder="请输入策略名称">
                        </div>
                    </div>
                </section>
                <div class="diyBtnGroup pull-left">
                    <button type="button" id="searchButton" class="btn btn-default f12 btn-lg2">查询</button>
                    <button onclick="reset('search-form')" type="button" class="btn btn-default f12 btn-lg2">重置</button>
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>
            </div>
	    </form>
        <!--Main CommonTable-->
        <div class="main_commontable">
            <!--theadscroll start-->
            <div class="theadscroll" id="theadScroll">
                <ul>
                    <li class="wh3"><input type="checkbox" value=""></li>
                    <li class="wh15">策略名称</li>
                    <li class="wh20">策略说明</li>
                    <li class="wh10">走马灯文案</li>
                    <li class="wh10">创建人</li>
                    <li class="wh15">创建时间</li>
                    <li class="wh12">修改人</li>
                    <li class="wh15">修改时间</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	<jsp:include page="/stVmText/listPage" flush="true" />
            </div>
            
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----走马灯配置策略 模态框 start----->
    <div class="modal fade" id="viewFlashBuy" tabindex="-1" role="dialog" aria-labelledby="闪购键配置策略" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title f16" id="">查看走马灯文案</h4>
                </div>
                <div class="modal-body">
                    <!--走马灯文案 start-->
                          <div>
                               <p>文案: <span>欢迎购买</span></p>
                          </div>
                    <!--走马灯文案 end-->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
                </div>
            </div>
        </div>
    </div>
    <!-----走马灯配置策略 模态框 end----->
    <!-----新建走马灯配置策略 模态框 start----->
    <div class="modal fade" id="addStVmText" tabindex="-1" role="dialog" aria-labelledby="新建走马灯配置策略" aria-hidden="true">
        
    </div>
    <!-----新建走马灯配置策略 模态框 end----->
    <!-----修改走马灯配置策略 模态框 start----->
    <div class="modal fade" id="editStVmText" tabindex="-1" role="dialog" aria-labelledby="修改走马灯配置策略" aria-hidden="true">
        
    </div>
	<!-----修改走马灯配置策略 模态框 end----->



    <!-----窗口 模态框----->
    <div class="modal fade" id="showText" tabindex="-1" role="dialog" aria-labelledby="查看走马灯配置策略" aria-hidden="true">
    </div>

</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
    	StVmText.init();
    	
    })
</script>
