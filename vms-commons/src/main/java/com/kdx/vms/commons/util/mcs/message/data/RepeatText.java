package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 *
 * Created by liyang on 2016/11/8
 */
public class RepeatText implements Serializable{

    private static final long serialVersionUID = -3243245675089203063L;
    private String text;
    private String startTime;
    private String endTime;

    public RepeatText() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

}
