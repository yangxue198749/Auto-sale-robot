package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.auto._TTaskVmChannel;
import org.apache.cayenne.Cayenne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TTaskVmChannel extends _TTaskVmChannel implements Comparable<TTaskVmChannel> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final int TYPE_DELIVERY = 3;

    public static final int CHANNEL_TYPE_FILL = 1;
    public static final int CHANNEL_TYPE_CLEAN = 2;
    public static final int CHANNEL_TYPE_ADJUST = 3;

    public boolean isFill() {
        return null != getTaskType() && getTaskType() == CHANNEL_TYPE_FILL;
    }

    public boolean isClean() {
        return null != getTaskType() && getTaskType() == CHANNEL_TYPE_CLEAN;
    }

    public boolean isAdjust() {
        return null != getTaskType() && getTaskType() == CHANNEL_TYPE_ADJUST;
    }

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    @Override
    public int compareTo(TTaskVmChannel tTaskVmChannel) {
        try {
            String channelCode1 = this.getChannel().getChannelCode();
            String channelCode2 = tTaskVmChannel.getChannel().getChannelCode();
            int colNo1 = StringUtil.getIntValue(channelCode1.split("-")[0], 0);
            int colNo2 = StringUtil.getIntValue(channelCode2.split("-")[0], 0);
            int rowNo1 = StringUtil.getIntValue(channelCode1.split("-")[1], 0);
            int rowNo2 = StringUtil.getIntValue(channelCode2.split("-")[1], 0);
            return colNo1 == colNo2 ? rowNo1 - rowNo2 : colNo1 - colNo2;
        } catch (Exception e) {
            return 0;
        }
    }

    private Integer maxCapacity;

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

//    public Integer getMaxCapacity() {
//        if(null==maxCapacity) return getChannel().getTempMaxCapacity();
//        return maxCapacity;
//    }

    public Integer getClear() {
        if (null != getRealClear()) {
            return getRealClear();
        } else {
            if (null != getClearCount()) {
                return getClearCount();
            }
        }
        return 0;
    }

}
