package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtVmAlarmSubscription;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.TMtVmAlarmSubscriptionService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TMtVmAlarmSubscriptionServiceImpl implements TMtVmAlarmSubscriptionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List findUserByCompanyId(ObjectContext context, Integer companyId) {
        try {
            Expression expression = ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, companyId);
            SelectQuery select = new SelectQuery(TSysUser.class, expression);
            List list = context.performQuery(select);
            if (list != null && list.size() > 0) {
                return list;
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return Collections.emptyList();
    }

    @Override
    public String addTMtVmAlarmSubscription(ObjectContext context, List<String> userIds, Integer companyId) {
        try {
            List<TMtVmAlarmSubscription> allUser = this.findByCompanyId(context, companyId);//所有订阅用户
            if(null != allUser && allUser.size() >0){
                context.deleteObjects(allUser);
            }
            if(null != userIds && userIds.size() >0){
                for(String userId : userIds){
                    TMtVmAlarmSubscription tMtVmAlarmSubscription = context.newObject(TMtVmAlarmSubscription.class);
                    tMtVmAlarmSubscription.setUserId(Integer.parseInt(userId));
                    tMtVmAlarmSubscription.setCompanyId(companyId);
                }
            }
            context.commitChanges();
            return null;
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(),e);
        }
        return "系统错误";
    }

    @Override
    public List<TMtVmAlarmSubscription> findByCompanyId(ObjectContext context, Integer companyId) {
        try {
            Expression expression = ExpressionFactory.matchExp(TMtVmAlarmSubscription.COMPANY_ID_PROPERTY, companyId);
            SelectQuery select = new SelectQuery(TMtVmAlarmSubscription.class, expression);
            List<TMtVmAlarmSubscription> list = context.performQuery(select);
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}
