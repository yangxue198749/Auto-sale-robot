package com.kdx.vms.model.custom;

/**
 * Created by guoyang on 2017/12/11.
 */
public class VmStockMessage {
    private String innerCode;
    private String nodeName;
    private String coutProportion;
    private String stockoutSku;

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getCoutProportion() {
        return coutProportion;
    }

    public void setCoutProportion(String coutProportion) {
        this.coutProportion = coutProportion;
    }

    public String getStockoutSku() {
        return stockoutSku;
    }

    public void setStockoutSku(String stockoutSku) {
        this.stockoutSku = stockoutSku;
    }
}
