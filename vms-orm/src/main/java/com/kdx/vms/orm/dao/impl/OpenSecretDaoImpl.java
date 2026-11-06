package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TOpenSecret;
import com.kdx.vms.orm.dao.OpenSecretDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by guoyang on 2018/2/9.
 */
@Repository
public class OpenSecretDaoImpl implements OpenSecretDao {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public List<TOpenSecret> findTOpenSecret(ObjectContext context, Integer pageSize) {
        try{
            Expression expression = ExpressionFactory.matchExp(TOpenSecret.ENABLE_PROPERTY, TOpenSecret.ADD_ENABLE);
            SelectQuery query = new SelectQuery(TOpenSecret.class,expression);
            query.setPageSize(pageSize);
            query.addOrdering(TOpenSecret.CTIME_PROPERTY, SortOrder.DESCENDING);
            return context.performQuery(query);
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(),e);
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public TOpenSecret findByPublicId(ObjectContext context, String customerId) {
        try{
            Expression expression = ExpressionFactory.matchExp(TOpenSecret.PUBLIC_ID_PROPERTY, customerId);
            SelectQuery select = new SelectQuery(TOpenSecret.class, expression);
            return (TOpenSecret) Cayenne.objectForQuery(context, select);
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

}
