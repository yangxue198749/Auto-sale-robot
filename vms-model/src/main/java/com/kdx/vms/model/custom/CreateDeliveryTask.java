package com.kdx.vms.model.custom;

import java.util.List;

/**
 * 创建配送工单
 * Created by zhangguodong on 2017/11/21.
 */
public class CreateDeliveryTask {
    private Integer vmId;
    private Integer ownId;
    /**
     * 手动:true    应补量为输入量
     * 补满:false   应补量为空
     */
    private boolean custom;
    private List<DeliveryObject> deliveryObjectList;

    public Integer getVmId() {
        return vmId;
    }

    public void setVmId(Integer vmId) {
        this.vmId = vmId;
    }

    public Integer getOwnId() {
        return ownId;
    }

    public void setOwnId(Integer ownId) {
        this.ownId = ownId;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public List<DeliveryObject> getDeliveryObjectList() {
        return deliveryObjectList;
    }

    public void setDeliveryObjectList(List<DeliveryObject> deliveryObjectList) {
        this.deliveryObjectList = deliveryObjectList;
    }
}
