package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmChannel;

/**
 * Created by liyang on 2016/11/28.
 */
public interface StVmChannelDao {

	/**
	 * 根据主键查询货道策略
	 * @param context
	 * @param channelId
	 * @return
	 */
	TStVmChannel findByPk(ObjectContext context, Integer channelId);

	/**
	 * 查询货道信息，包含商品不包含货道主键
	 * @param context
	 * @param skuId 商品标识
	 * @param channelId 货道标识
	 * @param strategyId 策略标识
	 * @return
	 */
	TStVmChannel hasSkuBySkuIdAndNotChannelId(ObjectContext context,
			Integer skuId, Integer channelId,Integer strategyId);

	/**
	 * 查询当前货道下合并的货道（不包含本身）
	 * @param context
	 * @param channelId 货道标识
	 * @return
	 */
	List<TStVmChannel> findByMergeChannelId(ObjectContext context, Integer channelId);
}
