package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TStVmEvent;

public class TStVmEvent extends _TStVmEvent {
    /**
     * 整机促销策略
     */
    public static final int EVENT_TYPE_VM     = 1;
    /**
     * 商品促销策略
     */
    public static final int EVENT_TYPE_SKU    = 2;
    /**
     * 百分比打折
     */
    public static final int DISCOUNT_TYPE_PERCENT     = 1;
    /**
     * 绝对值打折
     */
    public static final int DISCOUNT_TYPE_ABS         = 2;
}
