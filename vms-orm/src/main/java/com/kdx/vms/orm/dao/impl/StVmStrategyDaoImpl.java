package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TStVmText;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.StVmStrategyDao;

/**
 *
 * Created by liyang on 16-11-29.
 */
@Service("stVmStrategyDao")
public class StVmStrategyDaoImpl implements StVmStrategyDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TStVmStrategy findTStVmStrategyPk(ObjectContext context, Integer pk) {
        logger.debug("context:" + context + ", pk:" + pk);
        if (null == context || null == pk) {
            return null;
        }
        try {
            return Cayenne.objectForPK(context, TStVmStrategy.class, pk);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findShortcutsStrategy(ObjectContext context, TMtCompany company, String strategyName, Integer modelId, Integer pageSize) {
        logger.debug("context:" + context + ", company:" + company + ", strategyName:" + strategyName + ", modelId:" + modelId + ", pageSize:" + pageSize);
        try {
            Expression expression = ExpressionFactory.matchExp(TStVmStrategy.STRATEGY_TYPE_PROPERTY, TStVmStrategy.TYPE_ST_VM_SHORTCUTS);
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TStVmStrategy.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TStVmStrategy.COMPANY_PROPERTY, company));
                    expression = expression.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    expression = expression.andExp(ExpressionFactory.matchExp(TStVmStrategy.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            if (!StringUtil.isEmpty(strategyName))
                expression = expression.andExp(ExpressionFactory.likeExp(TStVmStrategy.STRATEGY_NAME_PROPERTY, "%" + strategyName + "%"));
            if (null != modelId && modelId > 0)
                expression = expression.andExp(ExpressionFactory.matchExp(TStVmStrategy.VM_MODEL_PROPERTY, modelId));
            SelectQuery selectQuery = new SelectQuery(TStVmStrategy.class, expression);
            selectQuery.setPageSize(pageSize);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findStVmStrategy(ObjectContext context, TMtCompany company, Integer strategyType,String strategyName,Integer pageSize) {
        if (null == context || null == company || null == strategyType) {
            return null;
        }
        logger.debug("context:" + context + ", companyId:" + company.getId() + ", strategyType:" + strategyType);
        try {
            Expression qualifier = ExpressionFactory.matchExp(TStVmStrategy.STRATEGY_TYPE_PROPERTY, strategyType);
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
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmStrategy.STRATEGY_NAME_PROPERTY,"%"+strategyName.trim()+"%"));
            }
            SelectQuery select = new SelectQuery(TStVmStrategy.class, qualifier);
            // 根据strategyType预加载关联数据
            switch (strategyType) {
                case TStVmStrategy.TYPE_ST_VM_CHANNEL:
                    select.addPrefetch(TStVmStrategy.ST_CHANNEL_PROPERTY);
                    break;
                case TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE:
                    select.addPrefetch(TStVmStrategy.ST_ENERGY_SAVE_PROPERTY);
                    break;
                case TStVmStrategy.TYPE_ST_VM_EVENT:
                    select.addPrefetch(TStVmStrategy.ST_EVENT_PROPERTY);
                    break;
                case TStVmStrategy.TYPE_ST_VM_PRICE:
                    select.addPrefetch(TStVmStrategy.ST_PRICE_PROPERTY);
                    break;
                case TStVmStrategy.TYPE_ST_VM_SHORTCUTS:
                    select.addPrefetch(TStVmStrategy.ST_SHORTCUTS_PROPERTY);
                    break;
                case TStVmStrategy.TYPE_ST_VM_SKU_SORT:
                    select.addPrefetch(TStVmStrategy.ST_SKU_SORT_PROPERTY);
                    break;
                case TStVmStrategy.TYPE_ST_VM_TEXT:
                    select.addPrefetch(TStVmStrategy.ST_TEXT_PROPERTY);
                    break;
            }
            select.setPageSize(pageSize);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

	@Override
	public List<TStVmStrategy> findStVmTextPage(ObjectContext context,
			TSysUser user, String stVmName, String text, Integer pageSize) {
		try {
			 Expression qualifier = ExpressionFactory.matchExp(TStVmStrategy.STRATEGY_TYPE_PROPERTY, TStVmStrategy.TYPE_ST_VM_TEXT);
			 TMtCompany company = user.getCompany();
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
			 if(!StringUtil.isEmpty(stVmName)){
				 qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmStrategy.STRATEGY_NAME_PROPERTY,"%"+stVmName.trim()+"%"));
			 }
			 if(!StringUtil.isEmpty(text)){
				 qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmStrategy.ST_TEXT_PROPERTY+"."+ TStVmText.TEXT_CONTENTS_PROPERTY,"%"+text.trim()+"%"));
			 }
			 SelectQuery select = new SelectQuery(TStVmStrategy.class, qualifier);
			 select.setPageSize(pageSize);
			 return context.performQuery(select);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public List<TStVmStrategy> findStVmEnergyPage(ObjectContext context,
			TSysUser user, String stVmName, Integer vmModelId, Integer pageSize) {
		try {
			 Expression qualifier = ExpressionFactory.matchExp(TStVmStrategy.STRATEGY_TYPE_PROPERTY, TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE);
			 TMtCompany company = user.getCompany();
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
			 if(!StringUtil.isEmpty(stVmName)){
				 qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmStrategy.STRATEGY_NAME_PROPERTY,"%"+stVmName.trim()+"%"));
			 }
			 if(null != vmModelId){
				 qualifier = qualifier.andExp(ExpressionFactory.matchExp(TStVmStrategy.VM_MODEL_PROPERTY,vmModelId));
			 }
			 SelectQuery select = new SelectQuery(TStVmStrategy.class, qualifier);
			 select.setPageSize(pageSize);
			 return context.performQuery(select);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public List findStVmChannelPage(ObjectContext context,
			TSysUser user, String stVmName, Integer vmModelId,
			Integer pageSize) {
		try {
			 Expression qualifier = ExpressionFactory.matchExp(TStVmStrategy.STRATEGY_TYPE_PROPERTY, TStVmStrategy.TYPE_ST_VM_CHANNEL);
			 TMtCompany company = user.getCompany();
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
			 if(!StringUtil.isEmpty(stVmName)){
				 qualifier = qualifier.andExp(ExpressionFactory.likeExp(TStVmStrategy.STRATEGY_NAME_PROPERTY,"%"+stVmName.trim()+"%"));
			 }
			 if(null != vmModelId){
				 qualifier = qualifier.andExp(ExpressionFactory.matchExp(TStVmStrategy.VM_MODEL_PROPERTY,vmModelId));
			 }
			 SelectQuery select = new SelectQuery(TStVmStrategy.class, qualifier);
			 select.setPageSize(pageSize);
			 return context.performQuery(select);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}
}