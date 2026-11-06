package com.kdx.vms.orm.dao.impl;

import java.util.List;

import com.kdx.vms.model.bean.TMtSku;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.orm.dao.MtSkuTypeDao;

@Repository
public class MtSkuTypeDaoImpl implements MtSkuTypeDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findSkuTypeList(Integer skuTypePid ,Integer pageSize, ObjectContext context) {
		SelectQuery selectQuery = new SelectQuery(TMtSkuType.class);
		selectQuery.andQualifier(ExpressionFactory.matchExp(TMtSkuType.PARENT_PROPERTY, skuTypePid));
		selectQuery.setPageSize(pageSize);
		selectQuery.addPrefetch(TMtSkuType.CHILDS_PROPERTY);
		selectQuery.addOrdering(TMtSkuType.CTIME_PROPERTY ,SortOrder.DESCENDING);
		return context.performQuery(selectQuery);
	}

	@Override
	public List<Object> findSkuTypeList(ObjectContext context) {
		return findSkuTypeList(null ,null, context);
	}

	@Override
	public TMtSkuType findByExternalId(ObjectContext context, String externalId) {
		SelectQuery selectQuery = new SelectQuery(TMtSkuType.class);
		selectQuery.andQualifier(ExpressionFactory.matchExp(TMtSkuType.EXTERNAL_ID_PROPERTY, externalId));
		List<TMtSkuType> list = context.performQuery(selectQuery);
		if(null!=list&&!list.isEmpty())return list.get(0);
		return null;
	}
}
