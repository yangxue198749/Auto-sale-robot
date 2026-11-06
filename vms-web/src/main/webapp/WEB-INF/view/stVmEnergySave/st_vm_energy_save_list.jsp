<%@ page language="java" contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>节能策略 | 贩售机管理系统</title>
   	<%@ include file="/WEB-INF/view/include/head.jsp" %>
	<script type="text/javascript" src="${path}/vmsjs/st_vm_energy_save.js"></script>
    <!--[if lt IE 9]>
    <script src="./plus/html5shiv/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="mp_container">
    <div class="mp_leftmenu">
        <div class="menu_wrap" id="LeftMenuGroup">
           <jsp:include page="/loadMenu?menuCode=st-vm-energy&code=operate-manage"/>
        </div>
    </div>
    <div class="mp_topheader fixed">
        <jsp:include page="/home?code=operate-manage"/>
        <%@ include file="/WEB-INF/view/include/head-right.jsp" %>
    </div>
    <div class="mp_main pt125">
        <!--Main Min topheader-->
        <div class="mp_topheader ver2 ver_fixed">
            <h2><strong id="MainHeaderTitle">节能策略</strong></h2>
            <div class="btn-group pull-right" data-toggle="buttons">
                <c:if test="${u:isPermitted('st-vm-energy-add')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" href="/stVmEnergySave/addStVmEnergyDialog" data-target="#addenergy">新增</button></c:if>
                <c:if test="${u:isPermitted('st-vm-energy-edit')}"><button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-set="#pagediv table tbody input[type='checkbox']:checked" data-must="true" href="/stVmEnergySave/deleteStVmStrategy">删除</button></c:if>
                <c:if test="${u:isPermitted('st-vm-energy-delete')}"><button type="button" class="btn btn-default btn-lg2" data-toggle="modal" data-target="#editStVmEnergySave" href="/stVmEnergySave/editStVmEnergySaveDialog" data-must="true" data-set="#pagediv table tbody input[type='checkbox']:checked" select-one="true">修改</button></c:if>
            </div>
        </div>
        <!--Main CommonHead-->
        <form id="search-form" action="${path }/stVmEnergySave/listPage" class="unKeyCode" method="POST">
	        <div class="main_commonhead hl30">
	            <section>
	                <strong>策略名:</strong>
	                <div class="form-group w120 pull-left">
	                    <div class="form-group w120 pull-left"><input name="searchStVmName" class="form-control input-sm" type="text" placeholder="请输入策略名"></div>
	                </div>
	            </section>
	            <section>
	                <strong>售货机型号:</strong>
	                <div class="form-group w120 pull-left">
	                    <select class="form-control input-sm" name="searchVmModelId">
	                        <option value="">请选择</option>
	                        <c:forEach items="${vmModelList }" var="vmModel">
		                      <option value="${vmModel.id }" >${vmModel.modelName }</option>
		                    </c:forEach>
	                    </select>
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
                    <li class="wh5"><input type="checkbox" value=""></li>
                    <li class="wh20">策略名</li>
                    <li class="wh20">策略说明</li>
                    <li class="wh15">售货机型号</li>
                    <li class="wh10">节能策略</li>
                    <li class="wh15">创建信息</li>
                    <li class="wh15">修改信息</li>
                </ul>
            </div>
            <!--theadscroll end-->
            <div id="pagediv">
            	 <jsp:include page="/stVmEnergySave/listPage" flush="true"/>
            </div>
        </div>
    </div>

    <!----
      **  模态框 **
    ---->
    <!-----新建节能策略 模态框 start----->
    <div class="modal fade" id="addenergy" tabindex="-1" role="dialog" aria-labelledby="新增节能策略" aria-hidden="true">
        
    </div>
     <!-----修改节能策略 模态框 start----->
    <div class="modal fade" id="editStVmEnergySave" tabindex="-1" role="dialog" aria-labelledby="修改节能策略" aria-hidden="true">
        
    </div>
    <div class="modal fade" id="editAddEnergySave" tabindex="-1" role="dialog" aria-labelledby="新增节能策略" aria-hidden="true">
        
    </div>
    <div class="modal fade" id="editEditEnergySave" tabindex="-1" role="dialog" aria-labelledby="新增节能策略" aria-hidden="true">
        
    </div>

    <!--下一步新增节能策略  -->
    <div class="modal fade" id="addNextEnergy"  role="dialog" aria-labelledby="新增节能策略" aria-hidden="true">
        <div class="modal-dialog w800">
         <div class="modal-content">
             <div class="modal-header">
                 <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                 <h4 class="modal-title f16">新增节能策略</h4>
             </div>
             <div class="modal-body">
                     <div class="modal_commonbody mt10 clearfix">
                         <section class="col-md-12">
                             <div class="btn-group pull-left" data-toggle="buttons">
                                 <button type="button" class="btn btn-default btn-lg2 addEnergyDialogBtn"  href="/stVmEnergySave/addEnergyDialog" data-toggle="modal" data-target="#energy">添加</button>
                                 <button type="button" class="btn btn-default btn-lg2 btn_tableDelete" data-must="true" data-set="#addEnergySaveDetailDiv table tbody input[type=checkbox]:checked" href="/stVmEnergySave/deleteEnergySave">删除</button>
                                 <button type="button" class="btn btn-default btn-lg2" data-set="#addEnergySaveDetailDiv table tbody input[type='checkbox']:checked" data-must="true" select-one="true" href="/stVmEnergySave/addEditEnergySaveDialog" data-toggle="modal" data-target="#add-editEnergySave">修改</button>
                             </div>
                         </section>
                     </div>
                 <div class="main_commontable mt20">
                     <!--theadscroll start-->
                     <div class="theadscroll">
                         <ul>
                             <li class="wh3"><input type="checkbox" value=""></li>
                             <li class="wh17">节能类型</li>
                             <li class="wh20">开始时间</li>
                             <li class="wh20">结束时间</li>
                             <li class="wh20">保温箱编号</li>
                             <li class="wh20">温度目标值</li>
                         </ul>
                     </div>
                     <!--theadscroll end-->
                     <div id="addEnergySaveDetailDiv">
                     </div>
                 </div>
             </div>
             <div class="modal-footer">
                 <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
             </div>
         </div>
		</div>
    </div>

    <!--添加节能策略  -->
    <div class="modal fade" id="energy" tabindex="-1" role="dialog" aria-labelledby="节能策略" aria-hidden="true"></div>
    <!--修改节能策略详情  -->
    <div class="modal fade" id="add-editEnergySave" tabindex="-1" role="dialog" aria-labelledby="节能策略" aria-hidden="true"></div>

    <!--查看节能策略 -->
    <div class="modal fade" id="checkenergy" tabindex="-1" role="dialog" aria-labelledby="查看节能策略" aria-hidden="true"></div>

</div>
</body>
</html>
<script type="text/javascript">
    $(function(){
    	StVmEnergySave.init();
    })
</script>
