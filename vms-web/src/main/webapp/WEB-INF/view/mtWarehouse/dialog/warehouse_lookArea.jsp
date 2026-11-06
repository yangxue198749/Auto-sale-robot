<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">查看关联区域</h4>
        </div>
        <div class="modal-body">
            <div class="main_commontable max400 border_line">
                <table>
                     <thead>
                         <tr>
                			<td class="wh30">省份</td>
                            <td class="wh35">城市</td>
                            <td class="wh35">区域</td>
                         </tr>
                     </thead>
                     <tbody class="auto">
                     	<c:forEach items="${list }" var="area">
                     		 <tr>
	                			<td class="wh30">${area.province }</td>
	                            <td class="wh35">${area.city}</td>
	                            <td class="wh35">${area.area }</td>
	                         </tr>
                     	</c:forEach>
                     </tbody>
                </table>
            </div>
        </div>
        <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2" data-dismiss="modal">确定</button>
        </div>
        
    </div>
</div>
