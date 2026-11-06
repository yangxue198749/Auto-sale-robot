package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtExport;
import org.apache.cayenne.ObjectContext;

/**
 * Created by guoyang on 2018/1/20.
 */
public interface IMtExportService {
    TMtExport findExportByMatchCondition(ObjectContext context,String condition);
}
