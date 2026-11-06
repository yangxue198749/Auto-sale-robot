package com.kdx.vms.model.custom.beneficiary;

import com.alibaba.fastjson.JSON;

/**
 * Created by yangshuxi on 2018/2/26.
 */
public abstract class PayInfo {
    public String toJson() {
        return JSON.toJSONString(this);
    }
    public abstract String showId();
}
