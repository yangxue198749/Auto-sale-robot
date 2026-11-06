package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by yangshuxi on 2018/2/26.
 */
public interface MtBeneficiaryDao {
    /**
     * 因配置生效是通过线下途径，所以不使用关联的方式查询
     *
     * @param sysUser
     * @return
     */
    public List<?> findBeneficiaryInfo(TSysUser sysUser);

    public List<?> findBeneficiaryInfoByCompanies(ObjectContext context,List<TMtCompany> companyList);

    public List<?> findEnabledBeneficiary(ObjectContext context);
}
