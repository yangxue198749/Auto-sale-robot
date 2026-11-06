package com.kdx.vms.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/9.
 */
public interface IMtVmTextService {
    public Map<String, Object> saveText(TSysUser currentUser, ObjectContext context, String text,Integer[] ids,Date beginDate,Date endDate);
    public Map<String, Object> saveText(TSysUser currentUser, ObjectContext context, Integer strategyId,Integer[] ids,Date beginDate,Date endDate);
}
