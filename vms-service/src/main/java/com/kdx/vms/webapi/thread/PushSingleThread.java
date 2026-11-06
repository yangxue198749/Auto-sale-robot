package com.kdx.vms.webapi.thread;

import com.kdx.vms.model.bean.TErpDataSync;
import com.kdx.vms.webapi.WebApiUtil;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by yangshuxi on 2017/1/19.
 */
public class PushSingleThread implements Runnable{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private TErpDataSync[] erpDataSyncs;
    private ObjectContext context;
    private PushSingleThread() {
    }

    public PushSingleThread( ObjectContext context,TErpDataSync... erpDataSyncs) {
        this.erpDataSyncs = erpDataSyncs;
        this.context = context;
    }

    @Override
    public void run() {
        if(null!=erpDataSyncs){
            try {
                for(TErpDataSync erpDataSync:erpDataSyncs){
                    String result= WebApiUtil.post(erpDataSync.getDataContent(),erpDataSync.getSyncUrl(),erpDataSync.getDataType());
                    if(result.contains("TRUE")){
                        erpDataSync.setSyncMark(TErpDataSync.SYNC_TRUE);
                        erpDataSync.setSyncTime(new Date());
                    }
                }
                context.commitChanges();
            } catch (IOException e) {
                context.rollbackChanges();
                e.printStackTrace();
            }
        }
    }


}
