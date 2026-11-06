package com.kdx.vms.orm.dao.impl;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectIdQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TStVmSkuSort;
import com.kdx.vms.orm.dao.StVmSkuSortDao;

@Service("stVmSkuSortDao")
public class StVmSkuSortDaoImpl implements StVmSkuSortDao{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public TStVmSkuSort findByPk(ObjectContext context, Integer sortId) {
		try {
			ObjectId id = new ObjectId(TStVmSkuSort.class.getSimpleName(), TStVmSkuSort.SORT_ID_PK_COLUMN, sortId);
            ObjectIdQuery query = new ObjectIdQuery(id);
            return (TStVmSkuSort) Cayenne.objectForQuery(context, query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
