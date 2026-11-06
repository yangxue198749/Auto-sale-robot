package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/11/30.
 */
public interface IStSkuPriceService {

    public List findStrategyList(TSysUser currentUser, ObjectContext context, String strategyName,Integer pageSize);
    public Map<String, Object> saveStrategy(TSysUser currentUser, ObjectContext context, String strategyName,String strategyDesc,Integer[] skuIds,Integer[] realPrices);
    public Map<String, Object> saveStrategy(TSysUser currentUser, ObjectContext context, String strategyName,String strategyDesc,Integer[] skuIds,Integer[] realPrices,Integer strategyId);
}
