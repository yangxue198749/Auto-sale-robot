package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVmVersion;

/**
 * 售货机设备版本信息
 * @author zhangguodong
 *
 */
public interface MtVmVersionDao {

	/**
	 * 获取设备版本信息
	 * @param context 
	 * @param vmId 售货机标识
	 * @param deviceType 版本类型
	 * @return
	 */
	TMtVmVersion fingVersionByVmIdAndType(ObjectContext context,Integer vmId,Integer deviceType);
}
