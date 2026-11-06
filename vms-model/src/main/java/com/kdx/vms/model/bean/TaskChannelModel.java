package com.kdx.vms.model.bean;


public class TaskChannelModel {

    /**
     * 默认清货量
     */
    public static Integer DEFAULT_REALCOUNT =0;


    private Integer channelId;
    private Integer realClear;
    private Integer realCount;
    private Integer skuId;
    private Integer maxNum;
    private Integer currNum;
    private String imgUrl;
    private String skuName;
    private String channelCode;
    private boolean flag;


    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getRealClear() {
        return realClear;
    }

    public void setRealClear(Integer realClear) {
        this.realClear = realClear;
    }

    public Integer getRealCount() {
        return realCount;
    }

    public void setRealCount(Integer realCount) {
        this.realCount = realCount;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Integer getCurrNum() {
        return currNum;
    }

    public void setCurrNum(Integer currNum) {
        this.currNum = currNum;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}