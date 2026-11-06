package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtUpgradePackage;
import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtUpgradePlanDao;
import com.kdx.vms.service.IMtUpgradePlanService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kdx.vms.webapi.WebApiTask.context;

/**
 * 升级计划
 * Created by zhangguodong on 2017/6/22.
 */
@Service("mtUpgradePlanService")
public class MtUpgradePlanServiceImpl implements IMtUpgradePlanService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private MtUpgradePlanDao mtUpgradePlanDao;
    @Override
    public List<TMtUpgradePlan> findList(ObjectContext context, String versionNo, Integer pageSize) {
        try {
            return mtUpgradePlanDao.findPlanList(context,versionNo,pageSize);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public TMtUpgradePlan findById(ObjectContext context, Integer planId) {
        return mtUpgradePlanDao.findById(context,planId);
    }

    @Override
    public Map<String,Object> createVmBoxPlan(TSysUser user, Integer vmModelId, Integer areaId, Integer vmBoxPackageId, Integer comminocationBoardPackageId, Integer outBoardPackageId) {
        Map<String,Object> map = new HashedMap();
        try {

            if(null == vmModelId){
                map.put("type","error");
                map.put("message","请选择售货机型号");
                return map;
            }
            if(null == areaId){
                map.put("type","error");
                map.put("message","请选择区域");
                return map;
            }
            if(null == vmBoxPackageId && null == comminocationBoardPackageId && null == outBoardPackageId){
                map.put("type","error");
                map.put("message","请选择升级包");
                return map;
            }
            ObjectContext context = user.getObjectContext();
            if(null != vmBoxPackageId){
                TMtUpgradePlan plan =formatPlan(context,vmBoxPackageId,vmModelId,areaId,user.getId());
            }
            if(null != comminocationBoardPackageId){
                TMtUpgradePlan plan =formatPlan(context,comminocationBoardPackageId,vmModelId,areaId,user.getId());
            }
            if(null != outBoardPackageId){
                TMtUpgradePlan plan =formatPlan(context,outBoardPackageId,vmModelId,areaId,user.getId());
            }
            context.commitChanges();
            map.put("type","success");
            map.put("message","新建升级计划成功");
        }catch (Exception e){
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(),e);
            map.put("type","error");
            map.put("message","新建升级计划失败");
        }
        return map;

    }
    private TMtUpgradePlan formatPlan(ObjectContext context,Integer packageId,Integer vmModelId,Integer areaId,Integer userId ){
        TMtUpgradePlan plan = context.newObject(TMtUpgradePlan.class);
        plan.setVmModelId(vmModelId);
        plan.setAreaId(areaId);
        plan.setCtime(new Date());
        plan.setCtor(userId);
        plan.setUpgradePackage(Cayenne.objectForPK(context,TMtUpgradePackage.class,packageId));
        return plan;
    }

}
