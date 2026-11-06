package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

public class MtWarehouseServiceImplTest {

    @Test
    public void test() {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        TMtArea area = Cayenne.objectForPK(context, TMtArea.class, 321283);
        System.out.println(area.getAreaName());
        System.out.println(area.getParentArea().getAreaName());
        TMtArea area2 = Cayenne.objectForPK(context, TMtArea.class, 321282);
        System.out.println(area2.getAreaName());
        System.out.println(area2.getParentArea().getAreaName());
    }

}