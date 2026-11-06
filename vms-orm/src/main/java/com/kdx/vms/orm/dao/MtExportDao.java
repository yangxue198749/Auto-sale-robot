package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtExport;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by yangshuxi on 2017/12/6.
 */
public interface MtExportDao {
    public List<?> findEarlyExportData(ObjectContext context);
    public List<?> findEarlyStockData(ObjectContext context);

    public List<?> findExportData(TSysUser sysUser);

    public TMtExport findExportByMatchCondition(ObjectContext context,String condition);
}
