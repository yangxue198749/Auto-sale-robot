package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by liyang on 2016/11/28.
 */
public interface StVmEventDao {
    public List findVmEventStrategy(ObjectContext context, TMtCompany company, String discountType, String strategyName, Integer pageSize);
    public List findSkuEventStrategy(ObjectContext context, TMtCompany company, String strategyName, Integer pageSize);
}
