package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCardType;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.OrderDetailDao;
import com.kdx.vms.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/11/17.
 */
@Service
public class OrderServiceImpl implements IOrderService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private OrderDetailDao orderDetailDao;


    @Override
    public List findSaleOrder(TSysUser sysUser, Integer searchType, String searchValue, String skuName, Integer status, Integer orderType, Integer payType, String beginTime, String endTime, Integer pageSize) {
        logger.info("findSaleOrder searchType:" + searchType + ", searchValue:" + searchValue + ", skuName:" + skuName + ", beginTime:" + beginTime + ", endTime:" + endTime + ", status:" + status + ", orderType:" + orderType + ", payType:" + payType + ", pageSize:" + pageSize);
        try {
            String innerCode = null;
            Integer orderDetailId = null;
            String nodeName = null;
            String outerSeq = null;
            String orderSeq = null;
            if (null != searchType && !StringUtil.isEmpty(searchValue)) {
                switch (searchType) {
                    case 1:
                        innerCode = searchValue;
                        break;
                    case 2:
                        orderDetailId = StringUtil.getIntValue(searchValue, 0);
                        break;
                    case 3:
                        nodeName = searchValue;
                        break;
                    case 4:
                        outerSeq = searchValue;
                        break;
                    case 5:
                        orderSeq = searchValue;
                        break;
                }
            }
            if (null != orderDetailId) {
                TOrderDetail orderDetail = orderDetailDao.findByDetailId(sysUser.getObjectContext(), orderDetailId);
                List<TOrderDetail> list = new ArrayList<>();
                if(null!=orderDetail) list.add(orderDetail);
                return list;
            }
            return orderDetailDao.findSaleOrder(sysUser, innerCode, orderSeq, nodeName, outerSeq, skuName, status, orderType, payType, beginTime, endTime, pageSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return Collections.emptyList();
    }


    /*@Override
    public List findSaleOrder(TSysUser sysUser, Integer searchType, String searchValue, String skuName, Integer vendoutStatus, Integer payType, String beginTime, String endTime, Integer pageSize) {
        logger.info("findSaleOrder searchType:" + searchType + ", searchValue:" + searchValue + ", skuName:" + skuName + ", beginTime:" + beginTime + ", endTime:" + endTime + ", status:" + vendoutStatus + ", payType:" + payType + ", pageSize:" + pageSize);
        try {
            //Integer orderDetailId = null;
            String innerCode = null;
            String orderSeq = null;
            String nodeName = null;
            String outerSeq = null;
            if (null != searchType && !StringUtil.isEmpty(searchValue)) {
                switch (searchType) {
                    case 1:
                        innerCode = searchValue;
                        break;
                    case 2:
                        orderSeq = searchValue;
                        break;
                    case 3:
                        nodeName = searchValue;
                        break;
                    *//*case 4:
                        outerSeq = searchValue;
                        break;
                    case 5:
                        orderDetailId = StringUtil.getIntValue(searchValue, 0);
                        break;*//*
                }
            }
           *//* if (null != orderDetailId) {
                TOrderDetail orderDetail = orderDetailDao.findByDetailId(sysUser.getObjectContext(), orderDetailId);
                List<TOrderDetail> list = new ArrayList<>();
                list.add(orderDetail);
                return list;
            }*//*
            return orderDetailDao.findSaleOrder(sysUser, innerCode, orderSeq, nodeName, skuName, vendoutStatus, payType, beginTime, endTime, pageSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return Collections.emptyList();
    }*/

    @Override
    public List findVendoutList(TSysUser sysUser, Integer searchType, String searchValue, String skuName, Integer payType, Integer outStatus, String beginTime, String endTime, Integer pageSize) {
        logger.info("findVendoutList searchType:" + searchType + ", searchValue:" + searchValue + ", skuName:" + skuName + ", beginTime:" + beginTime + ", endTime:" + endTime + ", outStatus:" + outStatus + ", payType:" + payType + ", pageSize:" + pageSize);
        try {
            Integer orderDetailId = null;
            String innerCode = null;
            String orderSeq = null;
            String nodeName = null;
            if (null != searchType && !StringUtil.isEmpty(searchValue)) {
                switch (searchType) {
                    case 1:
                        innerCode = searchValue;
                        break;
                    case 2:
                        orderSeq = searchValue;
                        break;
                    case 3:
                        nodeName = searchValue;
                        break;
                }
            }
            if (null != orderDetailId) {
                TOrderDetail orderDetail = orderDetailDao.findByDetailId(sysUser.getObjectContext(), orderDetailId);
                List<TOrderDetail> list = new ArrayList<>();
                list.add(orderDetail);
                return list;
            }
            return orderDetailDao.findVendoutList(sysUser, innerCode, orderSeq, nodeName, skuName, outStatus, payType, beginTime, endTime, pageSize);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<?> findVendoutErrorList(TSysUser sysUser, String beginTime, String endTime, Integer pageSize,String innerCode) {
        return orderDetailDao.findVendoutErrorList(sysUser, beginTime, endTime, pageSize,innerCode);
    }

    @Override
    public Map<Integer, String> findPayType(TSysUser sysUser) {
        List<TMtCardType> list = this.findPayTypeList(sysUser);
        Map<Integer, String> map = new HashMap<>();
        list.forEach(cardType -> map.put(cardType.getTypeCode(), cardType.getTypeName()));
        return map;
    }

    @Override
    public List<TMtCardType> findPayTypeList(TSysUser sysUser) {
        return orderDetailDao.findPayType(sysUser.getObjectContext()).stream().map(TMtCardType.class::cast).collect(Collectors.toList());

    }

}
