package com.kdx.vms.orm.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TVmOperationHis;
import com.kdx.vms.orm.dao.VmOperationHisDao;

@Service("vmOperationHisDao")
public class VmOperationHisDaoImpl implements VmOperationHisDao {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Integer getCurrDoorStatus(ObjectContext context, Integer vmId) {
		try {
			Expression expression = ExpressionFactory.expTrue();
			expression = expression.andExp(ExpressionFactory.matchExp(TVmOperationHis.VM_ID_PROPERTY, vmId));
			SelectQuery select = new SelectQuery(TVmOperationHis.class, expression);
			select.addOrdering(TVmOperationHis.CTIME_PROPERTY,SortOrder.DESCENDING);
			List<TVmOperationHis> list = context.performQuery(select);
			if(null !=list &&list.size()>0)return list.get(0).getVmDoorStatus();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
