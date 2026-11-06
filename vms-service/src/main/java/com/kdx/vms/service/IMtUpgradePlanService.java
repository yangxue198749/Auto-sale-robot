package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * 升级计划
 * Created by zhangguodong on 2017/6/22.
 */
public interface IMtUpgradePlanService {

    /**
     * 获取升级计划列表
     * @param context
     * @param versionNo
     * @param pageSize
     * @return
     */
    public List<TMtUpgradePlan> findList(ObjectContext context,String versionNo ,Integer pageSize);

    /**
     * 根据主键获取计划
     * @param context
     * @param planId
     * @return
     */
    public TMtUpgradePlan findById(ObjectContext context,Integer planId);

    /**
     * 创建升级计划
     * @param user
     * @param vmModelId
     * @param areaId
     * @param vmboxPackageId
     * @param comminocationBoardPackageId
     * @param outBoardPackageId
     * @return
     */
    public Map<String,Object> createVmBoxPlan(TSysUser user, Integer vmModelId, Integer areaId, Integer vmboxPackageId,Integer comminocationBoardPackageId,Integer outBoardPackageId);


}
