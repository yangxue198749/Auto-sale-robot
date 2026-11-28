package com.kdx.vms.orm.dao.impl;

import java.util.List;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysRoleElement;
import com.kdx.vms.model.bean.TSysUser;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TSysElement;
import com.kdx.vms.orm.dao.SysElementDao;


@Service("sysElementDao")
public class SysElementDaoImpl implements SysElementDao{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String getCodeByUrl(ObjectContext context, String url) {
		try {
			Expression expression = ExpressionFactory.matchExp(TSysElement.ELEMENT_URL_PROPERTY, url);
			SelectQuery select = new SelectQuery(TSysElement.class, expression);
			List<TSysElement> eleMentList = context.performQuery(select);
			if(null != eleMentList && eleMentList.size()>0){
				return eleMentList.get(0).getElementCode();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public List getSysElementAll(ObjectContext context) {
        if (null == context) return null;
        try {
            SelectQuery selectQuery = new SelectQuery(TSysElement.class);
            selectQuery.addOrdering(TSysElement.PARENT_ELEMENT_PROPERTY, SortOrder.ASCENDING);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public List getSysElementByRole(TSysRole role) {
        if (null == role) return null;
        try {
            Expression expression = ExpressionFactory.matchExp(TSysElement.ROLE_ELEMENT_PROPERTY + "." + TSysRoleElement.ROLE_PROPERTY, role);
            SelectQuery selectQuery = new SelectQuery(TSysElement.class, expression);
            return role.getObjectContext().performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

	@Override
	public List getElementAllBySystemAdmin(TSysRole sysRole) {
		try {
			ObjectContext context = sysRole.getObjectContext();
			TMtCompany company = sysRole.getCompany();
			Integer level = company.getCompanyLevel();
			Expression expression= ExpressionFactory.expTrue();
			switch (level) {
            case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
            	expression = expression.andExp(ExpressionFactory.matchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN));
                break;
            case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
            	expression = expression.andExp(ExpressionFactory.matchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN_2));
                break;
            case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
            	expression = expression.andExp(ExpressionFactory.matchExp(TSysRole.ROLE_LEVEL_PROPERTY, TSysRole.ROLE_LEVEL_ADMIN_3));
                break;
			}
			SelectQuery selectQuery = new SelectQuery(TSysRole.class, expression);
			List<TSysRole> roleList = context.performQuery(selectQuery);
			if(roleList != null && roleList.size()>0){
				if(roleList.size() > 1){
					//多个同层级管理员角色时取 ID 最小的一个，保证确定性
					//（存量旧角色 ID 均小于种子角色 990001；部署前置检查要求 ROLE_LEVEL=1 角色唯一）
					java.util.Collections.sort(roleList, new java.util.Comparator<TSysRole>(){
						public int compare(TSysRole a, TSysRole b){ return a.getId().compareTo(b.getId()); }
					});
					logger.warn("Multiple level-admin roles found, using the lowest id: " + roleList.get(0).getId());
				}
				TSysRole adminRole = roleList.get(0);
				return adminRole.getRoleElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
