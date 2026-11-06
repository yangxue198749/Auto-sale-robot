package com.kdx.vms.service.impl;

import com.kdx.mp.web.excel.ExportExcel;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.custom.OrderSaleVo;
import com.kdx.vms.orm.dao.OrderDetailDao;
import com.kdx.vms.orm.dao.impl.OrderDetailDaoImpl;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/10/10.
 */
public class SlExp implements Runnable {
    private Date begin;
    private Date end;
    private String beginTime;
    private String endTime;
    private String date;

    public SlExp(Date begin, Date end) {
        this.begin = begin;
        this.end = end;
        this.beginTime = DateUtils.getTime1(begin);
        this.endTime = DateUtils.getTime1(end);
        this.date = beginTime.equals(endTime) ? beginTime : beginTime + "~" + endTime;
        System.out.println("[sl]" + date + "[start]");
    }

    @Override
    public void run() {
        exportOrderList();
        System.out.println("[sl]" + date + "[done]");
    }

    public String exportOrderList() {
        try {
            // 从缓存中得到当前用户TSysUser
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            TSysUser sysUser = Cayenne.objectForPK(context, TSysUser.class, 1);
            OrderDetailDao orderDetailDao = new OrderDetailDaoImpl();
//            List<TOrderDetail> list = orderDetailDao.findVendoutList(sysUser, null, null, null, null, null, null, beginTime, endTime, 100).stream().map(TOrderDetail.class::cast).collect(Collectors.toList());
            List<TOrderDetail> list = orderDetailDao.findSaleOrder(sysUser, null, null, null, null, null, null,null,null, beginTime, endTime, 100).stream().map(TOrderDetail.class::cast).collect(Collectors.toList());
            System.out.println(date + "[size]-->" + list.size());
            List<OrderSaleVo> expList = list.stream().map(OrderSaleVo::of).collect(Collectors.toList());
            String fileName = "销售流水" + date + ".xlsx";
            new ExportExcel<>(null, OrderSaleVo.class).setDataList(expList).writeFile("D:/export/" + fileName).dispose();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
