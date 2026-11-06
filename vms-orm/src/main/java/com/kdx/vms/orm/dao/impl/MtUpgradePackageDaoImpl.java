package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtUpgradePackage;
import com.kdx.vms.orm.dao.MtUpgradePackageDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 *
 * Created by liyang on 2017/6/22.
 */
@Service("mtUpgradePackageDao ")
public class MtUpgradePackageDaoImpl implements MtUpgradePackageDao {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List queryUpgradePackage(ObjectContext context, Integer type, String version, Integer pageSize) {
        LOGGER.info("context:" + context + ", type:" + type + ", version:" + version + ", pageSize:" + pageSize);
        try {
            if (null == context) {
                throw new NullPointerException("context is null.");
            }
            SelectQuery selectQuery = new SelectQuery(TMtUpgradePackage.class);
            if (null != type) { // 程序类型不为空时，增加查询条件
                selectQuery.andQualifier(ExpressionFactory.matchExp(TMtUpgradePackage.PACKAGE_TYPE_PROPERTY, type));
            }
            if (!StringUtil.isEmpty(version)) { // 版本号不为空时，增加查询条件
                selectQuery.andQualifier(ExpressionFactory.likeExp(TMtUpgradePackage.PACKAGE_VERSION_PROPERTY, "%" + version + "%"));
            }
            selectQuery.setPageSize(IntegerUtils.isPositiveInteger(pageSize) ? pageSize : 10);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List queryUpgradePackage(ObjectContext context, Integer type, String version, Integer pageSize, Integer disable) {
        LOGGER.info("context:" + context + ", type:" + type + ", version:" + version + ", pageSize:" + pageSize + ",disable:" + disable);
        try {
            if (null == context) {
                throw new NullPointerException("context is null.");
            }
            SelectQuery selectQuery = new SelectQuery(TMtUpgradePackage.class);
            if (null != type) { // 程序类型不为空时，增加查询条件
                selectQuery.andQualifier(ExpressionFactory.matchExp(TMtUpgradePackage.PACKAGE_TYPE_PROPERTY, type));
            }
            if (!StringUtil.isEmpty(version)) { // 版本号不为空时，增加查询条件
                selectQuery.andQualifier(ExpressionFactory.likeExp(TMtUpgradePackage.PACKAGE_VERSION_PROPERTY, "%" + version + "%"));
            }
            if(null != disable){
                selectQuery.andQualifier(ExpressionFactory.matchExp(TMtUpgradePackage.DISABLE_PROPERTY, disable));
            }
            selectQuery.setPageSize(IntegerUtils.isPositiveInteger(pageSize) ? pageSize : 10);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

}
