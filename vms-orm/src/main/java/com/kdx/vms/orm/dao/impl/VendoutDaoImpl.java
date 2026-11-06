package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderDetailVendout;
import com.kdx.vms.model.bean.TOrderMaster;
import com.kdx.vms.orm.dao.VendoutDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.collections.functors.ExceptionFactory;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * create by zhouxing on 2017/11/23
 */
@Repository
public class VendoutDaoImpl implements VendoutDao{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public TOrderDetail findById(ObjectContext context, String id) {
        try {
            Asserts.notNull(id, "vendoutId not be null ");
            return Cayenne.objectForPK(context, TOrderDetail.class,id);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public TOrderMaster findByDetailId(ObjectContext context, Integer detailId) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TOrderMaster.DETAILS_PROPERTY,detailId);
            SelectQuery select = new SelectQuery(TOrderMaster.class, qualifier);
            List<TOrderMaster> tOrderMasters = context.performQuery(select);
            if(null != tOrderMasters.get(0)){
                return tOrderMasters.get(0);
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }
}