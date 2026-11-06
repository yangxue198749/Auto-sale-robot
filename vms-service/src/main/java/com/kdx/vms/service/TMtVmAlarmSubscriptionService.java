package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtVmAlarmSubscription;
import org.apache.cayenne.ObjectContext;

import java.util.List;

public interface TMtVmAlarmSubscriptionService {


    List findUserByCompanyId(ObjectContext context, Integer companyId);

    /**
     * 新增告警订阅
     * @param context
     * @param userIds
     * @param companyId
     * @return
     */
    String addTMtVmAlarmSubscription(ObjectContext context, List<String> userIds, Integer companyId);

    /**
     * 根据companyId查询
     * @param context
     * @param companyId
     * @return
     */
    List<TMtVmAlarmSubscription> findByCompanyId(ObjectContext context, Integer companyId);




}
