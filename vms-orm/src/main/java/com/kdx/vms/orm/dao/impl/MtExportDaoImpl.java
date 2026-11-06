package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.model.bean.TMtExport;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtExportDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yangshuxi on 2017/12/6.
 */
@Repository
public class MtExportDaoImpl implements MtExportDao {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<?> findEarlyExportData(ObjectContext context) {
        Expression qualifier = ExpressionUtils.fillEndTimeQualifier(null, TMtExport.CREATE_TIME_PROPERTY, DateUtils.getTime1(DateUtils.getDayBefore(7)));
        qualifier = ExpressionUtils.fillInQualifier(qualifier, TMtExport.TYPE_PROPERTY, TMtExport.TYPE_ORDER, TMtExport.TYPE_MANUAL);
        return context.performQuery(new SelectQuery(TMtExport.class, qualifier));
    }

    @Override
    public List<?> findEarlyStockData(ObjectContext context) {
        Expression qualifier = ExpressionUtils.fillEndTimeQualifier(null, TMtExport.CREATE_TIME_PROPERTY, DateUtils.getTime1(DateUtils.getDayBefore(30)));
        qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtExport.TYPE_PROPERTY, TMtExport.TYPE_STOCK);
        return context.performQuery(new SelectQuery(TMtExport.class, qualifier));
    }

    @Override
    public List<?> findExportData(TSysUser sysUser) {
        Expression qualifier = ExpressionUtils.fillMatchQualifier(null, TMtExport.COMPANY_ID_PROPERTY, sysUser.getCompany().getId());
        qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(TMtExport.ADDRESS_PROPERTY, null));
        SelectQuery query = new SelectQuery(TMtExport.class, qualifier);
        query.addOrdering(TMtExport.CREATE_TIME_PROPERTY, SortOrder.DESCENDING);
        return sysUser.getObjectContext().performQuery(query);
    }

    @Override
    public TMtExport findExportByMatchCondition(ObjectContext context, String condition) {
        try {
            Expression expression = ExpressionFactory.matchExp(TMtExport.CONDITION_PROPERTY, condition);
            SelectQuery select = new SelectQuery(TMtExport.class, expression);
            return (TMtExport) Cayenne.objectForQuery(context, select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }


}
