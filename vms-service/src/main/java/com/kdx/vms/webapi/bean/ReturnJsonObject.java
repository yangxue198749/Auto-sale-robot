package com.kdx.vms.webapi.bean;


import com.alibaba.fastjson.JSON;

/**
 * Created by yangshuxi on 2016/12/30.
 */
public class ReturnJsonObject {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String IGNORE = "ignore";
    public static final Integer SUCCESS_CODE = 0;

    private String type = SUCCESS;
    private String message = "";
    private Integer errorCode = SUCCESS_CODE;
    private Long sn;

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    private void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setError(ReturnErrorCode error){
        setErrorCode(error.getErrorCode());
        setMessage(error.getErrorMessage());
        setType(error.getType());
    }

    public String toJson(){
        return JSON.toJSONString(this);
    }
}
