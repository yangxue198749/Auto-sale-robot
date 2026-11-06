<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
	<c:when test="${empty nodeList }">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
	</c:when>
	<c:otherwise>
    <table class="sysTypeTable">
        <tbody class="auto" >
				<c:forEach items="${nodeList }" var="sysType">
					<tr>
                        <td class="wh5">
                            <input type="checkbox" value="${sysType.id}">
                        </td>
                        <td class="wh25">
                            <span>${sysType.typeCode}</span>
                        </td>
                        <td class="wh20">
                            <span>${sysType.typeName}</span>
                        </td>
                        <td class="wh50">
                            <span>${sysType.typeDesc}</span>
                        </td>
                    </tr>
				</c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty  nodeList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }"
		pageNo="${pageNo }" url="${path }/${url }" setPage="true" id="pagediv1"/>
</c:if>

<script type="text/javascript">
    $(function(){
	  //initSysType();
    })
</script>
