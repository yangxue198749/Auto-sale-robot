package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtNodeDao;
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
 * 
 * Created by yangshuxi on 2016/11/28.
 */
@Repository
public class MtNodeDaoImpl implements MtNodeDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List getMtNodeList(ObjectContext context, String deliveryName, String repairName, String partnerName, String nodeName, String propertyName, TMtCompany company, Integer province,Integer city,Integer area, Integer businessDistrictId, Integer pageSize) {
        Expression qualifier =  ExpressionFactory.expTrue();
        switch (company.getCompanyLevel()) {
            case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                break;
            case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                Expression companyExp = ExpressionFactory.inExp(TMtNode.COMPANY_PROPERTY, company.getChildCompany());
                companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtNode.COMPANY_PROPERTY, company));
                qualifier = qualifier.andExp(companyExp);
                break;
            case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtNode.COMPANY_PROPERTY, company));
                break;
            default:
                return null;
        }
        if(!StringUtil.isEmpty(deliveryName)){
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtNode.DELIVERY_PROPERTY+"." + TSysUser.USER_NAME_PROPERTY,"%"+deliveryName+"%"));
        }
        if(!StringUtil.isEmpty(repairName)){
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtNode.REPAIR_PROPERTY+"." + TSysUser.USER_NAME_PROPERTY,"%"+repairName+"%"));
        }
        if(!StringUtil.isEmpty(propertyName)){
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtNode.PROPERTY_NAME_PROPERTY,"%"+propertyName+"%"));
        }
        if(!StringUtil.isEmpty(nodeName)){
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtNode.NODE_NAME_PROPERTY,"%"+nodeName+"%"));
        }
        if(null != province){
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtNode.PROVINCE_ID_PROPERTY,province));
        }
        if(null != city){
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtNode.CITY_ID_PROPERTY,city));
        }
        if(null != area){
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtNode.AREA_ID_PROPERTY,area));
        }
        if(null!=businessDistrictId&&businessDistrictId>0){
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtNode.BUSINESS_DISTRICT_ID_PROPERTY,businessDistrictId));
        }
        SelectQuery select = new SelectQuery(TMtNode.class, qualifier);
        select.setPageSize(pageSize);
        select.addPrefetch(TMtNode.DELIVERY_PROPERTY);
        select.addPrefetch(TMtNode.REPAIR_PROPERTY);
        select.addPrefetch(TMtNode.COMPANY_PROPERTY);
        List list = context.performQuery(select);
        return list;
    }
    
	@Override
	public List<TMtNode> getMtNodeList(ObjectContext context, List<?> cityIdList, String addressName) {
		Expression qualifier = ExpressionFactory.inExp(TMtNode.CITY_ID_PROPERTY,cityIdList);
		qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtNode.NODE_ADDRESS_PROPERTY, "%"+addressName+"%"));
		SelectQuery select = new SelectQuery(TMtNode.class, qualifier);
        return context.performQuery(select);
	}


    @Override
    public List getMtNodeList(ObjectContext context, TMtCompany company, String startTime, String endTime) {
        Expression qualifier = ExpressionFactory.expTrue();
        Integer level = company.getCompanyLevel();
        switch (level) {
            case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                break;
            case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                Expression companyExp = ExpressionFactory.inExp(TMtNode.COMPANY_PROPERTY, company.getChildCompany());
                companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtNode.COMPANY_PROPERTY, company));
                qualifier = qualifier.andExp(companyExp);
                break;
            case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtNode.COMPANY_PROPERTY, company));
                break;
        }
        if(!StringUtil.isEmpty(startTime)){
            qualifier = qualifier.andExp(ExpressionFactory.greaterOrEqualExp(TMtNode.CTIME_PROPERTY, StringUtil.strToDate(startTime + " 00:00:00", StringUtil.Pattern)));
        }
        if(!StringUtil.isEmpty(endTime)){
            qualifier = qualifier.andExp(ExpressionFactory.lessOrEqualExp(TMtNode.CTIME_PROPERTY, StringUtil.strToDate(endTime + " 23:59:59", StringUtil.Pattern)));
        }
        SelectQuery select = new SelectQuery(TMtNode.class, qualifier);
        return context.performQuery(select);
    }

	@Override
	public List<TMtNode> getMtNodeList(ObjectContext context, List<?> cityIdList, List<?> businessIds) {
		SelectQuery select = new SelectQuery(TMtNode.class);
		if(cityIdList != null && cityIdList.size() > 0){
			select.andQualifier(ExpressionFactory.inExp(TMtNode.CITY_ID_PROPERTY, cityIdList));
		}
		if(businessIds != null && businessIds.size() >0){
			select.andQualifier(ExpressionFactory.inExp(TMtNode.BUSINESS_DISTRICT_ID_PROPERTY, businessIds));
		}
		select.addPrefetch(TMtNode.VM_PROPERTY);
		return context.performQuery(select);
	}

    @Override
    public List getAllNode(ObjectContext context, boolean prefetch) {
        logger.debug("context:" + context);
        if (null == context) return null;
        try {
            SelectQuery selectQuery = new SelectQuery(TMtNode.class);
            if (prefetch) selectQuery.addPrefetch(TMtNode.COMPANY_PROPERTY);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }
}
