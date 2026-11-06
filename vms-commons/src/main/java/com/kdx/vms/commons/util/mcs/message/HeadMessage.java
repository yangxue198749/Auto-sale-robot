package com.kdx.vms.commons.util.mcs.message;

import com.kdx.vms.commons.util.mcs.message.data.Extentions;

import java.io.Serializable;

/**
 *
 * Created by liyang on 2016/11/7.
 */
public class HeadMessage implements Serializable{

    private static final long serialVersionUID = 6776162993909209744L;
    private String  from;
    private String  to;
    private Long    sn;
    private Boolean needResend;
    private Data data;

    HeadMessage() {
        this.needResend = false;
        this.data       = new Data();
    }

    public boolean check() {
        return null != from && !"".equals(from) && null != to && !"".equals(to) && null != sn && 0 <= sn && null != needResend && null != data;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public Boolean getNeedResend() {
        return needResend;
    }

    public void setNeedResend(Boolean needResend) {
        this.needResend = needResend;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
