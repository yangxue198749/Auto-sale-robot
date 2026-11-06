<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w800">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">查看货道</h4>
        </div>
        <div class="modal-body">
            <div class="clearfix">
            	<c:forEach var="i" begin="0" end="${tMtVmModel.channelLayerQty - 1 }">
	                <section class="col-md-12">
	                    <table class="wh100 addModel_table bb0">
	                        <tr>
	                            <td class="wh10">
	                                <div>
	                                    <span>深:</span>
	                                    <span>${list[i].childList[0].channelLength}</span>
	                                </div>
	                                <div>
	                                    <span>高:</span>
	                                    <span>${list[i].childList[0].channelHeight}</span>
	                                </div>
	                                <div>
	                                	<c:choose>
	                                		<c:when test="${list[i].childList[0].displayMode == 0}">
	                                			<span>竖</span>
	                                		</c:when>
	                                		<c:when test="${list[i].childList[0].displayMode == 1}">
	                                			<span>横</span>
	                                		</c:when>
	                                	</c:choose>
	                                </div>
	                            </td>
	                            <c:forEach var="j" begin="0" end="${list[i].number - 1 }">
		                            <td class="wh${list[i].size}">
		                                <div>
		                                    <span>${list[i].childList[j].channelCode }</span>
		                                </div>
		                                <div>
		                                    <span>${list[i].childList[j].channelWidth }</span>
		                                    <span>,</span>
		                                    <span>${list[i].childList[j].acceptWidthMax }</span>
		                                </div>
		                                <div>
		                                    <c:choose>
		                                    	<c:when test="${list[i].childList[j].isFixedCapacity == 1 }">
		                                    		<span>是</span>
		                                    		<span>,</span>
		                                    		<span>${list[i].childList[j].fixedCapacity}</span>
		                                    	</c:when>
		                                    	<c:when test="${list[i].childList[j].isFixedCapacity == 0 }">
		                                    		<span>否</span>
		                                    	</c:when>
		                                    </c:choose>
		                                </div>
		                            </td>
		                    	</c:forEach>
	                        </tr>
	                    </table>
	                </section>
                </c:forEach>
            </div>
        </div>

        <!--按钮-->
        <div class="modal-footer">
            <button type="submit" class="btn btn-success btn-lg2 confirmbtn" data-dismiss="modal">确定</button>
            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
        </div>
    </div>
</div>
