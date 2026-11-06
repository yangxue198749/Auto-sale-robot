package com.kdx.vms.service;

import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;

/**
 * Created by yangshuxi on 2016/12/12.
 */
public interface IMtVmEnergySaveService {
    public Map<String, Object> saveEnergySave(TSysUser currentUser, ObjectContext context,Integer targetValue,Integer vmId,
                                              Integer energySaveType, String startTime, String endTime, String remark,Integer tempBoxNo);
    public Map<String, Object> saveEnergySave(TSysUser currentUser, ObjectContext context,Integer mtVmEnergySaveId, Integer targetValue,Integer vmId,
                                              Integer energySaveType, String startTime, String endTime, String remark,Integer tempBoxNo);
    public Map<String, Object> delete(TSysUser currentUser, ObjectContext context,Integer[] ids);
    
	public String makeTableString(ObjectContext context, Integer mtVmId);
	
	public String selectSt(ObjectContext objectContext, Integer[] mtVmIds,
			Integer strategyId,TSysUser user);
	/**
	 * 推送到MCS
	 * @param objectContext
	 * @param mtVmIds
	 * @return
	 */
	public String pushToMCS(ObjectContext objectContext, Integer[] mtVmIds);
	/**
	 * 推送到MCS
	 * new
	 * @param objectContext
	 * @param mtVmIds
	 * @return
	 */
	String pushToMCSNew(ObjectContext objectContext, Integer[] mtVmIds);
}
