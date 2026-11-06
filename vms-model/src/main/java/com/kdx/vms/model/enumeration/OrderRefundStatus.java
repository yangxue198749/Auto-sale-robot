package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by yangshuxi on 2017/8/3.
 */
public enum OrderRefundStatus implements ExtendedEnumeration {
    CREATE(0,"","","新创建退款订单"),
    NOTICE_SUCCESS(1,"退款中","退押金中","退款通知成功"),
    NOTICE_FAIL(2,"退款失败","退押金失败","退款通知失败"),
    SUCCESS(3,"已退款","已退押金","退款到账"),
    FAIL(4,"退款失败","退押金失败","退款失败"),
    PROCESSING(5,"退款中","退押金中","退款处理中"),
    CHANGE(6,"退款失败","退押金失败","转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款。")
    ;
    private Integer code;
    private String name;
    private String depositName;
    private String failReason;

    OrderRefundStatus(Integer code, String name, String depositName, String failReason) {
        this.code = code;
        this.name = name;
        this.failReason = failReason;
        this.depositName = depositName;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getFailReason() {
        return failReason;
    }

    public String getDepositName() {
        return depositName;
    }

    @Override
    public Object getDatabaseValue() {
        return code;
    }
}
