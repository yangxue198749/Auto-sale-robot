package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtVmVersion;
import com.kdx.vms.orm.dao.MtVmVersionDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("mtVmVersionDao")
public class MtVmVersionDaoImpl implements MtVmVersionDao {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public TMtVmVersion fingVersionByVmIdAndType(ObjectContext context,
			Integer vmId, Integer deviceType) {
		try {
			Expression expression = ExpressionFactory.expTrue();
			expression = expression.andExp(ExpressionFactory.matchExp(TMtVmVersion.MT_VM_PROPERTY,vmId));
			expression = expression.andExp(ExpressionFactory.matchExp(TMtVmVersion.DEVICE_TYPE_PROPERTY,deviceType));
			SelectQuery query = new SelectQuery(TMtVmVersion.class,expression);
			List<TMtVmVersion> list = context.performQuery(query);
			if(null != list && list.size()>0)return list.get(0);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
