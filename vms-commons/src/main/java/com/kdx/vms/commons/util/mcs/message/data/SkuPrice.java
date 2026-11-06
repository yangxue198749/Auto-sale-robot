package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 *
 * Created by liyang on 2016/11/8.
 */
public class SkuPrice implements Serializable{

    private static final long serialVersionUID = 1416014692280716419L;
    private Integer skuId;
    private Integer price;
    private Boolean IsDiscount;

    public SkuPrice() {
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

	public Boolean getIsDiscount() {
		return IsDiscount;
	}

	public void setIsDiscount(Boolean IsDiscount) {
		this.IsDiscount = IsDiscount;
	}

    public Boolean getDiscount() {
        return IsDiscount;
    }

    public void setDiscount(Boolean discount) {
        IsDiscount = discount;
    }
}
