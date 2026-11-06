<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal-dialog w600">
    <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title f16" id="">手动调量</h4>
            </div>
            <div class="modal-body">
                <div class="main_commontable max400 border_line">
                    <table style="text-align: center">
                        <thead>
                            <tr>
                                <td class="wh20">图片</td>
                                <td class="wh35">商品</td>
                                <td class="wh15">货道</td>
                                <td class="wh15">满量</td>
                                <td class="wh15">余量</td>
                                <td class="wh15">清货量</td>
                                <td class="wh15">补货量</td>
                            </tr>
                        </thead>

                        <tbody class="auto">
                        <c:if test="${! empty  taskChannelModels}">
                            <c:forEach items="${taskChannelModels }" var="tMtChannel" varStatus="task">
                                <tr <c:if test="${true == tMtChannel.flag}">style="display: none" </c:if>>
                                    <td class="wh20">
                                        <span><img class="table_img" src="${tMtChannel.imgUrl }"></span>
                                        <input type="hidden" name="channelId" value="${tMtChannel.channelId}">
                                        <input type="hidden" name="skuId" value="${tMtChannel.skuId}">
                                        <input type="hidden" name="flag" value="${tMtChannel.flag}">
                                    </td>
                                    <td class="wh35">${tMtChannel.skuName }</td>
                                    <td class="wh15">${tMtChannel.channelCode }</td>
                                    <td class="wh15">${tMtChannel.maxNum}<input type="hidden" name="maxNum" value="${tMtChannel.maxNum}"></td>
                                    <td class="wh15">${tMtChannel.currNum }<input type="hidden" name="currNum" value="${tMtChannel.currNum}"></td>
                                    <td class="wh15">
                                        <input type="text" class="realClearVal" style="width: 50px; text-align: center; font-size: 14px; line-height: 24px;" value="${empty tMtChannel.realClear ? 0 : tMtChannel.realClear}" name="realClear">
                                    </td>
                                    <td class="wh15">
                                        <input type="text" class="realCountVal" style="width: 50px; text-align: center; font-size: 14px; line-height: 24px;" value="${empty tMtChannel.realCount ? 0 : tMtChannel.realCount}" name="realCount">
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:if>
                        <c:if test="${ empty  taskChannelModels}">
                            <tr>
                                <td>
                                    <span>${message}</span>
                                </td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
    </div>
</div>
<script type="text/javascript">
    $(function(){
        DeliveryTask.adJustModel();
    })
</script>