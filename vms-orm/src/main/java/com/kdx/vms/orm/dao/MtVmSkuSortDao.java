package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVm;

import java.util.List;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtVmSkuSortDao {
    public List<?> findSkuSort(TMtVm mtVm);
}
