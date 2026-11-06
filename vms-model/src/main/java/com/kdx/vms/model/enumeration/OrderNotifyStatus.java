package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by yangshuxi on 2017/11/16.
 */
public enum  OrderNotifyStatus implements ExtendedEnumeration {
    STATUS_PENDING(1,"成功"),
    STATUS_FAIL(2,"失败"),
    STATUS_OFFLINE(3,"售货机不在线"),
    STATUS_TIMEOUT(4,"售货机回复超时"),
    STATUS_SERVER_TIMEOUT(5,"服务器通讯超时"),
    STATUS_SOLD_OUT(11,"售空"),
    STATUS_ERROR(-1,"通知出错"),
    STATUS_UN_NOTIFY(0,""),
    ;
    private Integer code;
    private String title;

    OrderNotifyStatus(Integer code, String title) {
        this.code = code;
        this.title = title;
    }

    public Integer getCode() {
        return code;
    }

    public OrderNotifyStatus setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public OrderNotifyStatus setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Object getDatabaseValue() {
        return code;
    }
}
