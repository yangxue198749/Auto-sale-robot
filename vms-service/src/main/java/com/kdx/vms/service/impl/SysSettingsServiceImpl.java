package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.SysSettingsDao;
import com.kdx.vms.redis.ConfigLoader;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.ISysSettingsService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/22.
 */
@Service
public class SysSettingsServiceImpl implements ISysSettingsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SysSettingsDao sysSettingsDao;
    @Autowired
    MyJedisCluster myJedisCluster;
    @Override
    public Map<String, Object> addSetting(TSysUser currentUser, String key, String value, Integer pid, String name) {
        if (StringUtil.isEmpty(key) || null == pid || (StringUtil.isEmpty(value)&&pid!=0))
            return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            TSysSettings parent = Cayenne.objectForPK(currentUser.getObjectContext(), TSysSettings.class, pid);
            Map<String, TSysSettings> map = parent.getChildMap();
            if (null != map.get(key)) {
                return ReturnCode.SAME_SETTING_KEY_EXIST.toMap();
            }
            logger.info("create sys_setting key = " + key + " value = " + value + "pid = " + pid);
            TSysSettings sysSettings = currentUser.getObjectContext().newObject(TSysSettings.class);
            sysSettings.setKey(key);
            sysSettings.setValue(value);
            sysSettings.setName(name);
            sysSettings.setParent(parent);
            sysSettings.setCtime(new Date());
            sysSettings.setCtor(currentUser.getId());
            currentUser.getObjectContext().commitChanges();
            ConfigLoader configLoader = new ConfigLoader(myJedisCluster);
            Thread thread = new Thread(configLoader);
            thread.start();
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            currentUser.getObjectContext().rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> updateSetting(TSysUser currentUser, String key, String value, Integer pid, String name, Integer settingId) {
        if (StringUtil.isEmpty(key) || null == pid || (StringUtil.isEmpty(value)&&pid!=0) || !IntegerUtils.isPositiveInteger(settingId))
            return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            TSysSettings parent = Cayenne.objectForPK(currentUser.getObjectContext(), TSysSettings.class, pid);
            if(!parent.getKey().equals(TSysSettings.REFUND_TYPE)&&!parent.getKey().equals(TSysSettings.UPLOAD_TYPE)){
                return ReturnCode.SETTING_MODIFY_DENY.toMap();
            }
            Map<String, TSysSettings> map = parent.getChildMap();
            if (null != map.get(key) && !map.get(key).getId().equals(settingId)) {
                return ReturnCode.SAME_SETTING_KEY_EXIST.toMap();
            }
            logger.info("update sys_setting key = " + key + " value = " + value + "pid = " + pid);
            TSysSettings sysSettings = Cayenne.objectForPK(currentUser.getObjectContext(), TSysSettings.class, settingId);
            sysSettings.setKey(key);
            sysSettings.setValue(value);
            sysSettings.setName(name);
            sysSettings.setParent(parent);
            sysSettings.setUtime(new Date());
            sysSettings.setUtor(currentUser.getId());
            currentUser.getObjectContext().commitChanges();
            ConfigLoader configLoader = new ConfigLoader(myJedisCluster);
            Thread thread = new Thread(configLoader);
            thread.start();
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            currentUser.getObjectContext().rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public Map<String, Object> deleteSetting(TSysUser currentUser, Integer[] settingIds) {
        if (null == settingIds) return ReturnCode.PARAMETER_ERROR.toMap();
        try {
            for (Integer settingId : settingIds) {
                TSysSettings sysSettings = Cayenne.objectForPK(currentUser.getObjectContext(), TSysSettings.class, settingId);
                currentUser.getObjectContext().deleteObject(sysSettings);
            }
            currentUser.getObjectContext().commitChanges();
            ConfigLoader configLoader = new ConfigLoader(myJedisCluster);
            Thread thread = new Thread(configLoader);
            thread.start();
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            currentUser.getObjectContext().rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public List findAllSettings(ObjectContext context,Integer pageSize,String searchValue) {
        return this.sysSettingsDao.findAllSettings(context,pageSize,searchValue);
    }
}
