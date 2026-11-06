package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/1.
 */
public interface IStVmEventService {
    public List findVmEventList(TSysUser currentUser, ObjectContext context, String discountType,String strategyName, Integer pageSize);
    public List findSkuEventList(TSysUser currentUser, ObjectContext context,String strategyName, Integer pageSize);
    public Map<String, Object> saveVmEvent(TSysUser currentUser, ObjectContext context, String strategyName, String strategyDesc, Integer discountType,Integer discountValue);
    public Map<String, Object> saveVmEvent(TSysUser currentUser, ObjectContext context, String strategyName,String strategyDesc, Integer discountType,Integer discountValue,Integer strategyId);
    public Map<String, Object> saveSkuEvent(TSysUser currentUser, ObjectContext context, String strategyName,String strategyDesc,Integer[] skuIds,Integer[] discountTypes,Integer[] discountValues);
    public Map<String, Object> saveSkuEvent(TSysUser currentUser, ObjectContext context, String strategyName,String strategyDesc,Integer[] skuIds,Integer[] discountTypes,Integer[] discountValues,Integer strategyId);

}
