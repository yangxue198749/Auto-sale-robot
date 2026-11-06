package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/22.
 */
public interface ISysSettingsService {
    public Map<String, Object> addSetting(TSysUser currentUser, String key, String value, Integer pid, String name);

    public Map<String, Object> updateSetting(TSysUser currentUser, String key, String value, Integer pid, String name, Integer settingId);

    public Map<String, Object> deleteSetting(TSysUser currentUser, Integer[] settingIds);

    public List findAllSettings(ObjectContext context, Integer pageSize,String searchValue);
}
