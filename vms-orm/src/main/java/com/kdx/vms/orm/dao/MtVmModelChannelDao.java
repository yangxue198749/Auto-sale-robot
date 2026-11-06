package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVmModelChannel;

public interface MtVmModelChannelDao {
	public List<TMtVmModelChannel> findVmModelChannelRowByCode(String vmModelId, String channelCode ,ObjectContext context);
	
	public List<TMtVmModelChannel> findVmModelChannelByCode(String vmModelId, String channelCode ,ObjectContext context);
}
