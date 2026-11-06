package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtUpgradePackage;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtUpgradePackageDao;
import com.kdx.vms.service.IMtUpgradePackageService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by liyang on 2017/6/22.
 */
@Service("mtUpgradePackageService")
public class MtUpgradePackageServiceImpl implements IMtUpgradePackageService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource private MtUpgradePackageDao mtUpgradePackageDao;

    @Override
    public List getUpgradePackage(TSysUser currUser, Integer type, String version, Integer pageSize) {
        LOGGER.info("currUser:" + currUser + ", type:" + type + ", version:" + version + ", pageSize:" + pageSize);
        if (null == currUser) {
            LOGGER.error("currUser is null.");
            return Collections.emptyList();
        }
        return mtUpgradePackageDao.queryUpgradePackage(currUser.getObjectContext(), type, version, pageSize);
    }

    @Override
    public List getUpgradePackage(TSysUser currUser, Integer type, String version, Integer pageSize, Integer disable) {
        LOGGER.info("currUser:" + currUser + ", type:" + type + ", version:" + version + ", pageSize:" + pageSize+ " ,disable:"+disable);
        if (null == currUser) {
            LOGGER.error("currUser is null.");
            return Collections.emptyList();
        }
        return mtUpgradePackageDao.queryUpgradePackage(currUser.getObjectContext(), type, version, pageSize,disable);
    }

    @Override
    public SystemCode addUpgradePackage(TSysUser currUser, Integer type, String version, String url, String name, String md5, Long size) {
        LOGGER.info("currUser:" + currUser + ", type:" + type + ", version:" + version + ", name:" + name + ", md5:" + md5);
        try {
            if (null == currUser || null == currUser.getObjectContext()) {
                LOGGER.error("currUser or context is null.");
                return SystemCode.CONTEXT_IS_NULL;
            }
            if (StringUtil.isEmpty(version) || StringUtil.isEmpty(url) || StringUtil.isEmpty(md5)) {
                LOGGER.error(SystemCode.PARAMETER_ERROR.getMessage());
                return SystemCode.PARAMETER_ERROR;
            }
            ObjectContext context = ObjectContextFactory.getInstance().getChildObjectContext(currUser.getObjectContext());
            Date cTime = new Date();
            TMtVmResource mtVmResource = context.newObject(TMtVmResource.class);
            mtVmResource.setCtor(currUser.getId());
            mtVmResource.setCtime(cTime);
            mtVmResource.setDeleteMark(0);
            mtVmResource.setResourceMd5(md5);
            mtVmResource.setResourceName(name);
            mtVmResource.setResourceSize(size.intValue());
            mtVmResource.setResourceUrl(url);

            TMtUpgradePackage mtUpgradePackage = context.newObject(TMtUpgradePackage.class);
            mtUpgradePackage.setCtor(currUser.getId());
            mtUpgradePackage.setCtime(cTime);
            mtUpgradePackage.setDisable(TMtUpgradePackage.ENABLE);
            mtUpgradePackage.setPackageType(type);
            mtUpgradePackage.setPackageVersion(version);
            mtUpgradePackage.setResource(mtVmResource);

            context.commitChanges();

            return SystemCode.SUCCESS;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

    @Override
    public SystemCode editUpgradePackage(TSysUser currUser, Integer packageId) {
        LOGGER.info("currUser:" + currUser + ", packageId:" + packageId);
        if (null == currUser || null == packageId) {
            LOGGER.error("parameter error.");
            return SystemCode.PARAMETER_ERROR;
        }
        ObjectContext context = currUser.getObjectContext();
        if (null == context) {
            LOGGER.error("context is null.");
            return SystemCode.CONTEXT_IS_NULL;
        }
        TMtUpgradePackage upgradePackage = Cayenne.objectForPK(context, TMtUpgradePackage.class, packageId);
        if (null == upgradePackage) {
            LOGGER.error("upgradePackage not find.");
            return SystemCode.FAIL;
        }
        try {
            upgradePackage.setDisable(upgradePackage.getDisable() ^ 1);
            upgradePackage.setUtor(currUser.getId());
            upgradePackage.setUtime(new Date());
            context.commitChanges();
            if (TMtUpgradePackage.DISABLE.equals(upgradePackage.getDisable()))
                return SystemCode.BTN_ENABLE;
            else
                return SystemCode.BTN_DISABLE;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL;
    }

}
