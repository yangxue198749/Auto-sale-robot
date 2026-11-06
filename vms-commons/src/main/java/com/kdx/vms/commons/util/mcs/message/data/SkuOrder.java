package com.kdx.vms.commons.util.mcs.message.data;

/**
 * 货架
 * Created by zhangguodong on 2017/11/27.
 */
public class SkuOrder {

    private Integer skuId;
    private Integer index;

    public SkuOrder() {

    }

    public SkuOrder(Integer skuId, Integer index) {
        this.skuId = skuId;
        this.index = index;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkuOrder && ((SkuOrder) obj).getIndex().equals(this.getIndex());
    }
}
