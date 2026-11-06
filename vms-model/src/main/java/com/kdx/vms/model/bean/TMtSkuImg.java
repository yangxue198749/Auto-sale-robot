package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.auto._TMtSkuImg;

public class TMtSkuImg extends _TMtSkuImg {


    /**
     * 不是详情图
     */
    public static final int NO_IMG_MASTER_URL = 0;
    /**
     * 详情图
     */
    public static final int IMG_MASTER_URL = 1;
    /**
     * 商品图1
     */
    public static final int IMG_FIRST_URL = 1;
    /**
     * 商品图2
     */
    public static final int IMG_SECOND_URL = 2;
    /**
     * 商品图3
     */
    public static final int IMG_THREE_URL = 3;
    /**
     * 描述图
     */
    public static final int IMG_DESC_URL = 4;

    /**
     * 来源：VMS
     */
    public static final int IMG_SOURCE_VMS = 1;

    public String getSkuImgUrl() {
        if (CheckUtils.check(this.getImgUrl())) {
            return PropertiesLoader.map.get("imgUrlPrefix") + this.getImgUrl();
        }
        return PropertiesLoader.map.get("imgUrlPrefix") + "default.png";
    }

}
