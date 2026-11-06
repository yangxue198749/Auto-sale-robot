package com.kdx.vms.service;

import com.kdx.vms.model.bean.TOrderDetailVendout;
import com.kdx.vms.model.bean.TOrderMaster;
import org.apache.cayenne.ObjectContext;

import java.util.Map;

/**
 * create by zhouxing on 2017/11/23
 */
public interface VendoutService {

    Map<String, Object> updateMemoById(ObjectContext context, String vendoutId, String memo);

    TOrderMaster findByDetailId(ObjectContext context, Integer detailId);

}