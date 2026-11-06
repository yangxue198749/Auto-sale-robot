package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtVmSkuSortNext;

public class TMtVmSkuSortNext extends _TMtVmSkuSortNext {
    public void setProperties(TMtVmSkuSort mtVmSkuSort){
        this.setSkuId(mtVmSkuSort.getSkuId());
        this.setSort(mtVmSkuSort.getSort());
    }

    public void setProperties(TStVmSkuSort stVmSkuSort){
        this.setSkuId(stVmSkuSort.getSkuId());
        this.setSort(stVmSkuSort.getSort());
    }

}
