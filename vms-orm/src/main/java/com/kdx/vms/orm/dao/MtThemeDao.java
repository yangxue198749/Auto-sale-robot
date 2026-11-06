package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 2017/5/3.
 */
public interface MtThemeDao {

    List findThemesByName(ObjectContext context, String themeName, Integer pageSize);

}
