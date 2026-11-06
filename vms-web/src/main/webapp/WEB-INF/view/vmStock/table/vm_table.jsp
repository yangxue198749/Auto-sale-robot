<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty vmList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
	</c:when>
	<c:otherwise>
    <table>
        <tbody class="auto">
        <c:forEach items="${vmList}" var="vm">
          <tr vmId="${vm.id}">
              <%--<td width="30"><input type="checkbox" value="${vm.id}"></td>--%>
              <td class="w20">
                  <span>${vm.innerCode}</span>
              </td>
              <td class="w20">
                  <span>${vm.nodeName}</span>
              </td>
              <td class="w15">
                  <span>${vm.countProportion}</span>
              </td>
              <td class="w45">
                  <span>${vm.stockoutSku}</span>
              </td>
          </tr>
        </c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<div class="clearfix"></div>
<c:if test="${! empty vmList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="false"/>
</c:if>
<script type="text/javascript">
    $(function () {
        VmStock.stockTable();
        //clear: stock-pagediv
        $('#pagediv .pagination>li>a').click(function(){
            $('#stock-pagediv').empty();
        })
    })
</script>
