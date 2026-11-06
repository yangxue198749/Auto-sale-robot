package com.kdx.vms.orm.dao.impl;

import java.util.List;

import com.kdx.vms.model.bean.TVmStatusHis;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TVmStatus;
import com.kdx.vms.orm.dao.VmStatusDao;

/**
 *
 * Created by liyang on 16-12-2.
 */
@Service("vmStatusDao")
public class VmStatusDaoImpl implements VmStatusDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public TVmStatus findByMtVmId(ObjectContext context, Integer vmId) {
		try {
			Expression expression = ExpressionFactory.matchExp(TVmStatus.MT_VM_PROPERTY,vmId);
			SelectQuery select = new SelectQuery(TVmStatus.class, expression);
			select.setPageSize(1);
			List<TVmStatus> list = context.performQuery(select);
			if(list != null && list.size() >0){
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public TVmStatusHis findLastNormalHis(ObjectContext context, Integer vmId) {
		try {
		Expression expression = ExpressionFactory.matchExp(TVmStatusHis.VM_ID_PROPERTY,vmId);
		expression = expression.andExp(ExpressionFactory.matchExp(TVmStatusHis.VM_STATUS_PROPERTY,0));
		SelectQuery select = new SelectQuery(TVmStatusHis.class, expression);
		select.setPageSize(1);
		select.addOrdering(TVmStatusHis.CTIME_PROPERTY, SortOrder.DESCENDING);
		List<TVmStatusHis> list = context.performQuery(select);
		if(list != null && list.size() >0){
			return list.get(0);
		}
	} catch (Exception e) {
		e.printStackTrace();
		logger.error(e.getLocalizedMessage(),e);
	}
		return null;
	}

}
