package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtBeneficiaryInfo;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2018/2/26.
 */
public interface IMtBeneficiaryService {
    public Map<Integer,TMtBeneficiaryInfo> findPayInfo(TSysUser sysUser);
    public String checkPayInfo(TSysUser sysUser);
    public List<Map<String,String>> findEnabledCompany(ObjectContext context);


}
