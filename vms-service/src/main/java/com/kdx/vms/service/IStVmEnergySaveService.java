package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmEnergySave;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;

public interface IStVmEnergySaveService {

	/**
	 * 查询节能策略
	 * @param context
	 * @param user 登录用户
	 * @param stVmName 策略名称
	 * @param modelId 售货机型号
	 * @param pageSize 每页数量
	 * @return
	 */
	List<TStVmStrategy> findPage(ObjectContext context,TSysUser user,String stVmName,Integer modelId,Integer pageSize);
	
	/**
	 * 根据策略主键查询节能策略信息
	 * @param context
	 * @param strategyId 策略主键
	 * @return
	 */
	List<TStVmEnergySave> findByStrategyId(ObjectContext context,Integer strategyId);
	
	/**
	 * 根据策略主键删除节能策略
	 * @param context
	 * @param strategyIds 策略主键
	 * @param user 操作人
	 * @return
	 */
	String deleteStVmEnergySaveByStrategyId(ObjectContext context ,Integer[] strategyIds ,TSysUser user);
	
	/**
	 * 
	 * 根据节能明细主键删除节能策略明细
	 * @param context
	 * @param energySaveId 节能明细主键
	 * @param user 操作人
	 * @return
	 */
	String deleteStVmEnergyByEnergyId(ObjectContext context ,Integer energySaveId ,TSysUser user);
	
	/**
	 * 新建售货机策略
	 * @param context
	 * @param stVmName
	 * @param stVmDesc
	 * @param vmModelId
	 * @param user
	 * @return
	 */
	Map<String,Object> addStVmStrategy(ObjectContext context ,String stVmName ,String stVmDesc,Integer vmModelId,TSysUser user);

	Map<String, Object> addStVmEnergySave(ObjectContext objectContext,
			TSysUser sysUser, Integer strategyId, Integer addTargetValue,
			Integer addEnergySaveType, String addStartTime, String addEndTime,
			String addRemark,Integer addTempBoxNo);
	
	String makeTableString(ObjectContext context,Integer strategyId);

	/**
	 * 删除节能详情
	 * @param objectContext
	 * @param ids
	 * @param sysUser
	 * @return
	 */
	String deleteEnergySave(ObjectContext objectContext, Integer[] ids,
			TSysUser sysUser);

	TStVmEnergySave findByEnergySaveId(ObjectContext objectContext, Integer ids);

	Map<String, Object> editEnergySave(ObjectContext objectContext,
			TSysUser sysUser, Integer energySaveId, Integer addTargetValue,
			Integer addEnergySaveType, String addStartTime, String addEndTime,
			String addRemark,Integer addTempBoxNo);
}
