package com.kdx.mp.service;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.VmStockInfo;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/28.
 */
public interface IMpVmStatusService {
    public List findVmList(TSysUser currentUser,String innerCode);
    public Map<String,String> getFaultTime(ObjectContext context, TMtVm mtVm);
    public List<VmStockInfo> findStockList(ObjectContext context, TMtVm mtVm);
}
