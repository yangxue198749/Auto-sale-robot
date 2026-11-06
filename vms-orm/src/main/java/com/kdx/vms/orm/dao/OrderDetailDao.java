package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderDetailNotify;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by yangshuxi on 2017/11/16.
 */
public interface OrderDetailDao {

    List<?> findSaleOrder(TSysUser sysUser, String innerCode, String orderSeq, String nodeName,
                       String outerSeq, String skuName, Integer status, Integer orderType,
                       Integer payType, String beginDate, String endDate, Integer pageSize);


    List<?> findVendoutList(TSysUser sysUser, String innerCode, String orderSeq, String nodeName,
                         String skuName, Integer outStatus, Integer payType, String beginDate,
                         String endDate, Integer pageSize);
    List<?> findVendoutErrorList(TSysUser sysUser,String beginDate, String endDate, Integer pageSize,String innerCode);

    TOrderDetail findByDetailId(ObjectContext context, Integer detailId);

    TOrderDetailNotify findNotify(ObjectContext context, Integer id);

    List<?> findPayType(ObjectContext context);

    List<?> findTodayOrder(TMtVm mtVm);
}
