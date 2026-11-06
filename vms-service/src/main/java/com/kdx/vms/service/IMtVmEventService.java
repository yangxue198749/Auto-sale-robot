package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmEventMaster;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/6.
 */
public interface IMtVmEventService {
    public List<TMtVmEventMaster> findMtEventMaster(TSysUser currentUser, ObjectContext context, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer pageSize);
    public List<TMtVm> findSkuEventVmList(TSysUser currentUser, ObjectContext context, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId ,Integer[] removeIds);
    public List<TMtVm> findVmEventVmList(TSysUser currentUser, ObjectContext context, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId ,Integer[] removeIds);
    public Map<String, Object> saveMtVmEvent(TSysUser currentUser, ObjectContext context, Integer[] vmIds, Integer strategyId, Date beginDate, Date endDate, String startTime, String endTime,Integer eventType);
    public Map<String, Object> deleteEvent(TSysUser currentUser, ObjectContext context, Integer[] mtVmEventIds);
}
