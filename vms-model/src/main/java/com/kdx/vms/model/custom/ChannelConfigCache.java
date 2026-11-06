package com.kdx.vms.model.custom;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVmChannel;
import com.kdx.vms.model.bean.TMtVmChannelNext;
import com.kdx.vms.model.bean.TStVmChannel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2018/2/5.
 */
public class ChannelConfigCache {

    public ChannelConfigCache addData(TMtVmChannel vmChannel, Integer nextChannelId, Integer mergePid) {
        ChannelConfig channelConfig = ChannelConfig.of(vmChannel, nextChannelId, mergePid);
        data.add(channelConfig);
        return this;
    }

    public ChannelConfigCache addData(TMtVmChannelNext vmChannel) {
        ChannelConfig channelConfig = ChannelConfig.of(vmChannel);
        data.add(channelConfig);
        return this;
    }

    public static ChannelConfigCache newInstance(Integer vmId, Integer userId) {
        ChannelConfigCache cache = new ChannelConfigCache();
        cache.setData(new ArrayList<>()).setVmId(vmId).setUserId(userId);
        return cache;
    }

    private List<ChannelConfig> data;
    private Integer vmId;
    private Integer userId;

    public Integer getVmId() {
        return vmId;
    }

    public ChannelConfigCache setVmId(Integer vmId) {
        this.vmId = vmId;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public ChannelConfigCache setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public List<ChannelConfig> getData() {
        return data;
    }

    public ChannelConfigCache setData(List<ChannelConfig> data) {
        this.data = data;
        return this;
    }

    public Map<String, ChannelConfig> loadCodeMap() {
        Map<String, ChannelConfig> map = new HashMap<>();
        data.forEach(channelConfig -> map.put(channelConfig.getChannelCode(), channelConfig));
        return map;
    }

    public Map<Integer, ChannelConfig> loadIdMap() {
        Map<Integer, ChannelConfig> map = new HashMap<>();
        data.forEach(channelConfig -> map.put(channelConfig.getChannelId(), channelConfig));
        return map;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public List<ChannelConfig> findMergedList(Integer channelId) {
        return data.stream().filter(channelConfig -> null != channelConfig.getMergePid() && channelConfig.getMergePid().equals(channelId)).collect(Collectors.toList());
    }

    public static class ChannelConfig {
        public static ChannelConfig of(TMtVmChannel vmChannel, Integer nextChannelId, Integer mergePid) {
            ChannelConfig channelConfig = new ChannelConfig();
            channelConfig
                    .setChannelCode(vmChannel.getChannelCode())
                    .setFixedCapacity(vmChannel.getFixedCapacity())
                    .setIsFixedCapacity(vmChannel.getIsFixedCapacity())
                    .setChannelId(nextChannelId)
                    .setMergePid(mergePid)
                    .setMergeQty(vmChannel.getMergeQty())
                    .setSkuId(Optional.ofNullable(vmChannel.getSku()).map(TMtSku::getId).orElse(null))
                    .setMaxCapacity(vmChannel.getMaxCapacity())
                    .setAdjustMaxCapacity(vmChannel.getTempMaxCapacity())
                    .setAcceptWidthMax(vmChannel.getAcceptWidthMax())
                    .setChannelHeight(vmChannel.getChannelHeight())
                    .setChannelLong(vmChannel.getChannelLong())
                    .setChannelWidth(vmChannel.getChannelWidth())
                    .setDisplayMode(vmChannel.getDisplayMode());
            return channelConfig;
        }

        public static ChannelConfig of(TMtVmChannelNext vmChannel) {
            ChannelConfig channelConfig = new ChannelConfig();
            channelConfig
                    .setChannelCode(vmChannel.getChannelCode())
                    .setFixedCapacity(vmChannel.getFixedCapacity())
                    .setIsFixedCapacity(vmChannel.getIsFixedCapacity())
                    .setChannelId(vmChannel.getChannelId())
                    .setMergePid(vmChannel.getMergePid())
                    .setMergeQty(vmChannel.getMergeQty())
                    .setSkuId(Optional.ofNullable(vmChannel.getSku()).map(TMtSku::getId).orElse(null))
                    .setMaxCapacity(vmChannel.getMaxNum())
                    .setAdjustMaxCapacity(vmChannel.getMaxNum())
                    .setAcceptWidthMax(vmChannel.getAcceptWidthMax())
                    .setChannelHeight(vmChannel.getChannelHeight())
                    .setChannelLong(vmChannel.getChannelLong())
                    .setChannelWidth(vmChannel.getChannelWidth())
                    .setDisplayMode(vmChannel.getDisplayMode());
            return channelConfig;
        }

        private Integer channelId;
        private String channelCode;
        private Integer mergePid;
        private Integer mergeQty;
        private Integer skuId;
        private Integer maxCapacity;
        private Integer isFixedCapacity;
        private Integer fixedCapacity;
        private Integer adjustMaxCapacity;
        private Integer acceptWidthMax;
        private Integer channelHeight;
        private Integer channelLong;
        private Integer channelWidth;
        private Integer displayMode;

        public Integer getChannelId() {
            return channelId;
        }

        public ChannelConfig setChannelId(Integer channelId) {
            this.channelId = channelId;
            return this;
        }

        public String getChannelCode() {
            return channelCode;
        }

        public ChannelConfig setChannelCode(String channelCode) {
            this.channelCode = channelCode;
            return this;
        }

        public Integer getMergePid() {
            return mergePid;
        }

        public ChannelConfig setMergePid(Integer mergePid) {
            this.mergePid = mergePid;
            return this;
        }

        public Integer getMergeQty() {
            return mergeQty;
        }

        public ChannelConfig setMergeQty(Integer mergeQty) {
            this.mergeQty = mergeQty;
            return this;
        }

        public Integer getSkuId() {
            return skuId;
        }

        public ChannelConfig setSkuId(Integer skuId) {
            this.skuId = skuId;
            return this;
        }

        public Integer getMaxCapacity() {
            return maxCapacity;
        }

        public ChannelConfig setMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Integer getIsFixedCapacity() {
            return isFixedCapacity;
        }

        public ChannelConfig setIsFixedCapacity(Integer isFixedCapacity) {
            this.isFixedCapacity = isFixedCapacity;
            return this;
        }

        public Integer getFixedCapacity() {
            return fixedCapacity;
        }

        public ChannelConfig setFixedCapacity(Integer fixedCapacity) {
            this.fixedCapacity = fixedCapacity;
            return this;
        }

        public Integer getAdjustMaxCapacity() {
            return adjustMaxCapacity;
        }

        public ChannelConfig setAdjustMaxCapacity(Integer adjustMaxCapacity) {
            this.adjustMaxCapacity = adjustMaxCapacity;
            return this;
        }

        public Integer getAcceptWidthMax() {
            return acceptWidthMax;
        }

        public ChannelConfig setAcceptWidthMax(Integer acceptWidthMax) {
            this.acceptWidthMax = acceptWidthMax;
            return this;
        }

        public Integer getChannelHeight() {
            return channelHeight;
        }

        public ChannelConfig setChannelHeight(Integer channelHeight) {
            this.channelHeight = channelHeight;
            return this;
        }

        public Integer getChannelLong() {
            return channelLong;
        }

        public ChannelConfig setChannelLong(Integer channelLong) {
            this.channelLong = channelLong;
            return this;
        }

        public Integer getChannelWidth() {
            return channelWidth;
        }

        public ChannelConfig setChannelWidth(Integer channelWidth) {
            this.channelWidth = channelWidth;
            return this;
        }

        public Integer getDisplayMode() {
            return displayMode;
        }

        public ChannelConfig setDisplayMode(Integer displayMode) {
            this.displayMode = displayMode;
            return this;
        }
        public Integer getTempMaxCapacity() {

            if(IntegerUtils.isPositiveInteger(getAdjustMaxCapacity()))
                return getAdjustMaxCapacity();
            if(null!=getIsFixedCapacity()&&getIsFixedCapacity()== TStVmChannel.IS_FIXED_CAPACITY)
                return getFixedCapacity();
            return getMaxCapacity()==null?0:getMaxCapacity();
        }
    }
}
