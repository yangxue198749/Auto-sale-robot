<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h4 class="modal-title f16" id="">详情</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable max400 border_line">
                <table style="text-align: center;">
                    <thead>
                        <tr>
                            <td class="wh20">图片</td>
                            <td class="wh35">商品</td>
                            <td class="wh15">货道</td>
                            <td class="wh15">补货前余量</td>
                            <td class="wh15">清货量</td>
                            <td class="wh15">应补量</td>
                            <td class="wh15">实补量</td>
                            <td class="wh15">补货后余量</td>
                        </tr>
                    </thead>
                    <tbody class="auto">
                    <c:if test="${! empty  taskChannelList}">
	                    <c:forEach items="${taskChannelList }" var="taskChannel">
	                        <tr>
	                            <td class="wh20">
	                                <span><img class="table_img" src="${taskChannel.sku.imgUrl }"></span>
	                            </td>
	                            <td class="wh35">${taskChannel.sku.skuName }</td>
	                            <td class="wh15">${taskChannel.channel.channelCode }</td>
	                            <td class="wh15">${taskChannel.beforeDeliveryStock }</td>
	                            <td class="wh15">${taskChannel.clear}</td>
	                            <td class="wh15">${empty taskChannel.skuCount ? 0 :taskChannel.skuCount}</td>
	                            <td class="wh15">${taskChannel.realCount }</td>
	                            <td class="wh15">${taskChannel.afterDeliveryStock }</td>
	                        </tr>
	                    </c:forEach>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2 confirmbtn" data-dismiss="modal">关闭</button>
        </div>
    </div>
</div>