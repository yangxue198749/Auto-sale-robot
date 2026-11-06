package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by liyang on 17-8-8.
 */
public enum OrderRefundType implements ExtendedEnumeration {

    USER_GOOD_DEPOSIT(11, "用户操作商品退款","本人取消","已退款(含押金)"),
    USER_DEPOSIT(12, "用户操作退还押金","回瓶退款","已退奶瓶押金"),
    USER_GOOD(13, "用户操作商品退款","本人取消","已退款"),
    AUTO_DEPOSIT(21, "系统自动退还押金","逾期未取货","已退奶瓶押金"),
    AUTO_GOOD_DEPOSIT(22, "出货失败自动退款(包含押金)","出货失败","已退款(含押金)"),
    AUTO_GOOD(23, "出货失败自动退款","出货失败","已退款"),
    VMS_GOOD(31, "vms操作商品退款(不含押金)","客服退款","已退款"),
    VMS_GOOD_DEPOSIT(32, "vms操作商品退款(包含押金)","客服退款","已退款(含押金)"),
    VMS_DEPOSIT(33, "vms操作退押金","客服退款","已退款押金"),
    LOCK_FAIL(41,"锁定库存失败退款","锁定库存失败","已退款")
    ;

    private Integer code;
    private String  desc;
    private String reason;
    private String type;

    private OrderRefundType(Integer code, String desc, String reason, String type) {
        this.code = code;
        this.desc = desc;
        this.reason = reason;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public Object getDatabaseValue() {
        return code;
    }
}
