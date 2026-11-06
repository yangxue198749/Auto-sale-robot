package com.kdx.mp.service;

import com.kdx.vms.model.bean.TMtNode;
import com.kdx.vms.model.bean.TMtVm;
import org.apache.cayenne.ObjectContext;

/**
 * Created by yangshuxi on 2016/12/21.
 */
public interface IMpActivateVmService {
    public TMtVm checkActivated(ObjectContext context, String uniqueCode);

    /**
     * type为0更新地球坐标，type为1更新火星坐标
     * @param context
     * @param mtNode
     * @param latitude
     * @param longitude
     * @param type
     */
    public void updateNodePosition(ObjectContext context, TMtNode mtNode,String latitude,String longitude,Integer type);
}
