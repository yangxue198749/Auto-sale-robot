package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

/**
 * 
 * @author zhangguodong
 *
 */
public interface VmOperationHisDao {

	/**
	 * 获取当前售货机开关门状态
	 * @param context
	 * @param vmId
	 * @return
	 */
	public Integer getCurrDoorStatus(ObjectContext context,Integer vmId);
}
