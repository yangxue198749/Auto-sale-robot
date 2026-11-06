package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmEnergySave;


/**
 * Created by liyang on 2016/11/28.
 */
public interface StVmEnergySaveDao {

	/**
	 * 根据策略主键查询节能策略
	 * @param context
	 * @param strategyId
	 * @return
	 */
	List<TStVmEnergySave> findByStrategyId(ObjectContext context,
			Integer strategyId);

	/**
	 * 根据主键查询节能策略明细
	 * @param context
	 * @param energySaveId 节能策略明细主键
	 * @return
	 */
	TStVmEnergySave findByPk(ObjectContext context, Integer energySaveId);

}
