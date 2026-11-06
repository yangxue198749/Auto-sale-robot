package com.kdx.vms.orm.dao.impl;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.springframework.stereotype.Repository;

import com.kdx.vms.model.bean.TMtVmModelChannel;
import com.kdx.vms.orm.dao.MtVmModelChannelDao;

@Repository
public class MtVmModelChannelDaoImpl implements MtVmModelChannelDao{

	@Override
	public List<TMtVmModelChannel> findVmModelChannelRowByCode(String vmModelId, String channelCode ,ObjectContext context) {
		
		SelectQuery query = new SelectQuery(TMtVmModelChannel.class);
		query.andQualifier(ExpressionFactory.matchExp(TMtVmModelChannel.MODEL_PROPERTY, vmModelId));
		query.andQualifier(ExpressionFactory.likeExp(TMtVmModelChannel.CHANNEL_CODE_PROPERTY, channelCode+"%"));
		return context.performQuery(query);
	}

	@Override
	public List<TMtVmModelChannel> findVmModelChannelByCode(String vmModelId, String channelCode,ObjectContext context) {

		SelectQuery query = new SelectQuery(TMtVmModelChannel.class);
		query.andQualifier(ExpressionFactory.matchExp(TMtVmModelChannel.MODEL_PROPERTY, vmModelId));
		query.andQualifier(ExpressionFactory.matchExp(TMtVmModelChannel.CHANNEL_CODE_PROPERTY, channelCode));
		return context.performQuery(query);
	}

}
