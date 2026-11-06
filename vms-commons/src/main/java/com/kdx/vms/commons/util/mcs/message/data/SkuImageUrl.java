package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;
/**
 *
 * Created by zhouxing on 2018/03/26.
 */
public class SkuImageUrl implements Serializable{
    private static final long serialVersionUID = 2145165909175794293L;

    private String url;
    private Integer type;
    private Integer index;

    public SkuImageUrl(){

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}