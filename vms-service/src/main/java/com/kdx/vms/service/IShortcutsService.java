package com.kdx.vms.service;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;

/**
 *
 * Created by liyang on 16-11-29.
 */
public interface IShortcutsService {

    /**
     * 获取闪购键策略
     * @param company
     * @param strategyName
     * @param modelId
     * @param pageSize
     * @return
     */
    List getShortcutsStrategy(TMtCompany company, String strategyName, Integer modelId, Integer pageSize);

    public SystemCode addShortcutsStrategy(String strategyName, String strategyDesc, Integer modelId, Integer[] skuIds, TSysUser currUser,Integer[] bindTypes,Integer[] resourceIds);

    public SystemCode editShortcutsStrategy(Integer strategyId, String strategyName, String strategyDesc, Integer[] skuIds, TSysUser currUser,Integer[] bindTypes,Integer[] resourceIds);

    /**
     * 删除策略
     * @param strategyId
     * @param currUser
     * @return
     */
    SystemCode deleteShortcutsStrategy(Integer strategyId, TSysUser currUser);

    SystemCode deleteShortcutsStrategy(Integer[] strategyIds, TSysUser currUser);

}
