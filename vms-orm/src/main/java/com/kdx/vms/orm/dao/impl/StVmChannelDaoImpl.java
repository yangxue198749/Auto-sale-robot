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

import com.kdx.vms.model.bean.TStVmChannel;
import com.kdx.vms.orm.dao.StVmChannelDao;

@Service("stVmChannelDao")
public class StVmChannelDaoImpl implements StVmChannelDao{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public TStVmChannel findByPk(ObjectContext context, Integer channelId) {
		try {
			ObjectId id = new ObjectId(TStVmChannel.class.getSimpleName(), TStVmChannel.CHANNEL_ID_PK_COLUMN, channelId);
            ObjectIdQuery query = new ObjectIdQuery(id);
            return (TStVmChannel) Cayenne.objectForQuery(context, query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	@Override
	public TStVmChannel hasSkuBySkuIdAndNotChannelId(ObjectContext context,
			Integer skuId, Integer channelId,Integer strategyId) {
		 try {
			 Expression qualifier = ExpressionFactory.matchExp(TStVmChannel.SKU_ID_PROPERTY, skuId);
			 qualifier = qualifier.andExp(ExpressionFactory.matchExp(TStVmChannel.STRATEGY_PROPERTY, strategyId));
			 SelectQuery select = new SelectQuery(TStVmChannel.class, qualifier);
			 List<TStVmChannel> list = context.performQuery(select);
			 if(null != list && list.size()>1){
				 return (TStVmChannel) list.get(0);
			 }
		 } catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		 }
		 return null;
	}

	@Override
	public List<TStVmChannel> findByMergeChannelId(ObjectContext context,
			Integer channelId) {
		try {
			Expression qualifier = ExpressionFactory.matchExp(TStVmChannel.MERGE_PID_PROPERTY, channelId);
			SelectQuery select = new SelectQuery(TStVmChannel.class, qualifier);
			return context.performQuery(select);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

	
}
