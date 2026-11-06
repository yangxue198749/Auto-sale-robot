package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TOpenService;
import com.kdx.vms.orm.dao.OpenServiceDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by guoyang on 2018/2/9.
 */
@Repository
public class OpenServiceDaoImpl implements OpenServiceDao {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public TOpenService findById(ObjectContext context, Integer id) {
        try {
            return Cayenne.objectForPK(context, TOpenService.class, id);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TOpenService> findOpenServiceList(ObjectContext context) {
        try{
            SelectQuery query = new SelectQuery(TOpenService.class);
            return context.performQuery(query);
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return Collections.emptyList();
    }
}
