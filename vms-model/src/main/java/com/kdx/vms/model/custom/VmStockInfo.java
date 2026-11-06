package com.kdx.vms.model.custom;

/**
 *
 * Created by liyang on 2016/12/16.
 */
public class VmStockInfo {

    private Integer skuId;
    private String  name;
    private Integer currCount;
    private Integer maxCount;

    public  VmStockInfo() {
        this.currCount = 0;
        this.maxCount = 0;
    }

    public static VmStockInfo newInstance() {
        return new VmStockInfo();
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrCount() {
        return currCount;
    }

    public synchronized void addCurrCount(Integer currCount) {
        this.currCount += null == currCount ? 0 : currCount;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public synchronized void addMaxCount(Integer maxCount) {
        this.maxCount += null == maxCount ? 0 : maxCount;
    }

    public String toString() {
        return "name:" + name + ", currCount:" + currCount + ", maxCount:" + maxCount;
    }

}
