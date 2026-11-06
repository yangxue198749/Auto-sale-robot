package com.kdx.vms.orm.dao.impl;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.orm.dao.MtAreaDao;

@Service("mtAreaDao")
public class MtAreaDaoImpl implements MtAreaDao{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public List<TMtArea> findMtArea(ObjectContext context, Integer level) {
		try {
			Expression qualifier = ExpressionFactory.noMatchExp(TMtArea.AREA_LEVEL_PROPERTY, null);
			if(null != level){
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtArea.AREA_LEVEL_PROPERTY,level));
			}
			SelectQuery select = new SelectQuery(TMtArea.class, qualifier);
	        List<TMtArea> list = context.performQuery(select);
	        return list;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

    @Override
    public List getAllArea(ObjectContext context) {
        logger.debug("context:" + context);
        if (null == context) return null;
        try {
            return context.performQuery(new SelectQuery(TMtArea.class));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }
}
