package com.kdx.vms.service;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TStVmText;
import com.kdx.vms.model.bean.TSysUser;

public interface IStVmTextService {

	/**
	 * 查询走马灯信息
	 * @param context
	 * @param user 登录用户
	 * @param stVmName 策略名称
	 * @param text 走马灯文案
	 * @param pageSize 每页数量
	 * @return
	 */
	List<TStVmStrategy> findStVmTextPage(ObjectContext context,TSysUser user,String stVmName,String text,Integer pageSize);
	
	/**
	 * 获取走马灯详情
	 * @param context
	 * @param strategyId 策略主键
	 * @return
	 */
	TStVmText findByStrategyId(ObjectContext context,Integer strategyId);
	
	/**
	 * 新增走马灯策略
	 * @param context
	 * @param stVmName 策略名称
	 * @param strategyDesc 策略描述
	 * @param text 走马灯内容
	 * @param user 操作人信息
	 * @return
	 */
	String addStVmText(ObjectContext context,String stVmName,String strategyDesc,String text,TSysUser user);
	
	/**
	 * 修改走马灯策略
	 * @param context
	 * @param stVmName
	 * @param strategyDesc
	 * @param text
	 * @param user
	 * @param strategyId
	 * @return
	 */
	String editStVmText(ObjectContext context,String stVmName,String strategyDesc,String text,TSysUser user,Integer strategyId);

	/**
	 * 删除走马灯策略
	 * @param objectContext
	 * @param sysUser
	 * @param ids
	 * @return
	 */
	String deleteByStrategyIds(ObjectContext objectContext, TSysUser sysUser,
			List<Integer> ids);
}
