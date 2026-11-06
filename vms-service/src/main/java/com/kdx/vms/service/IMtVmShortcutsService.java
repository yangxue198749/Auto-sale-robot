package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/9.
 */
public interface IMtVmShortcutsService {
    public Map<String, Object> saveShortcuts(TSysUser currentUser, ObjectContext context, Integer[] ids, Integer[] skuIds,String[] shortcutsCodes,Integer[] resourceIds,Integer[] bindTypes);

    public Map<String, Object> saveShortcuts(TSysUser currentUser, ObjectContext context, Integer[] ids, Integer strategyId);
}
