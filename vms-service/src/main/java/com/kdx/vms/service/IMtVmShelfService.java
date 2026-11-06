package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.ShelfConfigCache;

/**
 * Created by yangshuxi on 2018/2/7.
 */
public interface IMtVmShelfService {
    public ShelfConfigCache getShelfCache(TSysUser sysUser, TMtVm mtVm);
    public void deleteShelfCache(Integer vmId);
    public void updateShelfCache(ShelfConfigCache cache);

    public String makeShelfTable(TMtVm mtVm,ShelfConfigCache cache);
    public void saveShelf(TMtVm mtVm,ShelfConfigCache cache);


}
