package com.kdx.vms.service;

import java.util.List;

import com.kdx.vms.model.bean.*;

import org.apache.cayenne.ObjectContext;

/**
 *
 * Created by liyang on 16-12-6.
 */
public interface ITaskVmService {
	
	/**
	 * 查询工单
	 * @param context
	 * @param user 用户
	 * @param searchField 搜索字段 1:售货机编号 ;2:点位名称 ; 3:负责人  4: 硬件编号（撤机）
	 * @param searchFieldValue 搜索值
	 * @param taskVmStatus 工单状态
	 * @param startTime 执行时间
	 * @param endTime 执行时间
	 * @param taskType 工单类型
	 * @param pageSize 每页数量
	 * @return
	 */
	List<TTaskVm> findPage(ObjectContext context,TSysUser user,Integer searchField,String searchFieldValue,Integer taskVmStatus,String startTime,String endTime,Integer taskType,Integer pageSize);

	/**
	 * 取消工单
	 * @param objectContext
	 * @param sysUser
	 * @param ids
	 * @return
	 */
	String cancelTask(ObjectContext objectContext, TSysUser sysUser,
			Integer[] ids);

	/**
	 * 获取售货机列表
	 * @param objectContext
	 * @param sysUser
	 * @param searchNodeName 点位名字
	 * @param searchInnerCode 售货机编码
	 * @param pageSize
	 * @param vmStatus 售货机状态 
	 * @return
	 */
	List mtVmPage(ObjectContext objectContext, TSysUser sysUser,
			String searchNodeName, String searchInnerCode, Integer pageSize,Integer vmStatus);

	TTaskVm findByPk(ObjectContext objectContext, Integer taskVmId);

	String editTaskVm(ObjectContext objectContext, TSysUser sysUser,
			Integer taskVmId, Integer editOwnerId,String remarks,String taskTime);

	/**
	 * 创建维修工单
	 * @param objectContext
	 * @param sysUser
	 * @param vmId
	 * @param ownerId
	 * @param taskRemarks
	 * @param taskTime
	 * @return
	 */
	String createRepairTask(ObjectContext objectContext, TSysUser sysUser,
			Integer vmId, Integer ownerId,String taskRemarks,String taskTime);

	/**
	 * 创建撤机工单
	 * @param objectContext
	 * @param sysUser
	 * @param vmId
	 * @param own
	 * @param taskTime
	 * @return
	 */
	String createwithdrawTask(ObjectContext objectContext, TSysUser sysUser,
			Integer vmId, Integer own,String taskTime);

	/**
	 * 创建投放工单
	 * @param objectContext
	 * @param sysUser
	 * @param vmId
	 * @param own
	 * @param taskTime
	 * @param nodeId
	 * @return
	 */
	String createdeployTask(ObjectContext objectContext, TSysUser sysUser,
			Integer vmId, Integer own,String taskTime,Integer nodeId);

	/**
	 * 发布工单
	 * @param objectContext
	 * @param sysUser
	 * @param taskVmIds
	 * @param own
	 * @param taskTime
	 * @return
	 */
	String publishTask(ObjectContext objectContext, TSysUser sysUser,
			Integer[] taskVmIds, Integer own, String taskTime);

	/**
	 * 获取点位信息分页
	 * @param objectContext
	 * @param searchNodeName 点位名称
	 * @param pageSize
	 * @param token
	 * @return
	 */
	List mtNodePage(ObjectContext objectContext,String searchNodeName, Integer pageSize,TSysUser user);

	List<TTaskVm> exportTaskByIds(ObjectContext objectContext,
			Integer[] taskVmIds);

	boolean hasUnDealTask(ObjectContext context, TMtVm mtVm,Integer taskType);

	boolean hasUnDealRepairTask(ObjectContext context, TMtVm mtVm);

    /**
     * 根据VmIds新建补货工单
     * @param objectContext
     * @param sysUser
     * @param vmIds
     * @param ownerId
     * @return
     */
    String createDeliveryTaskByIds(ObjectContext objectContext, TSysUser sysUser,
                                   String[] vmIds, Integer ownerId, Integer deliveryPattern,List<TaskChannelModel> taskChannelModels);

	List searchChannelByVmId(ObjectContext context, Integer vmId);

}
