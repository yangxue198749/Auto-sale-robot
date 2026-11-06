package com.kdx.mp.vo;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TTaskVmChannel;

/**
 * Created by yangshuxi on 2017/8/16.
 */
public class TaskChannel implements Comparable<TaskChannel> {
    private String channelCode;
    private Integer channelId;
    private Integer maxCapacity;
    private Integer targetCapacity;
    private Integer currCapacity;
    private String skuName;
    private String skuImg;
    private Integer skuId;
    private boolean changeSku = false;

    public String getChannelCode() {
        return channelCode;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Integer getTargetCapacity() {
        return targetCapacity;
    }

    public Integer getCurrCapacity() {
        return currCapacity;
    }

    public String getSkuName() {
        return skuName;
    }

    public String getSkuImg() {
        return skuImg;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setTargetCapacity(Integer targetCapacity) {
        this.targetCapacity = targetCapacity;
    }

    public void setCurrCapacity(Integer currCapacity) {
        this.currCapacity = currCapacity;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public void setSkuImg(String skuImg) {
        this.skuImg = skuImg;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public boolean isChangeSku() {
        return changeSku;
    }

    public void setChangeSku(boolean changeSku) {
        this.changeSku = changeSku;
    }

    public static TaskChannel newInstance(TTaskVmChannel tTaskVmChannel) {
        return new TaskChannel(tTaskVmChannel.getChannel().getChannelCode(), tTaskVmChannel.getChannel().getId(), tTaskVmChannel.getChannel().getCurrNum(), tTaskVmChannel.getSku().getSkuName() + tTaskVmChannel.getSku().getSkuUnit(), tTaskVmChannel.getSku().getImgUrl(),tTaskVmChannel.getSku().getId());
    }

    private TaskChannel(String channelCode, Integer channelId, Integer currCapacity, String skuName, String skuImg,Integer skuId) {
        this.channelCode = channelCode;
        this.channelId = channelId;
        this.currCapacity = currCapacity;
        this.skuName = skuName;
        this.skuImg = skuImg;
        this.skuId = skuId;
    }

    @Override
    public int compareTo(TaskChannel taskChannel) {
        try {
            String channelCode1 = this.getChannelCode();
            String channelCode2 = taskChannel.getChannelCode();
            int colNo1 = StringUtil.getIntValue(channelCode1.split("-")[0], 0);
            int colNo2 = StringUtil.getIntValue(channelCode2.split("-")[0], 0);
            int rowNo1 = StringUtil.getIntValue(channelCode1.split("-")[1], 0);
            int rowNo2 = StringUtil.getIntValue(channelCode2.split("-")[1], 0);
            return colNo1 == colNo2 ? rowNo1 - rowNo2 : colNo1 - colNo2;
        } catch (Exception e) {
            return 0;
        }
    }
}
