<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">其他信息</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable border_line">
                <table>
                    <tbody>
                    <tr>
                        <td class="wh20">公司缩写:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.companyInit)}</span></td>
                        <td class="wh20">售货机外观:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.machineView)}</span></td>
                    </tr>
                    <tr>
                        <c:if test="${vmModel.screenHave == 1}">
                            <td class="wh20">屏尺寸:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.screenSize)}</span></td>
                            <td class="wh20">屏种类:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.screenType)}</span></td>
                        </c:if>
                    </tr>
                    <tr>
                        <td class="wh20">云台有无:</td>
                        <td class="wh30" style="background:#edf7ed;"><span>${vmModel.elevatorPlatformYn == 0 ? '有':'无'}</span>
                        </td>
                        <td class="wh20">出货结构:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.vendOutArchitecture)}</span></td>
                    </tr>
                    <tr>
                        <td class="wh20">非接卡:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.rfidCard)}</span></td>
                        <td class="wh20">出货口:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.vendOutType)}</span></td>
                    </tr>
                    <%-- v3.2 新增 保温箱数量 温控说明 --%>
                    <tr>
                        <td class="wh20">保温箱数量:</td><td class="wh30" style="background:#edf7ed;"><span>${vmModel.tempBoxCount}</span></td>
                        <td class="wh20">温控说明:</td><td class="wh30" style="background:#edf7ed;"><span>${vmModel.tempCtrlDesc}</span></td>
                    </tr>
                    <tr>
                        <td class="wh20">温控模式:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.temperatureHandlerMode)}</span></td>
                        <td class="wh20">屏有无:</td>
                        <td class="wh30" style="background:#edf7ed;"><span>${vmModel.screenHave == 1 ? '有':'无'}</span>
                        </td>
                    </tr>
                    <%-- v3.2 隐藏 温控模式 制冷有无 制冷设备 加热有无 加热设备 云台设备 start
                    <tr>
                        <td class="wh20">温控模式:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.temperatureHandlerMode)}</span></td>
                        <td class="wh20">制冷有无:</td><td class="wh30" style="background:#edf7ed;"><c:choose>
                        <c:when test="${vmModel.coolingYn == 0}">
                            <span>有</span>
                        </c:when>
                        <c:otherwise>
                            <span>无</span>
                        </c:otherwise>
                    </c:choose></td>
                        <td class="wh20">制冷设备:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.colingHdid)}</span></td>
                    </tr>
                    <tr>
                        <td class="wh20">加热有无:</td><td class="wh30" style="background:#edf7ed;"><c:choose>
                        <c:when test="${vmModel.hittingYn == 0}">
                            <span>有</span>
                        </c:when>
                        <c:otherwise>
                            <span>无</span>
                        </c:otherwise>
                    </c:choose></td>
                        <td class="wh20">加热设备:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.hittingHdid)}</span></td>
                        <td class="wh20">云台设备:</td><td class="wh30" style="background:#edf7ed;"><span>${u:getDictNameById(vmModel.elevatorPlatformHdid)}</span></td>
                    </tr>
                     v3.2 隐藏 温控模式 制冷有无 制冷设备 加热有无 加热设备 云台设备 end --%>
                    </tbody>
                </table>
            </div>

        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-success" data-dismiss="modal">确定</button>
        </div>
    </div>
</div>