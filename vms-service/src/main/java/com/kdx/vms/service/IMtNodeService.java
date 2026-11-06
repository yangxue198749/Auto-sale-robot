package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtNode;
import com.kdx.vms.model.bean.TMtPartner;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/11/28.
 */
public interface IMtNodeService {
    public Map<String, Object> saveNode(TSysUser currentUser,ObjectContext context, TMtNode mtNode,Integer partnerId,Integer deliveryId,Integer repairId);
    public Map<String, Object> saveNode(TSysUser currentUser,ObjectContext context,TMtNode mtNode,Integer nodeId,Integer partnerId,Integer deliveryId,Integer repairId);
    public Map<String, Object> deleteNode(TSysUser currentUser,ObjectContext context,Integer[] nodeIds);
    public Map<String, Object>  setDelivery(TSysUser currentUser,ObjectContext context, TSysUser delivery, Integer[] nodeIds);
    public Map<String, Object>  setRepair(TSysUser currentUser,ObjectContext context,TSysUser repair,Integer[] nodeIds);
    public TMtNode findNode(ObjectContext context,Integer nodeId);
    public List findNodeList(TSysUser currentUser,ObjectContext context,String searchType,String searchValue,Integer province,Integer city,Integer area,Integer businessDistrictId,Integer pageSize);
}
