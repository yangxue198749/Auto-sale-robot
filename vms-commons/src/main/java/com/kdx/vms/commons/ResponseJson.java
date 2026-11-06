package com.kdx.vms.commons;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by yangshuxi on 2018/1/2.
 */
public class ResponseJson {
    private String type;
    private String message;
    private Map<String, Object> data;

    public String getType() {
        return type;
    }

    public ResponseJson setType(String type) {
        this.type = type;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseJson setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public ResponseJson setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public ResponseJson(boolean type, String message, Map<String, Object> data) {
        this.type = type ? "ok" : "error";
        this.message = message;
        this.data = data;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
