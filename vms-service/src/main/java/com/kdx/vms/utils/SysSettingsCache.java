package com.kdx.vms.utils;

import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/19.
 */
public class SysSettingsCache {
    private static final Logger logger = LoggerFactory.getLogger(SysSettingsCache.class);
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static TSysSettings root;

    static {
        root = Cayenne.objectForPK(context, TSysSettings.class, TSysSettings.ROOT_ID);
        System.out.println(root);
        if (null == root) {
            root = new TSysSettings();
            root.setObjectId(new ObjectId(TSysSettings.class.getSimpleName(), TSysSettings.ID_PK_COLUMN, TSysSettings.ROOT_ID));
            root.setCtime(new Date());
            root.setCtor(UserUtils.getVmsSystem().getId());
            root.setName("ROOT");
            root.setKey("sys_setting_root");
            context.registerNewObject(root);
            context.commitChanges();
        }
    }

    public static TSysSettings getRoot() {
        return root;
    }

    public static Map<String, TSysSettings> getSettingsByParentKey(String parentKey) {
        Map<String, TSysSettings> parentMap = root.getChildMap();
        if (null != parentMap && parentMap.size() > 0) {
            TSysSettings parentSetting = parentMap.get(parentKey);
            if (null != parentSetting) {
                return parentSetting.getChildMap();
            }
        }
        return Collections.emptyMap();
    }

    public static Map<String, String> getSettingsValueByParentKey(String parentKey) {
        try {

            Map<String, TSysSettings> map = getSettingsByParentKey(parentKey);
            if (null != map) {
                Map<String, String> valueMap = new HashMap<>();
                for (String key : map.keySet()) {
                    valueMap.put(key, map.get(key).getValue());
                }
                return valueMap;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyMap();
    }

    public static String getSetting(String key, String parentKey, String defaultValue) {
        Map<String, TSysSettings> map = getSettingsByParentKey(parentKey);
        if (null != map && map.size() > 0) {
            TSysSettings setting = map.get(key);
            if (null != setting) {
                return setting.getValue() == null ? defaultValue : setting.getValue();
            }
        }
        return defaultValue;
    }

}
