<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form action="">
    <div class="modal-dialog w800">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16">修改货道</h4>
            </div>
            <div class="modal-body">
                <div class="clearfix">
                	<c:forEach var="i" begin="0" end="${tMtVmModel.channelLayerQty - 1 }">
	                    <section class="col-md-12">
	                        <table class="wh100 addModel_table bb0">
	                            <tr>
	                                <td class="wh10">
	                                    <button onclick="getModelChannelRowPage(this)" name="${tMtVmModel.id},${i + 1}" type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#modelTableRow">配置</button>
	                                </td>
	                            	<c:forEach var="j" begin="0" end="${list[i].number - 1 }">
		                                <td class="wh${list[i].size}">
		                                    <span>${i + 1}-${j + 1}</span>
		                                    <button onclick="getModelChannelPage(this)" name="${tMtVmModel.id},${i + 1}-${j + 1}" type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#modelTable">配置</button>
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
                <button onclick="searchEvent()" type="button" class="btn btn-success btn-lg2" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</form>