package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by yangshuxi on 2017/5/19.
 */
public interface SysSettingsDao {
    public List findAllSettings(ObjectContext context, Integer pageSize, String searchValue);

}
