package com.kdx.vms.webapi.thread;

import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.webapi.Counter;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yangshuxi on 2017/1/13.
 */
public class PushSaleThread extends PushThread<TOrderDetail> {


    public PushSaleThread(String url, List<TOrderDetail> subList, CountDownLatch latch, Object name, Counter counter) {
        super(url, subList, latch, name, counter);
    }

    @Override
    public void run() {
//        int i=0;
//        logger.debug("线程"+name+"开始推送销售流水信息到ERP");
//        try {
//            for(TSlOrderMaster slOrderSale:subList){
//                SaleObject saleObject = SaleObject.getInstance(slOrderSale);
//                String json = saleObject.getJson();
//                String result= WebApiUtil.post(json, url,"销售流水");
//                if(result.contains("TRUE") || result.contains("在当前系统中已经被使用")){
//                    i++;
////                    slOrderSale.setSyncMark(2);
////                    slOrderSale.setSyncTime(new Date());
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e.getLocalizedMessage());
//            e.printStackTrace();
//        }finally {
//            logger.debug("线程"+name+"完成推送销售流水信息到ERP，共"+subList.size()+"条数据，成功"+i+"条");
//            counter.plus(i);
//            latch.countDown();
//        }
    }


}
