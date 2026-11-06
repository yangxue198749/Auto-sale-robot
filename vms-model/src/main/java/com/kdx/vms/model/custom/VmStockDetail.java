package com.kdx.vms.model.custom;

/**
 * Created by guoyang on 2017/11/23.
 */
public class VmStockDetail {
    private String  name;
    private Integer currCount;
    private Integer maxCount;
    private Integer dayCount;
    private String  hasStock;

    public static VmStockDetail newInstance() {
        return new VmStockDetail();
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

    public void setCurrCount(Integer currCount) {
        this.currCount = currCount;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    public String getHasStock() {
        return hasStock;
    }

    public void setHasStock(String hasStock) {
        this.hasStock = hasStock;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private String channel;
}
