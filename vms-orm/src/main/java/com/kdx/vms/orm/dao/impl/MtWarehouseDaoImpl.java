package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtWarehouse;
import com.kdx.vms.orm.dao.MtWarehouseDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MtWarehouseDaoImpl implements MtWarehouseDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findWarehouseList(String idCode, String name, int pageSize, ObjectContext context) {
		Expression expression = null;
		if(StringUtils.isNotEmpty(idCode)){
			expression = ExpressionFactory.likeExp(TMtWarehouse.WAREHOUSE_CODE_PROPERTY, "%"+idCode+"%");
		}else if(StringUtils.isNotEmpty(name)){
			expression = ExpressionFactory.likeExp(TMtWarehouse.WAREHOUSE_NAME_PROPERTY, "%"+name+"%");
		}
		SelectQuery selectQuery = new SelectQuery(TMtWarehouse.class,expression);
		selectQuery.setPageSize(pageSize);
		return context.performQuery(selectQuery);
	}

	@Override
	public TMtWarehouse findByCode(ObjectContext context, String warehouseCode) {
		SelectQuery selectQuery = new SelectQuery(TMtWarehouse.class);
		selectQuery.andQualifier(ExpressionFactory.matchExp(TMtWarehouse.WAREHOUSE_CODE_PROPERTY, warehouseCode));
		List<TMtWarehouse> list = context.performQuery(selectQuery);
		if(null!=list&&!list.isEmpty())return list.get(0);
		return null;
	}

}
