package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TOpenService;
import com.kdx.vms.orm.dao.OpenServiceDao;
import com.kdx.vms.service.IOpenService;
import org.apache.cayenne.ObjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by guoyang on 2018/3/17.
 */
@Service
public class OpenServiceImpl implements IOpenService {

    @Autowired
    private OpenServiceDao openServiceDao;
    @Override
    public List<TOpenService> findOpenServiceList(ObjectContext context) {
        return openServiceDao.findOpenServiceList(context);
    }
}
