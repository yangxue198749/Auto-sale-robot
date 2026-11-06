<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<c:choose>
    <c:when test="${empty upgradePackageList}">
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
        <c:forEach items="${upgradePackageList}" var="upgradePackage">
            <tr>
                <td class="wh20">
                    <span>${upgradePackage.typeHtml}</span>
				</td>
				<td class="wh15">
					<span>${upgradePackage.packageVersion}</span>
				</td>
				<td class="wh10">
					<span>${upgradePackage.downloadUrlHtml}</span>
				</td>
				<td class="wh10">
                    ${upgradePackage.statusHtml}
				</td>
				<td class="wh15">
                    <p>
						<span>${u:getUserById(upgradePackage.ctor).userName}</span>
					</p>
					<p>
						<span><fmt:formatDate value="${upgradePackage.ctime}" type="both"/></span>
					</p>
				</td>
				<td class="wh15">
                    <p>
						<span>${u:getUserById(upgradePackage.utor).userName}</span>
					</p>
					<p>
						<span><fmt:formatDate value="${upgradePackage.utime}" type="both"/></span>
					</p>
				</td>
				<td class="wh15">
                    <c:if test="${u:isPermitted('upgradePackage-disable')}">
					    <button type="button" class="btn btn-default btn-sm DisableBtn" packageId="${upgradePackage.id}">${upgradePackage.btnName}</button>
                    </c:if>
				</td>
			</tr>
        </c:forEach>
		</tbody>
	</table>
    </c:otherwise>
</c:choose>
<c:if test="${! empty upgradePackageList}">
    <t:pager totalSize="${totalSize }" pageSize="${pageSize }" pageNo="${pageNo }" url="${url }" setPage="true"/>
</c:if>


<script type="text/javascript">
    //禁用操作 DisableBtn 询问操作
    $('.DisableBtn').on('click',function(){
    	var thisBtn = $(this);
    	layer.confirm('是否确认操作？', {
    	  title: '提示',
    	  btn: ['确认','取消']
    	}, function(){
    		//确认操作
            var options = {
                type: 'POST',
                url: "/upgradePackage/disable",
                data: {"packageId":thisBtn.attr("packageId")},
                beforeSubmit: function() {
                    // Metronic.blockUI({target: "#pagediv",boxed: true});
                },
                success: function(data) {
                    if(data.code == "0"){
                        layer.msg("操作成功！",{shade: [0.5, '#fff'],time:2000,});
                        // $("#DisableBtn").html(data.type)
                        UpgradePackage.searchForm();
                    } else {
                        layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
                    }
                }
            };
            $("#search-form").ajaxSubmit(options);
    	}, function(){
    		//取消操作 关闭询问框
    	});
    });
</script>
