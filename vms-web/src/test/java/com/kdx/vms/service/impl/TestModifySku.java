package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

/**
 * Created by yangshuxi on 2017/4/7.
 */
public class TestModifySku {
    public static void main(String[] args) {
        ObjectContext context1 = ObjectContextFactory.getInstance().newObjectContext();
        ObjectContext context2 = ObjectContextFactory.getInstance().newObjectContext();
        ObjectContext context3 = ObjectContextFactory.getInstance().newObjectContext();
        TMtSku mtSku1 = Cayenne.objectForPK(context1,TMtSku.class,2);
        System.out.println("base================"+mtSku1);
        TMtSku mtSku2 = Cayenne.objectForPK(context2,TMtSku.class,2);
        mtSku2.setSkuColor("红色51");
        TMtSku mtSku3 = Cayenne.objectForPK(context3,TMtSku.class,2);
        mtSku3.setSkuColor("绿色51");
        context3.commitChanges();
        System.out.println("modify================"+mtSku2);
        context2.rollbackChanges();
        mtSku2 = Cayenne.objectForPK(context2,TMtSku.class,2);
        System.out.println("research================"+mtSku2);
    }
}
