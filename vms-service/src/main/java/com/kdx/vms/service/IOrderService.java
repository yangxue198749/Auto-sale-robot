package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtCardType;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/11/17.
 */
public interface IOrderService {
    //List findSaleOrder(TSysUser sysUser,Integer searchType, String searchValue, String skuName, Integer outStatus, Integer payType, String beginTime, String endTime, Integer pageSize);

    List findSaleOrder(TSysUser sysUser,Integer searchType, String searchValue, String skuName, Integer status, Integer orderType, Integer payType, String beginTime, String endTime, Integer pageSize);

    List findVendoutList(TSysUser sysUser,Integer searchType, String searchValue, String skuName, Integer payType, Integer outStatus, String beginTime, String endTime, Integer pageSize);

    List<?> findVendoutErrorList(TSysUser sysUser,String beginTime, String endTime, Integer pageSize,String innerCode);

    Map<Integer,String> findPayType(TSysUser sysUser);
    List<TMtCardType> findPayTypeList(TSysUser sysUser);
}
