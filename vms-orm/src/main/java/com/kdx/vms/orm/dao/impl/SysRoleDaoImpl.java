package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.orm.dao.SysRoleDao;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * Created by liyang on 16-12-1.
 */
@Service("sysRoleDao")
public class SysRoleDaoImpl implements SysRoleDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public List findSysRoleCompany(TMtCompany company, String roleName, Integer pageSize) {
        logger.debug("company:" + company);
        if (null == company)
            return null;
        try {
            Expression expression = ExpressionFactory.noMatchExp(TSysRole.ROLE_TYPE_PROPERTY, TSysRole.ROLE_TYPE_SYSTEM);
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TSysRole.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TSysRole.COMPANY_PROPERTY, company));
                    expression = expression.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    expression = expression.andExp(ExpressionFactory.matchExp(TSysRole.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            expression = expression.orExp(ExpressionFactory.matchExp(TSysRole.ROLE_TYPE_PROPERTY, TSysRole.ROLE_TYPE_PUBLIC));
            if (!StringUtil.isEmpty(roleName)) {
                expression = expression.andExp(ExpressionFactory.likeExp(TSysRole.ROLE_NAME_PROPERTY, "%" + roleName + "%"));
            }
            SelectQuery select = new SelectQuery(TSysRole.class, expression);
            if (IntegerUtils.isPositiveInteger(pageSize)) select.setPageSize(pageSize);
            return company.getObjectContext().performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

	@Override
	public TSysRole findAdminRole(ObjectContext context, int level, int type) {
		SelectQuery query = new SelectQuery(TSysRole.class);
		query.andQualifier(ExpressionFactory.matchExp(TSysRole.ROLE_LEVEL_PROPERTY, level));
		query.andQualifier(ExpressionFactory.matchExp(TSysRole.ROLE_TYPE_PROPERTY, type));
		List<TSysRole> list = context.performQuery(query);
		if(list != null && list.size() > 0) return list.get(0);
		return null;
	}

	@Override
	public List findSysRoleByCompany(TMtCompany company) {
		try {
			Expression expression = ExpressionFactory.matchExp(TSysRole.COMPANY_PROPERTY, company);
			expression = expression.orExp(ExpressionFactory.matchExp(TSysRole.ROLE_TYPE_PROPERTY, TSysRole.ROLE_TYPE_PUBLIC));
			Integer level = company.getCompanyLevel();
			switch (level) {
			case TMtCompany.LEVEL_ONE:
				expression = expression.andExp(ExpressionFactory.noMatchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN_2));
				expression = expression.andExp(ExpressionFactory.noMatchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN_3));
				break;
			case TMtCompany.LEVEL_TWO:
				expression = expression.andExp(ExpressionFactory.noMatchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN));
				expression = expression.andExp(ExpressionFactory.noMatchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN_3));
				break;
			case TMtCompany.LEVEL_THREE:
				expression = expression.andExp(ExpressionFactory.noMatchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN));
				expression = expression.andExp(ExpressionFactory.noMatchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN_2));
				break;
			default:
				break;
			}
		  SelectQuery select = new SelectQuery(TSysRole.class, expression);
          return company.getObjectContext().performQuery(select);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}
}
