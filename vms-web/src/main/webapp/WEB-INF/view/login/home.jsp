<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'home.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

</head>

<body>
	<table>
		<tr>
			<td>补货工单</td>
			<td>deliveryTask/list</td>
		</tr>
		<tr>
			<td>投放工单</td>
			<td>deployTask/list</td>
		</tr>
		<tr>
			<td>场景</td>
			<td>businessDistrict/home</td>
		</tr>
		<tr>
			<td>公司</td>
			<td>company/home</td>
		</tr>
		<tr>
			<td>会员</td>
			<td>customer/home</td>
		</tr>
		<tr>
			<td>点位</td>
			<td>mtNode</td>
		</tr>
		<tr>
			<td>售货机管理</td>
			<td>mtVm/list</td>
		</tr>
		<tr>
			<td>售货机型号</td>
			<td>vmModel/home</td>
		</tr>
		<tr>
			<td>仓库</td>
			<td>warehouse/home</td>
		</tr>
		<tr>
			<td>流水</td>
			<td>order/list</td>
		</tr>
		<tr>
			<td>维护工单</td>
			<td>repairTask/list</td>
		</tr>
		<tr>
			<td>工单查询</td>
			<td>searchTask/list</td>
		</tr>
		<tr>
			<td>闪购键策略</td>
			<td>stShortcuts/list</td>
		</tr>
		<tr>
			<td>sim卡</td>
			<td>sim/list</td>
		</tr>
		<tr>
			<td>商品信息</td>
			<td>sku/home</td>
		</tr>
		<tr>
			<td>商品类型</td>
			<td>skuType/home</td>
		</tr>
		<tr>
			<td>商品促销策略</td>
			<td>stSkuEvent/list</td>
		</tr>
		<tr>
			<td>商品价格策略</td>
			<td>stSkuPrice/list</td>
		</tr>
		<tr>
			<td>售货机货道策略</td>
			<td>stVmChannel/list</td>
		</tr>
		<tr>
			<td>节能策略</td>
			<td>stVmEnergySave/list</td>
		</tr>
		<tr>
			<td>整机促销</td>
			<td>stVmEvent/list</td>
		</tr>
		<tr>
			<td>应用促销策略</td>
			<td>mtVmEvent/list</td>
		</tr>
		<tr>
			<td>走马灯策略</td>
			<td>stVmText/list</td>
		</tr>
		<tr>
			<td>角色</td>
			<td>role/list</td>
		</tr>
		<tr>
			<td>用户</td>
			<td>user/list</td>
		</tr>
		<tr>
			<td>售货机状态</td>
			<td>vmStatus/list</td>
		</tr>
		<tr>
			<td>撤机工单</td>
			<td>withdrawTask/list</td>
		</tr>
		<tr>
			<td>合伙人</td>
			<td>partner/home</td>
		</tr>
		<tr>
			<td>系统日志</td>
			<td>sysLog/home</td>
		</tr>
		<tr>
			<td>字典</td>
			<td>sysType/home</td>
		</tr>
		<tr>
			<td>账号设置</td>
			<td>account/home</td>
		</tr>
	</table>
</body>
</html>
