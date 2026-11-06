package com.kdx.vms.webapi.thread;

import com.kdx.vms.model.bean.TErpDataSync;
import com.kdx.vms.webapi.Counter;
import com.kdx.vms.webapi.WebApiUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yangshuxi on 2017/1/19.
 */
public class PushDataThread extends PushThread<TErpDataSync> {
    public PushDataThread(String url, List<TErpDataSync> subList, CountDownLatch latch, Object name, Counter counter) {
        super(url, subList, latch, name, counter);
    }

    @Override
    public void run() {
        int i = 0;
        logger.debug("线程" + name + "开始推送数据到ERP");
        try {
            for (TErpDataSync erpDataSync : subList) {
                String result = WebApiUtil.post(erpDataSync.getDataContent(), erpDataSync.getSyncUrl(), erpDataSync.getDataType());
                if (result.contains("TRUE") || result.contains("在当前系统中已经被使用")) {
                    i++;
                    erpDataSync.setSyncMark(2);
                    erpDataSync.setSyncTime(new Date());
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            logger.debug("线程" + name + "完成推送数据到ERP，共" + subList.size() + "条数据，成功" + i + "条");
            counter.plus(i);
            latch.countDown();
        }
    }
}
