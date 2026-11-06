package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TVmStatusHis;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TVmStatus;

/**
 *
 * Created by liyang on 16-12-15.
 */
public interface VmStatusDao {

	/**
	 * 获取售货机状态
	 * @param context
	 * @param vmId 售货机主键
	 * @return
	 */
	public TVmStatus findByMtVmId(ObjectContext context,Integer vmId);
	public TVmStatusHis findLastNormalHis(ObjectContext context,Integer vmId);
}
