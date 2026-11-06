package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.model.bean.TMtUpgradeVersion;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.orm.cache.AreaCache;
import com.kdx.vms.orm.dao.MtUpgradePlanDao;
import com.kdx.vms.service.IDealVmUpgradeRequestService;
import com.kdx.vms.utils.VersionComparator;
import com.kdx.vms.utils.VmVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/6/21.
 */
@Service
public class DealVmUpgradeRequestServiceImpl implements IDealVmUpgradeRequestService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Resource
    MtUpgradePlanDao mtUpgradePlanDao;
    private VersionComparator comparator = VersionComparator.getInstance();

    @Override
    public TMtUpgradePlan findNextVmBoxVersion(TMtVm mtVm, String version) {
        if (null == mtVm || StringUtil.isEmpty(version)) return null;
        try {
            List<TMtUpgradePlan> planList = findPlanList(mtVm, VmVersion.VM_BOX, false);
            if (null != planList && !planList.isEmpty()) {
                //从小到大排序，返回第一个大于上报版本号的版本
                Collections.reverse(planList);
                for (TMtUpgradePlan mtUpgradePlan : planList) {
                    if (comparator.compareVersionNum(mtUpgradePlan.getUpgradePackage().getPackageVersion(), version) < 0) {
                        return mtUpgradePlan;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public String updateDownloadedVersion(TMtVm mtVm, String version, Integer type) {
        if (null == mtVm || StringUtil.isEmpty(version) || null == type) {
            return "参数错误";
        }
        LOGGER.info("更新下载版本信息，mtVm:" + mtVm.getInnerCode() + "version:" + version + "type:" + type);
        try {
            synchronized (mtVm.getInnerCode()) {
                Map<Integer, TMtUpgradeVersion> map = mtVm.getUpgradeVersion();
                if (null != map && null != map.get(type)) {
                    TMtUpgradeVersion mtUpgradeVersion = map.get(type);
                    mtUpgradeVersion.setLastDownloadedVer(version);
                } else {
                    TMtUpgradeVersion mtUpgradeVersion = mtVm.getObjectContext().newObject(TMtUpgradeVersion.class);
                    mtUpgradeVersion.setLastDownloadedVer(version);
                    mtUpgradeVersion.setMtVm(mtVm);
                    mtUpgradeVersion.setVersionType(type);
                }
                mtVm.getObjectContext().commitChanges();
                return "";
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            mtVm.getObjectContext().rollbackChanges();
        }
        return "保存下载版本信息失败";
    }

    @Override
    public TMtUpgradePlan findHighestVersion(TMtVm mtVm, String version, VmVersion type) {
        if (null == mtVm || StringUtil.isEmpty(version) || null == type) return null;
        try {
            List<TMtUpgradePlan> planList = findPlanList(mtVm, type, true);
            if (null != planList && !planList.isEmpty()) {
                TMtUpgradePlan mtUpgradePlan = planList.get(0);
                //如果有新版本，就返回最高版本升级计划，如果没有就返回null
                if (comparator.compareVersionNum(mtUpgradePlan.getUpgradePackage().getPackageVersion(), version) < 0) {
                    return mtUpgradePlan;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private List<TMtUpgradePlan> findPlanList(TMtVm mtVm, VmVersion type, boolean matchArea) {
        if (null == mtVm || null == type) return null;
        try {
            if (matchArea) {
                Integer areaId = mtVm.getNode().getAreaId();
                TMtArea area = AreaCache.getArea(areaId);
                TMtArea city = area.getParentArea();
                TMtArea province = city.getParentArea();
                List<TMtUpgradePlan> planList = mtUpgradePlanDao.findByModelAndTypeAndArea(mtVm.getObjectContext(), mtVm.getVmModelId(), type.getType(), areaId, city.getId(), province.getId());
                Collections.sort(planList, comparator);
                return planList;
            } else {
                List<TMtUpgradePlan> planList = mtUpgradePlanDao.findByModelAndTypeAndArea(mtVm.getObjectContext(), mtVm.getVmModelId(), type.getType(), null, null, null);
                Collections.sort(planList, comparator);
                return planList;
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}