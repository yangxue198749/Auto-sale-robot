package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.orm.dao.*;
import com.kdx.vms.service.IWorkbenchService;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/16.
 */
@Service
public class WorkbenchServiceImpl implements IWorkbenchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TaskVmDao taskVmDao;
    @Autowired
    private MtNodeDao mtNodeDao;
    @Autowired
    private MtVmDao mtVmDao;
    @Autowired
    private MtVmEventMasterDao mtVmEventMasterDao;
    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public Map<Long, Integer> findDeliveryTaskList(ObjectContext context, TSysUser currentUser) {
        List<TTaskVm> deliveryTaskList = taskVmDao.findPage(context, currentUser, null, null, null, DateUtils.getTime1(), DateUtils.getTime1(), TTaskVm.TYPE_DELIVERY, null);
        List<TTaskVm> list1 = new ArrayList<>();
        List<TTaskVm> list2 = new ArrayList<>();
        List<TTaskVm> list3 = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();
        for (TTaskVm tTaskVm : deliveryTaskList) {
            if (tTaskVm.getTaskVmStatus() == null) continue;
            switch (tTaskVm.getTaskVmStatus()) {
                case TTaskVm.STATUS_INITIALIZATION:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_ONGOING:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_FINISHED:
                    list2.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_CLOSED:
                    list3.add(tTaskVm);
                    break;
            }
        }
        map.put((long) TTaskVm.STATUS_INITIALIZATION, list1.size());
        map.put((long) TTaskVm.STATUS_FINISHED, list2.size());
        map.put((long) TTaskVm.STATUS_CLOSED, list3.size());

        return map;
    }

    @Override
    public Map<Long, Integer> findRepairTaskList(ObjectContext context, TSysUser currentUser) {
        List<TTaskVm> deliveryTaskList = taskVmDao.findPage(context, currentUser, null, null, null, DateUtils.getTime1(), DateUtils.getTime1(), TTaskVm.TYPE_REPAIR, null);
        List<TTaskVm> list1 = new ArrayList<>();
        List<TTaskVm> list2 = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();
        for (TTaskVm tTaskVm : deliveryTaskList) {
            if (tTaskVm.getTaskVmStatus() == null) continue;
            switch (tTaskVm.getTaskVmStatus()) {
                case TTaskVm.STATUS_INITIALIZATION:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_ONGOING:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_FINISHED:
                    list2.add(tTaskVm);
                    break;
            }
        }
        map.put((long) TTaskVm.STATUS_INITIALIZATION, list1.size());
        map.put((long) TTaskVm.STATUS_FINISHED, list2.size());
        return map;
    }

    @Override
    public Map<Long, Integer> findDeployTaskList(ObjectContext context, TSysUser currentUser) {
        List<TTaskVm> deliveryTaskList = taskVmDao.findPage(context, currentUser, null, null, null, DateUtils.getTime1(), DateUtils.getTime1(), TTaskVm.TYPE_DEPLOY, null);
        List<TTaskVm> list1 = new ArrayList<>();
        List<TTaskVm> list2 = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();
        for (TTaskVm tTaskVm : deliveryTaskList) {
            if (tTaskVm.getTaskVmStatus() == null) continue;
            switch (tTaskVm.getTaskVmStatus()) {
                case TTaskVm.STATUS_INITIALIZATION:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_ONGOING:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_FINISHED:
                    list2.add(tTaskVm);
                    break;
            }
        }
        map.put((long) TTaskVm.STATUS_INITIALIZATION, list1.size());
        map.put((long) TTaskVm.STATUS_FINISHED, list2.size());
        return map;
    }

    @Override
    public Map<Long, Integer> findWithdrawTaskList(ObjectContext context, TSysUser currentUser) {
        List<TTaskVm> deliveryTaskList = taskVmDao.findPage(context, currentUser, null, null, null, DateUtils.getTime1(), DateUtils.getTime1(), TTaskVm.TYPE_WITHDRAW, null);
        List<TTaskVm> list1 = new ArrayList<>();
        List<TTaskVm> list2 = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();
        for (TTaskVm tTaskVm : deliveryTaskList) {
            if (tTaskVm.getTaskVmStatus() == null) continue;
            switch (tTaskVm.getTaskVmStatus()) {
                case TTaskVm.STATUS_INITIALIZATION:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_ONGOING:
                    list1.add(tTaskVm);
                    break;
                case TTaskVm.STATUS_FINISHED:
                    list2.add(tTaskVm);
                    break;
            }
        }
        map.put((long) TTaskVm.STATUS_INITIALIZATION, list1.size());
        map.put((long) TTaskVm.STATUS_FINISHED, list2.size());
        return map;
    }

    @Override
    public Integer findNewNode(ObjectContext context, TSysUser currentUser) {
        List list = mtNodeDao.getMtNodeList(context, currentUser.getCompany(), DateUtils.getTime1(), DateUtils.getTime1());
        return null == list ? 0 : list.size();
    }

    @Override
    public Integer findNewVm(ObjectContext context, TSysUser currentUser) {
        List list = mtVmDao.findVmByTime(context, currentUser.getCompany(), DateUtils.getTime1(), DateUtils.getTime1());
        return null == list ? 0 : list.size();
    }

    @Override
    public Integer findEventVm(ObjectContext context, TSysUser currentUser) {
        List list = mtVmEventMasterDao.findEventVmList(context, currentUser.getCompany(), DateUtils.getTime1());
        return list.size();
    }

    @Override
    public Integer findOutOfStockVm(ObjectContext context, TSysUser currentUser) {
        List list = mtVmDao.findOnSaleVm(context, currentUser.getCompany());
        int number = 0;
        for (Object o : list) {
            if (((TMtVm) o).getStockStatus() > 0) {
                number++;
            }
        }
        return number;
    }

    @Override
    public Integer findSalesVolume(ObjectContext context, TSysUser currentUser) {
        List<?> list = orderDetailDao.findVendoutList(currentUser, null, null, null, null, 1, null, DateUtils.getTime1(), DateUtils.getTime1(), 100);
        return list.size();
    }

    @Override
    public String findSalesAmount(ObjectContext context, TSysUser currentUser) {
        try {
            List<?> list = orderDetailDao.findVendoutList(currentUser, null, null, null, null, 1, null, DateUtils.getTime1(), DateUtils.getTime1(), 100);
            Integer sum = list.parallelStream()
                    .map(TOrderDetail.class::cast)
                    .mapToInt(TOrderDetail::getCostMoney)
                    .sum();
            return StringUtil.formatAmount(sum);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "0.00";
    }

}
