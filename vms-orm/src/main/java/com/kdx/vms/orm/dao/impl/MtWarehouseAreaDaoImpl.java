package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtWarehouseArea;
import com.kdx.vms.orm.dao.MtWarehouseAreaDao;

@Repository
public class MtWarehouseAreaDaoImpl implements MtWarehouseAreaDao {

	@Override
	public List<TMtWarehouseArea> findWarehouseAreaDao(ObjectContext context) {
		SelectQuery query = new SelectQuery(TMtWarehouseArea.class);
		query.addPrefetch(TMtWarehouseArea.AREA_PROPERTY);
		query.addPrefetch(TMtWarehouseArea.WARE_HOUSE_PROPERTY);
		return context.performQuery(query);
	}

	@Override
	public TMtWarehouseArea findWarehouseAreaByAreaIdAndWarehouseId(Integer areaId, Integer warehouseId, ObjectContext context) {
		SelectQuery query = new SelectQuery(TMtWarehouseArea.class);
		if(areaId != null){
			query.andQualifier(ExpressionFactory.matchExp(TMtWarehouseArea.AREA_PROPERTY ,areaId));
		}
		if(warehouseId != null){
			query.andQualifier(ExpressionFactory.matchExp(TMtWarehouseArea.WARE_HOUSE_PROPERTY ,warehouseId));
		}
		List<TMtWarehouseArea> list = context.performQuery(query);
		if(list != null && list.size() > 0) return list.get(0);
		return null;
	}

}
