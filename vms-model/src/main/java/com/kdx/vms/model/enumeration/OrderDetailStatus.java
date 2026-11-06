package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by yangshuxi on 2017/8/2.
 */
public enum OrderDetailStatus implements ExtendedEnumeration {
    STATUS_PENDING(0,"待配送"),
    STATUS_DISTRIBUTION(1,"配送中"),
    STATUS_DELIVERED(2,"已送达"),
    ;

    private Integer code;
    private String title;

    OrderDetailStatus(Integer code, String title) {
        this.code = code;
        this.title = title;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Object getDatabaseValue() {
        return this.code;
    }
}
