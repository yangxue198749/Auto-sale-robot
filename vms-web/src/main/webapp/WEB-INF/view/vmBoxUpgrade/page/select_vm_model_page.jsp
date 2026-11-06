<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty vmModelList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
    </c:when>
    <c:otherwise>
    <table>
        <thead>
            <tr>
                <td width="40px"></td>
                <td>售货机型号</td>
                <td>型号描述</td>
                <td>货道类型</td>
                <td>货道层数</td>
                <td>各层货道数</td>
            </tr>
        </thead>
        <tbody class="auto" id="chooseMatDiv">
        <c:forEach items="${vmModelList}" var="mtVmModel">
            <tr>
                <td><input type="radio" name="vmModel" value="${mtVmModel.id}" modelName="${mtVmModel.modelName}"></td>
                <td>${mtVmModel.modelName}</td>
                <td>${mtVmModel.modelDesc}</td>
                <td>${u:getDictNameById(mtVmModel.channelType)}</td>
                <td>${mtVmModel.channelLayerQty}</td>
                <td>${mtVmModel.channelPerLayer}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty vmModelList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" id="selectVmModelDiv" setPage="false" />
</c:if>