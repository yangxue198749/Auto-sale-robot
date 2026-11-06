package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TStVmText;
import com.kdx.vms.orm.dao.MtVmTextDao;

@Service("mtVmTextDao")
public class MtVmTextDaoImpl implements MtVmTextDao{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public TStVmText findByStrategyId(ObjectContext context, Integer strategyId) {
		try {
			Expression qualifier = ExpressionFactory.matchExp(TStVmText.STRATEGY_PROPERTY, strategyId);
			SelectQuery select = new SelectQuery(TStVmText.class, qualifier);
			List<TStVmText> list = context.performQuery(select);
			if(null != list && list.size()>0)return list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
