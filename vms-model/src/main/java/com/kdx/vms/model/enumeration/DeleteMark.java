package com.kdx.vms.model.enumeration;

import org.apache.cayenne.ExtendedEnumeration;

/**
 *
 * Created by liyang on 2017/5/3.
 */
public enum DeleteMark implements ExtendedEnumeration {

    DELETE(1, "删除"),
    EXIST(0, "存在")
    ;

    private Integer code;
    private String  name;

    private DeleteMark(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object getDatabaseValue() {
        return code;
    }

}
