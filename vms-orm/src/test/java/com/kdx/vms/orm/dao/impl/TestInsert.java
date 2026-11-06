package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TStVmChannel;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 * Created by yangshuxi on 2016/12/2.
 */
public class TestInsert implements Runnable{
    ObjectContext context= ObjectContextFactory.getInstance().newObjectContext();
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            TStVmChannel stVmChannel = context.newObject(TStVmChannel.class);
            stVmChannel.setCtime(new Date());
            stVmChannel.setSkuId(i);
            context.commitChanges();
        }
    }
}
