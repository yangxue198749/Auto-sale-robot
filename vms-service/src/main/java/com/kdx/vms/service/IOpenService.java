package com.kdx.vms.service;

import com.kdx.vms.model.bean.TOpenService;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by guoyang on 2018/3/17.
 */
public interface IOpenService {
    List<TOpenService> findOpenServiceList(ObjectContext context);
}
