package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by yangshuxi on 2017/8/3.
 */
public enum OrderVendoutStatus implements ExtendedEnumeration {
    SUCCESS(0,"出货成功",""),
    COMMUNICATION_TIMEOUT(-1,"出货失败","通讯超时"),
    EMPTY(1,"出货失败","货道售空"),
    CHANNEL_BROKEN(2,"出货失败","货道故障"),
    OUT_TIMEOUT(3,"出货失败","出货超时"),
    MEANWHILE(4,"出货失败","连续支付"),
    ORDER_TIMEOUT(5,"出货失败","接受订单超时"),
    BUSY(6,"出货失败","设备忙碌"),
    NOT_REPORT(7,"出货未知","电控未上报结果"),
    PRINTER_BROKEN(8,"出货失败","打印机故障"),
    CHANNEL_MISSING(9,"出货失败","货道不存在"),
    NULL(-100,"未取货","")
    ;
    private Integer code;
    private String name;
    private String failReason;

    OrderVendoutStatus(Integer code, String name, String failReason) {
        this.code = code;
        this.name = name;
        this.failReason = failReason;
    }

    @Override
    public Object getDatabaseValue() {
        return this.code;
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
}
