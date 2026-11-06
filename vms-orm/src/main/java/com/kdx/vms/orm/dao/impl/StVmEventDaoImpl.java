package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TStVmEvent;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.orm.dao.StVmEventDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/1.
 */
@Repository
public class StVmEventDaoImpl implements StVmEventDao{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public List findVmEventStrategy(ObjectContext context, TMtCompany company, String discountType, String strategyName, Integer pageSize) {
        if (null == context || null == company ) {
            return null;
        }
        logger.debug("context:" + context + ", companyId:" + company.getId());

        try {
            Expression qualifier = ExpressionFactory.matchExp(TStVmEvent.EVENT_TYPE_PROPERTY,TStVmEvent.EVENT_TYPE_VM);
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TStVmEvent.STRATEGY_PROPERTY+"."+TStVmStrategy.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TStVmEvent.STRATEGY_PROPERTY+"."+TStVmStrategy.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TStVmEvent.STRATEGY_PROPERTY+"."+TStVmStrategy.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            if(!StringUtil.isEmpty(strategyName)){
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmEvent.STRATEGY_PROPERTY+"."+TStVmStrategy.STRATEGY_NAME_PROPERTY,"%"+strategyName+"%"));
            }
            if(!StringUtil.isEmpty(discountType)){
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TStVmEvent.DISCOUNT_TYPE_PROPERTY,discountType));

            }
            SelectQuery select = new SelectQuery(TStVmEvent.class, qualifier);
            select.addPrefetch(TStVmEvent.STRATEGY_PROPERTY);
            select.setPageSize(pageSize);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findSkuEventStrategy(ObjectContext context, TMtCompany company,String strategyName, Integer pageSize) {
        if (null == context || null == company ) {
            return null;
        }
        logger.debug("context:" + context + ", companyId:" + company.getId());

        try {
            Expression qualifier = ExpressionFactory.matchExp(TStVmStrategy.ST_EVENT_PROPERTY+"."+ TStVmEvent.EVENT_TYPE_PROPERTY,TStVmEvent.EVENT_TYPE_SKU);
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TStVmStrategy.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TStVmStrategy.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TStVmStrategy.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
           if(!StringUtil.isEmpty(strategyName)){
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmStrategy.STRATEGY_NAME_PROPERTY,"%"+strategyName+"%"));
            }
            SelectQuery select = new SelectQuery(TStVmStrategy.class, qualifier);
            select.addPrefetch(TStVmStrategy.ST_EVENT_PROPERTY);
            select.setPageSize(pageSize);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
