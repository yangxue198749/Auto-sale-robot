package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by liyang on 17-8-1.
 */
public enum PayType implements ExtendedEnumeration {

    WECHAT_PAY(1101, "微信支付"),
    ALI_PAY(1201, "支付宝支付"),
    UNION_PAY(1301, "银联支付"),
    RECHARGE_CARD(2101, "康得新充值卡"),
    RECHARGE_CARD2102(2102, "蛋卡83折"),
    RECHARGE_CARD2103(2103, "蛋卡90折"),
    RECHARGE_CARD2104(2104, "蛋卡无折扣"),
    RECHARGE_CARD2105(2105, "蛋卡赠送"),
    FICT_CARD(2000, "虚拟卡"),
    RECHARGE_CARD0(0, "康得新充值卡"),
    UNKNOWN(-1, "未知"),
    ;

    private Integer code;
    private String  name;

    PayType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Object getDatabaseValue() {
        return code;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
