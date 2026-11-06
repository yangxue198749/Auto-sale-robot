package com.kdx.vms.orm.dao.impl;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.orm.dao.MtBusinessDistrictDao;

@Repository
public class MtBusinessDistrictDaoImpl implements MtBusinessDistrictDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findBusinessDistrict(Integer pageSize, ObjectContext context) {
		SelectQuery query = new SelectQuery(TMtBusinessDistrict.class);
		query.setPageSize(pageSize);
		query.addOrdering(TMtBusinessDistrict.CTIME_PROPERTY, SortOrder.DESCENDING);
		return context.performQuery(query);
	}

	@Override
	public List<TMtBusinessDistrict> findAllBusinessDistrict(ObjectContext context) {
		SelectQuery query = new SelectQuery(TMtBusinessDistrict.class);
		query.addOrdering(TMtBusinessDistrict.CTIME_PROPERTY, SortOrder.DESCENDING);
		return context.performQuery(query);
	}

    @Override
    public List getAllBusinessDistrict(ObjectContext context) {
        logger.info("context:" + context);
        try {
            return context.performQuery(new SelectQuery(TMtBusinessDistrict.class));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }
}
