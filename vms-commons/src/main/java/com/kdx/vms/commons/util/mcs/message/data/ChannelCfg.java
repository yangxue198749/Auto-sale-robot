package com.kdx.vms.commons.util.mcs.message.data;


import java.io.Serializable;

/**
 *
 * Created by liyang on 2016/11/7.
 */
public class ChannelCfg implements Serializable{

    private static final long serialVersionUID = 2766534858570005831L;
    private String  channelCode;
    private Integer maxCapcity;
    private Integer skuId;
    /**
     * 批号
     */
    private String batchNumber;

    /**
     * 失效日期
     */
    private String expiryDate;
    /**
     * 生产日期
     */
    private String productDate;
	public ChannelCfg() {
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getMaxCapcity() {
        return maxCapcity;
    }

    public void setMaxCapcity(Integer maxCapcity) {
        this.maxCapcity = maxCapcity;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }
}
