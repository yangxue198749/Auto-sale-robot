package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TOpenService;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by guoyang on 2018/2/9.
 */
public interface OpenServiceDao {

    TOpenService findById(ObjectContext context, Integer id);
    List<TOpenService> findOpenServiceList(ObjectContext context);
}
