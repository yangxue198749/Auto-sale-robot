package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 * Create zhouxing by 18/3/5
 */
public class Portalcfg implements Serializable{

    private static final long serialVersionUID = -820311737874095439L;

    private Integer portalCode;
    private String bindUrl;
    private String imgUrl;

    public Portalcfg() {
    }

    public Integer getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(Integer portalCode) {
        this.portalCode = portalCode;
    }

    public String getBindUrl() {
        return bindUrl;
    }

    public void setBindUrl(String bindUrl) {
        this.bindUrl = bindUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}