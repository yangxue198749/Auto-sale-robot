package com.kdx.vms.model.custom;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/9/30.
 */
public class MpJsonObject<T> {
    public static final String TYPE_OK = "ok";
    public static final String TYPE_ERROR = "error";

    private String type;
    private String message;
    private Map<String,Object> summary = new HashMap<>();
    private List<T> data = new ArrayList<>();

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public MpJsonObject addSummary(String key, Object value) {
        this.summary.put(key, value);
        return this;
    }
    public MpJsonObject addData(T t){
        this.data.add(t);
        return this;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public MpJsonObject(String type, String message, List<T> data) {
        this.type = type;
        this.message = message;
        this.data = data;
    }
}
