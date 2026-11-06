package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtVmEvent;

public class TMtVmEvent extends _TMtVmEvent {
    public void setProperties(TStVmEvent stVmEvent){
        this.setSku(stVmEvent.getSku());
        this.setDiscountType(stVmEvent.getDiscountType());
        this.setDiscountValue(stVmEvent.getDiscountValue());
        this.setEventType(stVmEvent.getEventType());
    }
}
