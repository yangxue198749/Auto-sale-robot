package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtCompanyDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MtCompanyDaoImpl implements MtCompanyDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<Object> findCompanyList(String companyName ,String customerName ,String adminName ,Integer unusedLevel  ,Integer twoLevelCompanyId ,Integer pageSize ,
			ObjectContext context) {
		Expression expression = ExpressionFactory.expTrue();
		if(StringUtils.isNotEmpty(companyName)){
			expression = expression.andExp(ExpressionFactory.likeExp(TMtCompany.COMPANY_NAME_PROPERTY, "%"+companyName+"%"));
		}
		if(StringUtils.isNotEmpty(customerName)){
			expression = expression.andExp(ExpressionFactory.likeExp(TMtCompany.CUSTOMER_PROPERTY + "." + TMtCustomer.CUSTOMER_NAME_PROPERTY, "%"+customerName+"%"));
		}
		if(StringUtils.isNotEmpty(adminName)){
			expression = expression.andExp(ExpressionFactory.likeExp(TMtCompany.USER_PROPERTY + "." + TSysUser.USER_NAME_PROPERTY, "%"+adminName+"%"));
		}
		if(unusedLevel != null){
			expression = expression.andExp(ExpressionFactory.notInExp(TMtCompany.COMPANY_LEVEL_PROPERTY, unusedLevel));
		}
		if(twoLevelCompanyId != null){
			expression = expression.andExp(ExpressionFactory.matchExp(TMtCompany.PARENT_COMPANY_PROPERTY, twoLevelCompanyId));
		}
		SelectQuery select = new SelectQuery(TMtCompany.class,expression);
		select.setPageSize(pageSize);
		select.addOrdering(TMtCompany.CTIME_PROPERTY, SortOrder.DESCENDING);
		select.addPrefetch(TMtCompany.CUSTOMER_PROPERTY);
		return context.performQuery(select);
	}

    public TSysUser findAdminSysUserByCompany(ObjectContext context, Integer companyId ,Integer level) {
        logger.debug("context:" + context + ", companyId:" + companyId);
        if (null == context || null == companyId) return null;
        try {
        	 SelectQuery query = new SelectQuery(TSysUser.class);
        	 query.andQualifier(ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, companyId));
        	 query.andQualifier(ExpressionFactory.matchExp(TSysUser.USER_ROLE_PROPERTY+"."+TSysUserRole.ROLE_PROPERTY+"."+TSysRole.ROLE_LEVEL_PROPERTY, level));
        	 if(level.equals(1)){
        		 query.andQualifier(ExpressionFactory.matchExp(TSysUser.USER_ROLE_PROPERTY+"."+TSysUserRole.ROLE_PROPERTY+"."+TSysRole.ROLE_TYPE_PROPERTY, TSysRole.ROLE_TYPE_SYSTEM));
        	 }else{
        		 query.andQualifier(ExpressionFactory.matchExp(TSysUser.USER_ROLE_PROPERTY+"."+TSysUserRole.ROLE_PROPERTY+"."+TSysRole.ROLE_TYPE_PROPERTY, TSysRole.ROLE_TYPE_PUBLIC));
        	 }
        	 List<TSysUser> list = context.performQuery(query);
        	 if(list != null && list.size() > 0) return list.get(0);
//            Expression qualifier = ExpressionFactory.matchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN); // 获取最高权限：管理员
//            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TSysRole.COMPANY_PROPERTY, companyId));
//            SelectQuery select = new SelectQuery(TSysRole.class, qualifier);
//            List list = context.performQuery(select);
//            TSysRole tSysRole = null == list || list.size() == 0 ? null : (TSysRole) list.get(0);
//            if (null == tSysRole) return null;
//            List<TSysUserRole> tSysUserRoles = tSysRole.getUserRole();
//            TSysUserRole sysUserRole = null == tSysUserRoles || tSysUserRoles.size() == 0 ? null : tSysUserRoles.get(0);
//            if (null == sysUserRole) return null;
//            return sysUserRole.getUser();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

	@Override
	public List<TMtCompany> findCompanyPage(String companyName, TMtCompany company  ,Integer pageSize , ObjectContext context) {
		int level = company.getCompanyLevel();
		Expression qualifier = ExpressionFactory.expTrue();
		switch (level) {
			case TMtCompany.LEVEL_ONE:
				break;
			case TMtCompany.LEVEL_TWO:
				Expression expression = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
				expression = expression.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
				qualifier = qualifier.andExp(expression);
				break;
			case TMtCompany.LEVEL_THREE:
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
				break;
			default:
				break;
		}
		if(StringUtils.isNotEmpty(companyName)){
			qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtCompany.COMPANY_NAME_PROPERTY, "%"+companyName+"%"));
		}
		SelectQuery select = new SelectQuery(TMtCompany.class,qualifier);
		select.setPageSize(pageSize);
		select.addOrdering(TMtCompany.CTIME_PROPERTY, SortOrder.DESCENDING);
		return context.performQuery(select);
	}

	@Override
	public TMtCompany findCompanyById(ObjectContext context, Integer companyId) {
		try {
			return Cayenne.objectForPK(context, TMtCompany.class, companyId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
