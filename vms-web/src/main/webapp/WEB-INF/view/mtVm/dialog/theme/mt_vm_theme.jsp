<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<!-------------------------------------皮肤设置：#CtrlTheTheme start------------------------------------------------>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">选择皮肤策略</h4>
        </div>
        <div class="modal-body">
            <!---Tab 选择皮肤 与 选择版式 切换 start--->
            <div class="tabheader">
                <ul>
                    <li class="on slt"><span>选择皮肤</span></li>
                    <li class="slt"><span>选择版式</span></li>
                    <li class="slt"><span>入口配置</span></li>
                </ul>
            </div>
            <!--Tab 选择皮肤 与 选择版式 切换 end-->
            <!--选择策略 start-->
            <div class="modal_commonbody clearfix">
                <div class="main_commonhead hl30">
                    <form action="${path}/mtVmTheme/page" id="st_vm_theme_search_form" name="" method="post" class="unKeyCode">
                        <section>
                            <strong>策略名称:</strong>
                            <div class="form-group w120 pull-left"><input class="form-control input-sm" type="text" name="strategyName" placeholder="请输入策略名称"></div>
                        </section>
                        <button type="button" id="st_vm_theme_search_btn" class="btn btn-default f12 btn-lg2 mr10">查询</button>
                    </form>
                </div>
                <form id="mt_vm_theme_form_st" method="post" action="${path}/mtVmTheme/save">
                    <c:forEach items="${ids}" var="vmId" >
                        <input type="hidden" name="ids" value="${vmId}">
                    </c:forEach>
                    <!--table body start-->
                    <div class="main_commontable max400 border_line clearfix" id="st_vm_theme_div">
                        <jsp:include page="/mtVmTheme/page" flush="true"></jsp:include>
                    </div>
                    <!--table body end-->
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>
            </div>
            <!--选择策略 end-->

            <!-- v3.5.0 新增 选择版式 start-->
            <div class="modal_commonbody clearfix" style="display: none;">
                <form id="mt_vm_theme_format_form" method="post" action="${path}/mtVmTheme/saveFormat">
                    <c:forEach items="${ids}" var="vmId" >
                        <input type="hidden" name="ids" value="${vmId}">
                    </c:forEach>
                    <input type="hidden" id="formatCode" name="formatCode" value="3">
                    <!--table body start-->
                    <div class="main_commontable max400 mb15 clearfix" style="text-align: center;" id="themeFormat">
                        <div class="diy_radio">
                            <h4>2页版式</h4><p>商品列表页、商品支付页</p><i class="icon_radio"></i>
                            <input type="hidden" value="2">
                        </div>
                        <div class="diy_radio mat2">
                            <h4>3页版式</h4><p>商品走马灯页、商品列表页、商品支付页</p><i class="icon_radio"></i>
                            <input type="hidden" value="3">
                        </div>
                    </div>
                    <!--table body end-->
                    <!--按钮-->
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>
            </div>
            <!-- v3.5.0 新增 选择版式 end-->


            <!-- v5.0.0.a 入口配置 -->
            <div class="modal_commonbody clearfix" style="display: none;">
                <form method="post" id="theme-form" action="${path}/mtVmTheme/saveConfiguration" enctype="multipart/form-data">
                    <c:forEach items="${ids}" var="vmId" >
                        <input type="hidden" name="ids" value="${vmId}">
                    </c:forEach>
                    <div class="commonTable max400">
                        <table>
                            <thead>
                            <tr>
                                <td width="50">入口</td>
                                <td>内容</td>
                                <td width="160">图片</td>
                                <td width="120">配置</td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>
                                    <span>1</span>
                                </td>
                                <td id="name_1" class="nameSpan">
                                    <c:forEach items="${entrances}" var="entrance">
                                        <c:if test="${1 == entrance.entranceIndex}">
                                            <span>${entrance.resouseName}</span>
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>
                                    <input type="file" class="form-control input-sm w140 fileInput" name="firstFile" placeholder="选择文件" id="firstFile">
                                </td>
                                <td>
                                    <div class="btn-group resourceInput" data-toggle="buttons">
                                        <c:choose>
                                            <c:when test="${empty entrances}">
                                                <input type="hidden" name="firstResourceId" class="resourceId" id="resource_id_1">
                                                <input type="hidden" class="imgUrl" value="">
                                            </c:when>
                                            <c:otherwise>

                                                <%--<c:forEach items="${entrances}" var="entrance" varStatus="sss">
                                                    <c:choose>
                                                        <c:when test="${sss.index == 0}">
                                                            <input type="hidden" name="firstResourceId" class="resourceId" id="resource_id_1" value="${entrance.resouseId}">
                                                            <input type="hidden" class="imgUrl" value="${entrance.imgUrl}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="hidden" name="firstResourceId" class="resourceId" id="resource_id_1">
                                                            <input type="hidden" class="imgUrl" value="">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>--%>

                                                <c:forEach items="${entrances}" var="entrance" varStatus="sss">
                                                    <c:if test="${1 == entrance.entranceIndex}">
                                                        <input type="hidden" name="firstResourceId" class="resourceId" id="resource_id_1" value="${entrance.resouseId}">
                                                        <input type="hidden" class="imgUrl" value="${entrance.imgUrl}">
                                                        <c:set var="firstValue" value="1"></c:set>
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${null == firstValue}">
                                                    <input type="hidden" name="firstResourceId" class="resourceId" id="resource_id_1">
                                                    <input type="hidden" class="imgUrl" value="">
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                        <a class="btn btn-default btn-sm" data-toggle="modal" data-target="#selectLink" href="/mtVmTheme/selectMtVmResourcesDialog?shortcutsCode=1">链接</a>
                                        <a class="btn btn-default btn-sm clearBtn" href="javascript:;">清空</a>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <span>2</span>
                                </td>
                                <td id="name_2" class="nameSpan">
                                    <c:forEach items="${entrances}" var="entrance" >
                                        <c:if test="${2 == entrance.entranceIndex}">
                                            <span>${entrance.resouseName}</span>
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>
                                    <input type="file" class="form-control input-sm w140 fileInput" name="secondFile" placeholder="选择文件" id="secondFile">
                                </td>
                                <td>
                                    <div class="btn-group resourceInput" data-toggle="buttons">

                                        <c:choose>
                                            <c:when test="${empty entrances}">
                                                <input type="hidden" name="secondResourceId" class="resourceId" id="resource_id_2">
                                                <input type="hidden" class="imgUrl" value="">
                                            </c:when>
                                            <c:otherwise>

                                                <%--<c:forEach items="${entrances}" var="entrance" varStatus="sss">
                                                    <c:choose>
                                                        <c:when test="${sss.index == 1}">
                                                            <input type="hidden" name="secondResourceId" class="resourceId" id="resource_id_2" value="${entrance.resouseId}">
                                                            <input type="hidden" class="imgUrl" value="${entrance.imgUrl}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="hidden" name="secondResourceId" class="resourceId" id="resource_id_2">
                                                            <input type="hidden" class="imgUrl" value="">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>--%>
                                                <c:forEach items="${entrances}" var="entrance" varStatus="sss">
                                                    <c:if test="${2 == entrance.entranceIndex}">
                                                        <input type="hidden" name="secondResourceId" class="resourceId" id="resource_id_2" value="${entrance.resouseId}">
                                                        <input type="hidden" class="imgUrl" value="${entrance.imgUrl}">
                                                        <c:set var="secondValue" value="2"></c:set>
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${null == secondValue}">
                                                    <input type="hidden" name="secondResourceId" class="resourceId" id="resource_id_2">
                                                    <input type="hidden" class="imgUrl" value="">
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                        <a class="btn btn-default btn-sm" data-toggle="modal" data-target="#selectLink" href="/mtVmTheme/selectMtVmResourcesDialog?shortcutsCode=2">链接</a>
                                        <a class="btn btn-default btn-sm clearBtn" href="javascript:;">清空</a>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <span>3</span>
                                </td>
                                <td id="name_3" class="nameSpan">
                                    <c:forEach items="${entrances}" var="entrance">
                                        <c:if test="${3 == entrance.entranceIndex}">
                                            <span>${entrance.resouseName}</span>
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>
                                    <input type="file" class="form-control input-sm w140 fileInput" name="thirdFile" placeholder="选择文件" id="thirdFile">
                                </td>
                                <td>
                                    <div class="btn-group resourceInput" data-toggle="buttons">
                                        <c:choose>
                                            <c:when test="${empty entrances}">
                                                <input type="hidden" name="thirdResourceId" class="resourceId" id="resource_id_3">
                                                <input type="hidden" class="imgUrl" value="">
                                            </c:when>
                                            <c:otherwise>
                                                <%--<c:forEach items="${entrances}" var="entrance" varStatus="sss">
                                                    <c:choose>
                                                        <c:when test="${sss.index == 2}">
                                                            <input type="hidden" name="thirdResourceId" class="resourceId" id="resource_id_3" value="${entrance.resouseId}">
                                                            <input type="hidden" class="imgUrl" value="${entrance.imgUrl}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="hidden" name="thirdResourceId" class="resourceId" id="resource_id_3">
                                                            <input type="hidden" class="imgUrl" value="">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>--%>
                                                <c:forEach items="${entrances}" var="entrance" varStatus="sss">
                                                    <c:if test="${3 == entrance.entranceIndex}">
                                                        <input type="hidden" name="thirdResourceId" class="resourceId" id="resource_id_3" value="${entrance.resouseId}">
                                                        <input type="hidden" class="imgUrl" value="${entrance.imgUrl}">
                                                        <c:set var="thirdValue" value="3"></c:set>
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${null == thirdValue}">
                                                    <input type="hidden" name="thirdResourceId" class="resourceId" id="resource_id_3">
                                                    <input type="hidden" class="imgUrl" value="">
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                        <a class="btn btn-default btn-sm" data-toggle="modal" data-target="#selectLink" href="/mtVmTheme/selectMtVmResourcesDialog?shortcutsCode=3">链接</a>
                                        <a class="btn btn-default btn-sm clearBtn" href="javascript:;">清空</a>
                                    </div>
                                </td>
                            </tr>
                            </tbody>


                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success btn-lg2">确定</button>
                        <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                    </div>
                </form>
            </div>
            <!-- / v5.0.0.a 入口配置 -->



        </div>
    </div>
</div>
<!-------------------------------------皮肤设置：#CtrlTheTheme end ------------------------------------------------->

<div class="modal fade" id="selectLink" tabindex="-1" role="dialog" aria-labelledby="选择跳转页" aria-hidden="true">

</div>

<script>
    $(function(){
        MtVmTheme.init();
        $('#theme-form .clearBtn').click(function(){
            $(this).parents('tr').find('.resourceInput input[type="hidden"]').val('');
            $(this).parents('tr').find('.fileInput').val('');
            $(this).parents('tr').find('.nameSpan').empty();
        });
    });
</script>