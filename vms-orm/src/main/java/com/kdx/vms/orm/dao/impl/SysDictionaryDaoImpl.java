package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.orm.dao.SysDictionaryDao;

@Repository
public class SysDictionaryDaoImpl implements SysDictionaryDao {

    private final Logger logger = Logger.getLogger(getClass());
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findSysDictionaryList(Integer tSysTypeInfoId, String name, Integer pageSize,
			ObjectContext context) {
		SelectQuery query = new SelectQuery(TSysDictionary.class ,ExpressionFactory.matchExp(TSysDictionary.TYPE_INFO_PROPERTY, tSysTypeInfoId));
		if(StringUtils.isNotEmpty(name)){
			query.andQualifier(ExpressionFactory.likeExp(TSysDictionary.DICTIONARY_NAME_PROPERTY, "%"+name+"%"));
		}
		query.setPageSize(pageSize);
		query.addOrdering(TSysDictionary.DICTIONARY_CODE_PROPERTY ,SortOrder.DESCENDING);
		return context.performQuery(query);
	}

	@Override
	public TSysDictionary findSysDictionaryByCode(String code, ObjectContext context) {
		SelectQuery query = new SelectQuery(TSysDictionary.class ,ExpressionFactory.matchExp(TSysDictionary.DICTIONARY_CODE_PROPERTY, code));
		List<TSysDictionary> list = context.performQuery(query);
		if(list != null && list.size() > 0) return list.get(0); 
		return null;
	}

	@Override
	public List<TSysDictionary> findByTypeInfoId(Integer typeInfoId,
			ObjectContext context) {
		SelectQuery query = new SelectQuery(TSysDictionary.class ,ExpressionFactory.matchExp(TSysDictionary.TYPE_INFO_PROPERTY, typeInfoId));
		return context.performQuery(query);
	}

	@Override
	public TSysDictionary getTSysDictionaryByCodeAndTypeInfoId(String code,
			Integer typeInfoId, ObjectContext context) {
		try {
			Expression expression = ExpressionFactory.matchExp(TSysDictionary.DICTIONARY_CODE_PROPERTY, code);
			expression = expression.andExp(ExpressionFactory.matchExp(TSysDictionary.TYPE_INFO_PROPERTY, typeInfoId));
			SelectQuery query = new SelectQuery(TSysDictionary.class ,expression);
			List<TSysDictionary> list = context.performQuery(query);
			if(list != null && list.size() > 0) return list.get(0); 
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
