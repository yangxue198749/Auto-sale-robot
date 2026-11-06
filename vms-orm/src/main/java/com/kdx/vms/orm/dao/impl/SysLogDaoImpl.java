package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.SysLogDao;

@Repository
public class SysLogDaoImpl implements SysLogDao {

	@Override
	public List<Object> findSysLogList(List<TSysUser> userIdList, Integer pageSize, ObjectContext context) {
		SelectQuery query = new SelectQuery(TSysLog.class);
		if(userIdList != null && userIdList.size() > 0){
			query.andQualifier(ExpressionFactory.inExp(TSysLog.USER_ID_PROPERTY, userIdList));
		}
		query.addOrdering(TSysLog.LOG_TIME_PROPERTY, SortOrder.DESCENDING);
		query.setPageSize(pageSize);
		return context.performQuery(query);
	}

}
