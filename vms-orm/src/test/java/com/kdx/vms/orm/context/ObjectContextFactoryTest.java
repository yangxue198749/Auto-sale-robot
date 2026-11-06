package com.kdx.vms.orm.context;

import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.model.bean.TSysUser;
import java.util.Date;

import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

public class ObjectContextFactoryTest {

    @Test
    public void test() throws Exception {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysUser sysUser = context.newObject(TSysUser.class);
        sysUser.setCtime(new Date());
        sysUser.setUserAccount("admin");
        sysUser.setUserPassword(Md5Util.md5("1"));
        sysUser.setDeleteMark(0);
        sysUser.setUserName("admin");
        try {
            context.commitChanges();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
