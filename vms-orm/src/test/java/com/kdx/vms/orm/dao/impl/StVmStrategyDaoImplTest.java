package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Test;

public class StVmStrategyDaoImplTest {

    @Test
    public void testFindStVmStrategy() throws Exception {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
//        new StVmStrategyDaoImpl().findStVmStrategy(context, 1, TStVmStrategy.TYPE_ST_VM_CHANNEL);

    }

    @Test
    public void test() throws Exception {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        SelectQuery select = new SelectQuery(TSysUser.class);
        context.performQuery(select);
        System.out.println("----------------------------");
        System.out.println(Cayenne.objectForPK(context, TSysUser.class, 237));
        System.out.println(Cayenne.objectForPK(context, TSysUser.class, 237));

    }

    @Test
    public void test1() throws Exception {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TStVmStrategy tStVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, 1140);

        System.out.println(tStVmStrategy.getStSkuSort().size());
    }
}