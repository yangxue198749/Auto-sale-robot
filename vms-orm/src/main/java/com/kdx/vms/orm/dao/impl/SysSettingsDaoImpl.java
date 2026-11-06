package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.orm.dao.SysSettingsDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yangshuxi on 2017/5/19.
 */
@Repository
public class SysSettingsDaoImpl implements SysSettingsDao {
    @Override
    public List findAllSettings(ObjectContext context, Integer pageSize, String searchValue) {
        Expression qualifier = ExpressionFactory.matchExp(TSysSettings.PARENT_PROPERTY, Cayenne.objectForPK(context, TSysSettings.class, TSysSettings.ROOT_ID));
        if (!StringUtil.isEmpty(searchValue)) {
            Expression expression = ExpressionFactory.likeExp(TSysSettings.KEY_PROPERTY, "%" + searchValue.trim() + "%");
            expression = expression.orExp(ExpressionFactory.likeExp(TSysSettings.NAME_PROPERTY, "%" + searchValue.trim() + "%"));
            qualifier = qualifier.andExp(expression);
        }
        SelectQuery query = new SelectQuery(TSysSettings.class, qualifier);
        query.setPageSize(pageSize);
        query.addOrdering(TSysSettings.PARENT_PROPERTY, SortOrder.ASCENDING);
        return context.performQuery(query);
    }
}
