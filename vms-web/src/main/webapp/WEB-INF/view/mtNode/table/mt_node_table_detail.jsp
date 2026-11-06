<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">详细信息</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable border_line">
                <table>
                    <tbody>
                    <tr>
                        <td class="wh20">经度:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.longitude}</span></td>
                        <td class="wh20">纬度:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.latitude}</span></td>
                    </tr>
                    <tr>
                        <td class="wh20">经营时间:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.optTime}</span></td>
                        <td class="wh20">可投放时间:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.putTime}</span></td>
                    </tr>
                    <tr>
                        <td class="wh20">物业名称:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.propertyName}</span></td>
                        <td class="wh20">物业负责人:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.propertyManager}</span></td>
                    </tr>
                    <tr>
                        <td class="wh20">物业负责人电话:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.propertyPhone}</span></td>
                        <td class="wh20">物业负责人职位:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.propertyLevel}</span></td>
                    </tr>
                    <%--
                    <!-- 以下两个为3.5之前需求，目前版本v3.5已注释 by xinfei 20170705 17:48 -->
                    <tr>
                        <td class="wh20">结束经营时间:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.optEndTime}</span></td>
                        <td class="wh20">投放方式:</td><td class="wh30" style="background:#edf7ed;"><span>${mtNode.putMethod}</span></td>
                    </tr>--%>
                    </tbody>
                </table>
            </div>

            <%--
            <!-- v3.4以前的界面样式，v3.5已更新为上方展示结构 by xinfei 20170705 17:48 -->
            <div class="main_commonbody clearfix">
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">经度:</span>
                    <span>${mtNode.longitude}</span>
                </section>
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">纬度:</span>
                    <span>${mtNode.latitude}</span>
                </section>
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">经营时间:</span>
                    <span>${mtNode.optTime}</span>
                </section>
                &lt;%&ndash;<section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">结束经营时间:</span>
                    <span>${mtNode.optEndTime}</span>
                </section>&ndash;%&gt;
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">可投放时间:</span>
                    <span>${mtNode.putTime}</span>
                </section>
                &lt;%&ndash;<section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">投放方式:</span>
                    <span>${mtNode.putMethod}</span>
                </section>&ndash;%&gt;
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">物业名称:</span>
                    <span>${mtNode.propertyName}</span>
                </section>
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">物业负责人:</span>
                    <span>${mtNode.propertyManager}</span>
                </section>
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">物业负责人电话:</span>
                    <span>${mtNode.propertyPhone}</span>
                </section>
                <section class="col-md-6 mt20">
                    <span class="pull-left w100 text-right pr5">物业负责人职位:</span>
                    <span>${mtNode.propertyLevel}</span>
                </section>

            </div>--%>

        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            <button type="button" class="btn btn-success" data-dismiss="modal">确定</button>
        </div>
    </div>
</div>