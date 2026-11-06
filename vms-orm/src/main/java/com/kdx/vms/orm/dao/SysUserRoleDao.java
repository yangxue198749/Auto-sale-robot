package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 16-11-28.
 */
public interface SysUserRoleDao {

    public List getDelivery(ObjectContext context, boolean relationships);

}
