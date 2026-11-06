package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/13.
 */
public interface IMtVmPriceService {
    public Map<String, Object> savePrice(TSysUser currentUser, ObjectContext context, Integer strategyId, Integer[] ids);

    /**
     * 恢复原价
     * @param currentUser
     * @param context
     * @param ids
     * @return
     */
    Map<String, Object> originalPrice(TSysUser currentUser, ObjectContext context, Integer[] ids);
}
