package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtExport;
import com.kdx.vms.orm.dao.MtExportDao;
import com.kdx.vms.service.IMtExportService;
import org.apache.cayenne.ObjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by guoyang on 2018/1/20.
 */
@Service
public class MtExportServiceImpl implements IMtExportService {
    @Autowired
    private MtExportDao mtExportDao;
    @Override
    public TMtExport findExportByMatchCondition(ObjectContext context, String condition) {
        return mtExportDao.findExportByMatchCondition(context,condition);
    }
}
