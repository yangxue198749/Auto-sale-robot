package com.kdx.vms.orm.dao.impl;

import java.util.List;

import com.kdx.vms.orm.dao.SysTypeInfoDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TSysTypeInfo;

@Repository
@SuppressWarnings("unchecked")
public class SysTypeInfoDaoImpl implements SysTypeInfoDao {

	@Override
	public TSysTypeInfo findSysTypeinfo(String code, ObjectContext context) {
		SelectQuery query = new SelectQuery(TSysTypeInfo.class ,ExpressionFactory.expTrue());
		if(StringUtils.isNotEmpty(code)){
			query.andQualifier(ExpressionFactory.matchExp(TSysTypeInfo.TYPE_CODE_PROPERTY, code));
		}
		List<TSysTypeInfo> list = context.performQuery(query);
		if(list != null && list.size() > 0) return list.get(0);
		return null;
	}

	@Override
	public List<Object> findTSysTypeInfoList(String name, Integer pageSize, ObjectContext context) {
		SelectQuery query = new SelectQuery(TSysTypeInfo.class ,ExpressionFactory.expTrue());
		if(StringUtils.isNotEmpty(name)){
			query.andQualifier(ExpressionFactory.likeExp(TSysTypeInfo.TYPE_NAME_PROPERTY, "%"+name+"%"));
		}
		query.setPageSize(pageSize);
		query.addOrdering(TSysTypeInfo.TYPE_CODE_PROPERTY ,SortOrder.DESCENDING);
		return context.performQuery(query);
	}
	
}
