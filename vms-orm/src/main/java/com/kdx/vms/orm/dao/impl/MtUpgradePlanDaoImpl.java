package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtUpgradePackage;
import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.orm.dao.MtUpgradePlanDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhangguodong on 2017/6/22.
 */
@Service("mtUpgradePlanDao")
public class MtUpgradePlanDaoImpl implements MtUpgradePlanDao {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<TMtUpgradePlan> findPlanList(ObjectContext context, String versionNo, Integer pageSize) {
        try {
            Expression qualifier = ExpressionFactory.expTrue();
            if (!StringUtil.isEmpty(versionNo)) {
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtUpgradePlan.UPGRADE_PACKAGE_PROPERTY + "." + TMtUpgradePackage.PACKAGE_VERSION_PROPERTY, "%" + versionNo.trim() + "%"));
            }
            SelectQuery select = new SelectQuery(TMtUpgradePlan.class, qualifier);
            if (null != pageSize) {
                select.setPageSize(pageSize);
            }
            select.addOrdering(TMtUpgradePlan.CTIME_PROPERTY, SortOrder.ASCENDING);
            List<TMtUpgradePlan> list = context.performQuery(select);
            return list;

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TMtUpgradePlan findById(ObjectContext context, Integer planId) {
        return Cayenne.objectForPK(context, TMtUpgradePlan.class, planId);
    }

    @Override
    public List<TMtUpgradePlan> findByModelId(ObjectContext context, Integer vmModelId) {
        try {
            Expression qualifier = ExpressionFactory.expTrue();
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtUpgradePlan.VM_MODEL_ID_PROPERTY, vmModelId));
            SelectQuery select = new SelectQuery(TMtUpgradePlan.class, qualifier);
            List<TMtUpgradePlan> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtUpgradePlan> findByModelAndTypeAndArea(ObjectContext context, Integer vmModelId, Integer packageType, Integer areaId, Integer cityId, Integer provinceId) {
        try {
            Expression qualifier = ExpressionFactory.expTrue();
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtUpgradePlan.VM_MODEL_ID_PROPERTY, vmModelId));
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtUpgradePlan.UPGRADE_PACKAGE_PROPERTY + "." + TMtUpgradePackage.PACKAGE_TYPE_PROPERTY, packageType));
            if (IntegerUtils.isPositiveInteger(areaId) && IntegerUtils.isPositiveInteger(cityId) && IntegerUtils.isPositiveInteger(provinceId)) {
                Expression areaQualifier = ExpressionFactory.matchExp(TMtUpgradePlan.AREA_ID_PROPERTY, areaId);
                areaQualifier = areaQualifier.orExp(ExpressionFactory.matchExp(TMtUpgradePlan.AREA_ID_PROPERTY, cityId));
                areaQualifier = areaQualifier.orExp(ExpressionFactory.matchExp(TMtUpgradePlan.AREA_ID_PROPERTY, provinceId));
                qualifier = qualifier.andExp(areaQualifier);
            }
            SelectQuery select = new SelectQuery(TMtUpgradePlan.class, qualifier);
            select.addPrefetch(TMtUpgradePlan.UPGRADE_PACKAGE_PROPERTY);
            select.addPrefetch(TMtUpgradePlan.UPGRADE_PACKAGE_PROPERTY + "." + TMtUpgradePackage.RESOURCE_PROPERTY);
            List<TMtUpgradePlan> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
