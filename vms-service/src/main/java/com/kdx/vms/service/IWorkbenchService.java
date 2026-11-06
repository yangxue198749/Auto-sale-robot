package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/16.
 */
public interface IWorkbenchService {
    /**
     * 今日补货工单
     * @param context
     * @param currentUser
     * @return
     */
    public Map<Long,Integer> findDeliveryTaskList(ObjectContext context, TSysUser currentUser);
    /**
     * 今日维修工单
     * @param context
     * @param currentUser
     * @return
     */
    public Map<Long,Integer> findRepairTaskList(ObjectContext context, TSysUser currentUser);
    /**
     * 今日投放工单
     * @param context
     * @param currentUser
     * @return
     */
    public Map<Long,Integer> findDeployTaskList(ObjectContext context, TSysUser currentUser);
    /**
     * 今日撤机工单
     * @param context
     * @param currentUser
     * @return
     */
    public Map<Long,Integer> findWithdrawTaskList(ObjectContext context, TSysUser currentUser);
    /**
     * 今日新增点位
     * @param context
     * @param currentUser
     * @return
     */
    public Integer findNewNode(ObjectContext context, TSysUser currentUser);
    /**
     * 今日新增设备
     * @param context
     * @param currentUser
     * @return
     */
    public Integer findNewVm(ObjectContext context, TSysUser currentUser);
    /**
     * 今日促销设备
     * @param context
     * @param currentUser
     * @return
     */
    public Integer findEventVm(ObjectContext context, TSysUser currentUser);
    /**
     * 今日缺货设备
     * @param context
     * @param currentUser
     * @return
     */
    public Integer findOutOfStockVm(ObjectContext context, TSysUser currentUser);

    /**
     * 今日销量
     * @param context
     * @param currentUser
     * @return
     */
    public Integer findSalesVolume(ObjectContext context, TSysUser currentUser);

    /**
     * 今日销售额
     * @param context
     * @param currentUser
     * @return
     */
    public String findSalesAmount(ObjectContext context, TSysUser currentUser);
}
