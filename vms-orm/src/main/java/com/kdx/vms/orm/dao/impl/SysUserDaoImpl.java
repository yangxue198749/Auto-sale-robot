package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.SysUserDao;
import org.apache.cayenne.Cayenne;
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
 */
@Service("sysUserDao")
public class SysUserDaoImpl implements SysUserDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List findSysUserByAccount(ObjectContext context, String account, boolean eliminateDelete) {
        try {
            Expression matchExp = ExpressionFactory.matchExp(TSysUser.USER_ACCOUNT_PROPERTY, account);
            if (eliminateDelete) {
                matchExp = matchExp.andExp(ExpressionFactory.matchExp(TSysUser.DELETE_MARK_PROPERTY, 0));
            }
            SelectQuery select = new SelectQuery(TSysUser.class, matchExp);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public boolean addUser(TSysUser tSysUser, ObjectContext context) {
        try {
            context.registerNewObject(tSysUser);
            context.commitChanges();
            return true;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    @Override
    public List findSysUserByName(ObjectContext context, String name) {
        logger.debug("context:" + context + ", name:" + name);
        try {
            Expression matchExp = ExpressionFactory.likeExp(TSysUser.USER_NAME_PROPERTY, "%" + name + "%");
            matchExp = matchExp.andExp(ExpressionFactory.matchExp(TSysUser.DELETE_MARK_PROPERTY, 0));
            SelectQuery selectQuery = new SelectQuery(TSysUser.class, matchExp);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findSysUserForCompany(ObjectContext context, String searchType, String searchValue, TMtCompany
            mtCompany, Integer pageSize) {
        logger.debug("context:" + context + ", searchType:" + searchType + ", searchValue:" + searchValue + ", mtCompany:" + mtCompany);
        if (null == context || null == mtCompany) return null;
        try {
            Expression expression = ExpressionFactory.matchExp(TSysUser.DELETE_MARK_PROPERTY, 0);
            switch (mtCompany.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TSysUser.COMPANY_PROPERTY, mtCompany.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, mtCompany));
                    expression = expression.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    expression = expression.andExp(ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, mtCompany));
                    break;
                default:
                    return null;
            }
            if (null != searchValue) {
                switch (searchType) {
                    case "1":
                        expression = expression.andExp(ExpressionFactory.likeExp(TSysUser.USER_NAME_PROPERTY, "%" + searchValue + "%"));
                        break;
                    case "2":
                        expression = expression.andExp(ExpressionFactory.likeExp(TSysUser.COMPANY_PROPERTY + "." + TMtCompany.COMPANY_NAME_PROPERTY, "%" + searchValue + "%"));
                        break;
                }
            }
            SelectQuery selectQuery = new SelectQuery(TSysUser.class, expression);
            selectQuery.setPageSize(pageSize);
            selectQuery.addPrefetch(TSysUser.USER_ROLE_PROPERTY);
            selectQuery.addPrefetch(TSysUser.USER_ROLE_PROPERTY + "." + TSysUserRole.ROLE_PROPERTY);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findSysUserForCompany(ObjectContext context, String searchType, String searchValue, Integer
            mtCompanyId, Integer pageSize) {
        logger.debug("context:" + context + ", searchType:" + searchType + ", searchValue:" + searchValue);
        if (null == context || null == mtCompanyId || mtCompanyId <= 0) return null;
        try {
            TMtCompany mtCompany = Cayenne.objectForPK(context, TMtCompany.class, mtCompanyId);
            return findSysUserForCompany(context, searchType, searchValue, mtCompany, pageSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TSysUser> findSysUsersByRoleLevelForCompany(ObjectContext context, String userName, Integer
            roleLevel, Integer companyId) {
        logger.debug("context:" + context + ", userName:" + userName + ", roleLevel:" + roleLevel);
        if (null == context || null == companyId || companyId <= 0) return null;
        try {
            Expression expression = ExpressionFactory.matchExp(TSysUser.DELETE_MARK_PROPERTY, 0);
            expression = expression.andExp(ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, companyId));
            if (!StringUtil.isEmpty(userName)) {
                expression = expression.andExp(ExpressionFactory.likeExp(TSysUser.USER_NAME_PROPERTY, "%" + userName + "%"));
            }
            expression = expression.andExp(ExpressionFactory.matchExp(TSysUser.USER_ROLE_PROPERTY + "." + TSysUserRole.ROLE_PROPERTY + "." + TSysRole.ROLE_LEVEL_PROPERTY, roleLevel));
            SelectQuery selectQuery = new SelectQuery(TSysUser.class, expression);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TSysUser findUserByPk(ObjectContext context, Integer userId) {
        try {
            return Cayenne.objectForPK(context, TSysUser.class, userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public void flashRoleCache(TSysUser user) {
        try {
            Expression expression = ExpressionFactory.matchExp(TSysUserRole.USER_PROPERTY, user);
            SelectQuery selectQuery = new SelectQuery(TSysUserRole.class, expression);
            selectQuery.addPrefetch(TSysUserRole.ROLE_PROPERTY);
            selectQuery.addPrefetch(TSysUserRole.ROLE_PROPERTY + "." + TSysRole.ROLE_ELEMENT_PROPERTY);
            selectQuery.addPrefetch(TSysUserRole.ROLE_PROPERTY + "." + TSysRole.ROLE_ELEMENT_PROPERTY + "." + TSysRoleElement.ELEMENT_PROPERTY);
            user.getObjectContext().performQuery(selectQuery);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }

    }

}
