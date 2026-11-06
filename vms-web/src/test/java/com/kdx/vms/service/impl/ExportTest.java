package com.kdx.vms.service.impl;

import com.kdx.mp.web.excel.ExportExcel;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.custom.OrderSaleVo;
import com.kdx.vms.orm.dao.OrderDetailDao;
import com.kdx.vms.orm.dao.impl.OrderDetailDaoImpl;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/12/6.
 */
public class ExportTest {
    public static void main(String[] args){
        new Thread(() -> {
            try {
                PipedInputStream in = new PipedInputStream();
                PipedOutputStream out = new PipedOutputStream(in);
                new Thread(() -> {
                    try {
                    OrderDetailDao orderDetailDao = new OrderDetailDaoImpl();
                    ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
                    TSysUser sysUser = Cayenne.objectForPK(context, TSysUser.class, 1);
                    List<?> list = orderDetailDao.findSaleOrder(sysUser, null, null, null, null, null, null, null, null, null, null, 100);
                    List<OrderSaleVo> expList = list
                            .stream()
                            .map(TOrderDetail.class::cast)
                            .map(OrderSaleVo::of)
                            .collect(Collectors.toList());
                        new ExportExcel<>("销售流水测试2017-12-06", OrderSaleVo.class).setDataList(expList).write(out).dispose();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                AliyunOssUtils.upload("export/test/销售流水测试2017-12-06", in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
