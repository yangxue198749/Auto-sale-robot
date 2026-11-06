package com.kdx.vms.service;

import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;

import org.apache.cayenne.ObjectContext;

import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/1.
 */
public interface IStVmStrategyService {
    public Map<String, Object> deleteStrategy(TSysUser currentUser, ObjectContext context, Integer[] strategyIds,Integer strategyType);
    
    /**
     * 根据主键查询售货机策略信息
     * @param context
     * @param strategyId 策略主键
     * @return
     */
    public TStVmStrategy findByPk(ObjectContext context,Integer strategyId);
}
