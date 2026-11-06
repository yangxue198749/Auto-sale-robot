package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderDetailVendout;
import com.kdx.vms.model.bean.TOrderMaster;
import org.apache.cayenne.ObjectContext;

/**
 * create by zhouxing on 2017/11/23
 */

public interface VendoutDao {

    TOrderDetail findById(ObjectContext context, String id);

    TOrderMaster findByDetailId(ObjectContext context, Integer detailId);
}