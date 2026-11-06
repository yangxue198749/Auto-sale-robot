package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.model.bean.TMtBeneficiaryInfo;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.MtBeneficiaryDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by yangshuxi on 2018/2/26.
 */
@Repository
public class MtBeneficiaryDaoImpl implements MtBeneficiaryDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<?> findBeneficiaryInfo(TSysUser sysUser) {
        try {
            TMtCompany company = sysUser.getCompany();
            Expression qualifier = ExpressionUtils.fillMatchQualifier(null, TMtBeneficiaryInfo.COMPANY_PROPERTY, company);
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtBeneficiaryInfo.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode());
            return sysUser.getObjectContext().performQuery(new SelectQuery(TMtBeneficiaryInfo.class, qualifier));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<?> findBeneficiaryInfoByCompanies(ObjectContext context, List<TMtCompany> companyList) {
        if (!CheckUtils.check(companyList)) return Collections.emptyList();
        try {
            Expression qualifier = ExpressionFactory.inExp(TMtBeneficiaryInfo.COMPANY_PROPERTY, companyList);
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtBeneficiaryInfo.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode());
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtBeneficiaryInfo.ENABLE_MARK_PROPERTY, 1);
            return context.performQuery(new SelectQuery(TMtBeneficiaryInfo.class, qualifier));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<?> findEnabledBeneficiary(ObjectContext context) {
        try {
            Expression qualifier = ExpressionUtils.fillMatchQualifier(null, TMtBeneficiaryInfo.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode());
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtBeneficiaryInfo.ENABLE_MARK_PROPERTY, 1);
            return context.performQuery(new SelectQuery(TMtBeneficiaryInfo.class, qualifier));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

}
