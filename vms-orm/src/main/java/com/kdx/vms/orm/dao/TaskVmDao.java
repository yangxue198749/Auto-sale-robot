package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 16-11-28.
 */
public interface TaskVmDao {

    List hasTaskVm(TMtVm mtVm);

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
	List<TTaskVm> findPage(ObjectContext context, TSysUser user,
			Integer searchField, String searchFieldValue, Integer taskVmStatus,
			String startTime, String endTime, Integer taskType,
			Integer pageSize);

	/**
	 * 获取今天未处理的工单
	 * @param context
	 * @param mtVm 售货机
	 * @param taskType 工单类型
	 * @param day 日期 例：2016-12-12
	 * @return
	 */
	TTaskVm getUnDealTaskToday(ObjectContext context, TMtVm mtVm,Integer taskType,String day);

	/**
	 * 根据主键获取工单信息
	 * @param context
	 * @param taskVmId
	 * @return
	 */
	TTaskVm findByPk(ObjectContext context, Integer taskVmId);

	List findByOwner(ObjectContext context, Integer userId,Integer taskType);

	public List findTaskByOwnerAndDay(ObjectContext context, Integer userId,String day,Integer taskType);
	public List findFinishTaskByOwnerAndDay(ObjectContext context, Integer userId,String day,Integer taskType);

	TTaskVm findByVmId(ObjectContext objectContext, Integer vmId, Integer ownerId, Integer deliveryPattern);

	List refreshChannel(TTaskVm taskVm);

	List<?> findDeployTaskByVm(TMtVm mtVm);

	List searchFinishTask(ObjectContext context, TMtVm mtVm, Integer taskType);
}
