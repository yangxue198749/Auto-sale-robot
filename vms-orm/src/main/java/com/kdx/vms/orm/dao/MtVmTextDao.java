package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmText;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtVmTextDao {

	/**
	 * 根据策略主键查询走马灯策略
	 * @param context
	 * @param strategyId 策略主键
	 * @return
	 */
	TStVmText findByStrategyId(ObjectContext context, Integer strategyId);
}
