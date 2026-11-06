package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 *
 * Created by liyang on 2016/11/8.
 */
public class MtEnergy implements Serializable{


    private static final long serialVersionUID = 155390689995111645L;
    private Integer energyType;
    private Integer targetValue;
    private String  startTime;
    private String  endTime;
    //增加 保温箱编号(序号)，开关屏时此字段为0
    private Integer tempBoxNo;
    public MtEnergy() {
    }

    public Integer getEnergyType() {
        return energyType;
    }

    public void setEnergyType(Integer energyType) {
        this.energyType = energyType;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTempBoxNo() {
        return tempBoxNo;
    }

    public void setTempBoxNo(Integer tempBoxNo) {
        this.tempBoxNo = tempBoxNo;
    }
}
