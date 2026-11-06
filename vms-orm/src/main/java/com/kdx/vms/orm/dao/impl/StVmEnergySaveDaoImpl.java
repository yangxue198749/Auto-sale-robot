package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TStVmEnergySave;
import com.kdx.vms.orm.dao.StVmEnergySaveDao;

@Service("stVmEnergySaveDao")
public class StVmEnergySaveDaoImpl implements StVmEnergySaveDao{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<TStVmEnergySave> findByStrategyId(ObjectContext context,
			Integer strategyId) {
		try {
			Expression qualifier = ExpressionFactory.matchExp(TStVmEnergySave.STRATEGY_PROPERTY, strategyId);
			SelectQuery select = new SelectQuery(TStVmEnergySave.class, qualifier);
			return context.performQuery(select);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public TStVmEnergySave findByPk(ObjectContext context, Integer energySaveId) {
		try {
			ObjectId id = new ObjectId(TStVmEnergySave.class.getSimpleName(), TStVmEnergySave.ENERGY_SAVE_ID_PK_COLUMN, energySaveId);
            ObjectIdQuery query = new ObjectIdQuery(id);
            return (TStVmEnergySave) Cayenne.objectForQuery(context, query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}


}
