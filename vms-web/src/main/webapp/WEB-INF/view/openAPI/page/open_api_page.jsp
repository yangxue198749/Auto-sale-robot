<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty serviceList}">
        <div>
            <section style="padding:100px 0; text-align:center;">
                <p><i class="iconfont f48 mb10 icon-lvyoutehui" style="color:#ccc;"></i></p>
                <p>暂无内容</p>
            </section>
        </div>
    </c:when>
    <c:otherwise>
        <table>
            <tbody class="auto" id="openApiPageTable">
            <c:forEach items="${serviceList }" var="service">
                <tr>
                    <td>
                        <p><span>${service.companyName}</span></p>
                    </td>
                    <td>
                         <c:forEach items="${service.serviceName}" var="name">
                             <p><span>${name}</span></p>
                         </c:forEach>
                    </td>
                    <td>
                        <p><span>${service.customerId}</span></p>
                    </td>
                    <td>
                        <p><span>${service.key}</span></p>
                    </td>
                    <td width="140">
                        <div class="btn-group" data-toggle="buttons">
                            <button type="button" class="btn btn-default btn-sm editBtn" href="#" data-company-name="${service.companyName}" data-customer-id="${service.customerId}" data-service-id="${service.serviceId}" data-toggle="modal" data-target="#editOpenInterface">修改</button>
                            <button type="button" class="btn btn-default btn-sm deleteOpenInterface" href="/openApi/deleteOpenApiDialog?customerId=${service.customerId}" data-toggle="modal" data-target="#deleteOpenInterface">删除</button>
                        </div>
                    </td>
                    <script>
                        $(function () {
                            $('#openApiPageTable .editBtn').each(function () {
                                var companyName = $(this).attr('data-company-name');
                                //转义中文 - 公司名称
                                var enCodeCompanyName = encodeURIComponent(companyName);
                                enCodeCompanyName = encodeURIComponent(enCodeCompanyName);

                                var serviceId = $(this).attr('data-service-id');
                                var customerId =$(this).attr('data-customer-id');
                                var _thisHrefParams = '/openApi/updateOpenApiDialog?companyName='+enCodeCompanyName+'&serviceId='+serviceId+'&customerId='+customerId;
                                console.log('_thisHrefParams:'+_thisHrefParams);
                                $(this).attr('href',_thisHrefParams);
                            })
                        })
                    </script>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty  serviceList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url}" setPage="true" />
</c:if>