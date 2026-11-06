package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtSimDao;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import java.util.List;

public class MtSimDaoImplTest {

    @Test
    public void testFindMtSimPage() throws Exception {

    }

    @Test
    public void testFindMtSimPk() throws Exception {

    }

    @Test
    public void testFindMtSim() throws Exception {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        MtSimDao mtSimDao = new MtSimDaoImpl();
        List list = mtSimDao.findMtSim(context,null, "139", null, null, false, null);
        for (Object obj : list) {
            System.out.println(obj);
        }
    }

    @Test
    public void testFindMtSim1() throws Exception {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        MtSimDao mtSimDao = new MtSimDaoImpl();
        System.out.println(mtSimDao.findMtSim(context,null, null, null, null, null, null).size());
        System.out.println(mtSimDao.findMtSim(context, null,"13911111111", null, null, null, null).size());
        System.out.println(mtSimDao.findMtSim(context,null, "13911111111", null, null, null, null).size());
        System.out.println(mtSimDao.findMtSim(context,null, "13911111111", null, null, null, null).size());
        System.out.println(mtSimDao.findMtSim(context,null,"139", null, null, null, null).size());
        System.out.println(mtSimDao.findMtSim(context, null,"1391", null, null, null, null).size());
    }
}