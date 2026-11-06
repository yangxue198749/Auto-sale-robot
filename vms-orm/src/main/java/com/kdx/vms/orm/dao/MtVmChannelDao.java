package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVmChannelNext;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmChannel;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by liyang on 2016/11/28.
 */
public interface MtVmChannelDao {

	public TMtVmChannel findByChannelCode(ObjectContext context, TMtVm mtVm,String channelCode);

	public Map<String, TMtVmChannel> getMtVmChannelInfoMap(TMtVm mtVm);

	public Map<String, TMtVmChannelNext> getMtVmChannelNextMap(TMtVm mtVm);

	public List queryValidityByEnd(TSysUser user, Integer searchType, String searchValue,Integer pageSize, String skuCode);

	List<TMtVmChannel> findByVmId(ObjectContext context, Integer vmId);
}
