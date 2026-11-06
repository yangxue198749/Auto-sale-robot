package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtUpgradePlan;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * 升级计划
 * Created by zhangguodong on 2017/6/22.
 */
public interface MtUpgradePlanDao {
    /**
     * 获取升级计划列表
     * @param context
     * @param versionNo
     * @param pageSize
     * @return
     */
    public List<TMtUpgradePlan> findPlanList(ObjectContext context,String versionNo ,Integer pageSize);

    /**
     * 根据主键获取计划
     * @param context
     * @param planId
     * @return
     */
    public TMtUpgradePlan findById(ObjectContext context,Integer planId);

    /**
     * 根据售货机型号获取计划
     * @param context
     * @param vmModelId
     * @return
     */
    public List<TMtUpgradePlan> findByModelId(ObjectContext context,Integer vmModelId);

    public List<TMtUpgradePlan> findByModelAndTypeAndArea(ObjectContext context,Integer vmModelId,Integer packageType,Integer areaId,Integer cityId,Integer provinceId);
}
