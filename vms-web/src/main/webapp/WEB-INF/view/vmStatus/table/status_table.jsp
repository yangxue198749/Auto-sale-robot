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
                  <tr>
                      <td class="wh5">
                          <input type="checkbox" value="${vm.id}">
                      </td>
                      <td class="wh8">
                          <span>${vm.innerCode}</span>
                      </td>
                      <td class="wh8">
                          <span>${vm.nodeName}</span>
                      </td>
                      <td class="wh8">
                          <a href="#" data-toggle="tooltip" data-placement="bottom" data-placement="left" data-html="true"
                             title="<c:forEach items="${vm.vmVersion}" var="vmVersion">
                                    ${u:getNameByCode("vmVersion",vmVersion.deviceType,"")}:${vmVersion.deviceVersion}<br>
                                    </c:forEach>">
                              <span>
                                  <c:forEach items="${vm.vmVersion}" var="vmVersion">
                                      <c:if test="${ 1== vmVersion.deviceType}">
                                          ${u:getNameByCode("vmVersion",vmVersion.deviceType,"")}:${vmVersion.deviceVersion}
                                      </c:if>
                                  </c:forEach>
                              </span><i class="iconfont f14 ml5 icon-tishi cf_blue"></i></a>
                      </td>
                      <td class="wh5">
                          <span>${vm.doorStatus}</span>
                      </td>
                      <td class="wh6">
                          <span>${vm.targetTemp}</span>
                      </td>
                      <td class="wh6">
                          <span>${vm.nowTemp}</span>
                      </td>
                      <td class="wh6">
                          <c:choose>
                              <c:when test="${vm.tempStatusNew eq '1' }"><span class="icon_state">正常</span></c:when>
                              <c:when test="${vm.tempStatusNew eq '2' }"><span class="icon_state red">异常</span></c:when>
                              <c:when test="${vm.tempStatusNew eq '3' }"><span class="icon_state blue">未知</span></c:when>
                          </c:choose>
                      </td>
                      <td class="wh6">
                        ${u:getVmStatusStr(vm.id)}
                      </td>
                      <td class="wh10">
                          <span>${u:getVmStatusDetailStr(vm.id)}</span>
                      </td>
                      <td class="wh8">
                          <span>${vm.netStatus}</span>
                      </td>
                      <td class="wh8">
                          <span>${vm.taskStatus}</span>
                      </td>
                      <td class="wh8">
                          <c:choose>
                              <c:when test="${vm.vmStatus == 0 }"><span class="icon_state gray">未投放</span></c:when>
                              <c:when test="${vm.vmStatus == 1 }"><span class="icon_state">投放中</span></c:when>
                              <c:when test="${vm.vmStatus == 2 }"><span class="icon_state">在售</span></c:when>
                              <c:when test="${vm.vmStatus == 3 }"><span class="icon_state red">停售</span></c:when>
                              <c:when test="${vm.vmStatus == 4 }"><span class="icon_state blue">撤回中</span></c:when>
                          </c:choose>
                      </td>
                      <td class="wh8">
                            <span>${vm.lastTime}</span>
                      </td>
                  </tr>
				</c:forEach>
        </tbody>
    </table>
	</c:otherwise>
</c:choose>
<c:if test="${! empty vmList}">
	<t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>
<script type="text/javascript">
$(function(){
	 $("[data-toggle='tooltip']").tooltip();
});
</script>
