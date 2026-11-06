package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TSysSettings;
import org.apache.cayenne.Cayenne;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSysSettings extends _TSysSettings {
    public static Integer ROOT_ID = 0;

    public static String REFUND_TYPE = "refund";
    public static String REFUND_MAX_MONEY = "max_refund_money";

    public static String UPLOAD_TYPE = "upload";
    public static String THEME_MAX_ZIP_SIZE = "theme_max_zip_size";
    public static String THEME_MAX_PREVIEW_SIZE = "theme_max_preview_size";
    public static String SKU_MAX_IMAGE_SIZE = "sku_max_image_size";

    public static String REDIS_TYPE = "redis";

    public static String MCS_TYPE = "mcs";


    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    public static Integer ADD_TYPE;
    public static Integer ADD_SETTING;

    public Map<String, TSysSettings> getChildMap() {
        List<TSysSettings> list = super.getChild();
        Map<String, TSysSettings> map = new HashMap<>();
        if (null != list && list.size() > 0) {
            for (TSysSettings sysSettings : list) {
                map.put(sysSettings.getKey(), sysSettings);
            }
        }
        return map;
    }
}
