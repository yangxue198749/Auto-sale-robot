package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.auto._TMtVmThemeEntrance;

public class TMtVmThemeEntrance extends _TMtVmThemeEntrance {
    /**
     * 快捷入口：1
     */
    public static final int FIRST_PORTAL = 1;
    /**
     * 快捷入口：2
     */
    public static final int SECOND_PORTAL = 2;
    /**
     * 快捷入口：3
     */
    public static final int THIRD_PORTAL = 3;

    public String getResouseName(){
        TMtVmResource resource = this.getResource();
        if(null == resource) return null;
        String name = resource.getResourceName();
        return name;
    }
    public Integer getResouseId(){
        TMtVmResource resource = this.getResource();
        if(null == resource) return null;
        Integer id = resource.getId();
        return id;
    }

    public String getSkuImgUrl(){
        if (CheckUtils.check(getImgUrl())) {
            return PropertiesLoader.map.get("imgUrlPrefix") + getImgUrl();
        }
        return PropertiesLoader.map.get("imgUrlPrefix") + "default.png";
    }

}
