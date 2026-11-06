package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TOrderMaster;
import org.apache.cayenne.Cayenne;

import java.util.HashMap;
import java.util.Map;

public class TOrderMaster extends _TOrderMaster {
    public final static String RESERVE = "reserve";
    public final static String IMMEDIATE = "immediate";
    // 5.0.0a 删掉预售类型
    // public final static String PROMOTION = "promotion";
    public final static String CARD = "card";

    public static Map<String,String> map = new HashMap<>();
    static {
        map.put(IMMEDIATE,"即售订单");
        map.put(RESERVE,"预定订单");
        map.put(CARD,"即售订单");
    }
    public String getOrderTypeCn(){
        return map.get(super.getOrderType());
    }

    public Integer getId(){
        return Cayenne.intPKForObject(this);
    }


}
