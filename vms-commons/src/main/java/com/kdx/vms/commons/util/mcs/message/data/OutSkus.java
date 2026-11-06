package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 *
 * Created by liyang on 2016/11/9.
 */
public class OutSkus implements Serializable{

    private static final long serialVersionUID = 9220933952977874267L;
    private Long    requestTime;
    private Integer skuId;
    private String  orderId;
    private Integer timeOut;
    private Integer payPrice;

    public OutSkus() {
    }

    public Integer getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(Integer payPrice) {
        this.payPrice = payPrice;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }
}
