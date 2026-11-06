package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserBind;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.SysUserBindDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/20.
 */
@Repository
public class SysUserBindDaoImpl implements SysUserBindDao {
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public TSysUserBind findUserBindByOuterId(String outerId, Integer bindType) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TSysUserBind.OUTER_ID_PROPERTY, outerId);
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TSysUserBind.BIND_TYPE_PROPERTY, bindType));
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TSysUserBind.USER_PROPERTY + "." + TSysUser.DELETE_MARK_PROPERTY, TSysUser.DELETE_MARK_EXIST));
            SelectQuery select = new SelectQuery(TSysUserBind.class, qualifier);
            select.addPrefetch(TSysUserBind.USER_PROPERTY);
            List<TSysUserBind> list = context.performQuery(select);
            if (null != list && !list.isEmpty()) return list.get(0);
            return null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
