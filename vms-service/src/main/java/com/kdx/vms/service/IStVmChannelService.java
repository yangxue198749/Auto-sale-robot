package com.kdx.vms.service;

import com.kdx.vms.model.bean.TStVmChannel;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * 货道策略
 * @author zhangguodong
 *
 */
public interface IStVmChannelService {

	/**
	 * 新建货道策略
	 * @param context
	 * @param user 操作人
	 * @param vmModelId 售货机型号
	 * @param stVmName 货道策略名字
	 * @param stVmDesc 货道策略描述
	 * @return
	 */
	String addStVmChannel(ObjectContext context,TSysUser user,Integer vmModelId,String stVmName,String stVmDesc);
	
	String makeStVmChanenlTableString(ObjectContext context,Integer strategyId, Boolean showChannel);
	
	/**
	 * 合并货道
	 * @param context
	 * @param firstChannelId
	 * @param secondChannelId
	 * @param user
	 * @return
	 */
	String mergeChannel(ObjectContext context,Integer firstChannelId,Integer secondChannelId,TSysUser user );
	
	/**
	 * 取消关联
	 * @param context
	 * @param user 操作人
	 * @param channelIds 货道标识数组
	 * @param submit 是否提交
	 * @return
	 */
	String cancelRelateSku(ObjectContext context,TSysUser user,List<Integer> channelIds,boolean submit);
	
	/**
	 * 关联商品
	 * @param context
	 * @param user 操作人
	 * @param channelIds 货道标识数组
	 * @param skuId 商品标识
	 * @return resultMap中有三个key 。type,message,capaticy;type-->success/error;message-->失败信息;capaticy-->最大容量
	 */
	Map<String,Object> relateSku(ObjectContext context,TSysUser user,Integer[] channelIds,Integer skuId);
	
	/**
	 * 检测货道能否容纳商品
	 * @param context
	 * @param channelIds 货道标识
	 * @param skuId 商品标识
	 * @return resultMap中有三个key 。type,message,capaticy;type-->success/error;message-->失败信息;capaticy-->最大容量
	 */
	Map<String,Object> checkSku(ObjectContext context,Integer[] channelIds,Integer skuId);
	
	/**
	 * 解除合并
	 * @param context
	 * @param chanelId 货道标识
	 * @param user 操作人
	 * @return
	 */
	String cancelMergeChannel(ObjectContext context,Integer chanelId,TSysUser user);
	
	/**
	 * 查看列表页策略
	 * @param context
	 * @param strategyId
	 * @return
	 */
	String makeStringSort(ObjectContext context,Integer strategyId);

	List getPage(ObjectContext objectContext, TSysUser sysUser,
			String searchStVmName, Integer searchVmModelId, Integer pageSize);

	TStVmChannel findByPk(ObjectContext objectContext, Integer firstChannelId);

	/**
	 * 列表页位置互换
	 * @param objectContext
	 * @param sysUser 操作用户
	 * @param firstSortId 第一个位置标识
	 * @param secondSortId 第二个位置标识
	 * @return
	 */
	Map<String, Object> changeSort(ObjectContext objectContext,
			TSysUser sysUser, Integer firstSortId, Integer secondSortId);

	/**
	 * 复制一个货架
	 * @param context
	 * @param sysUser
	 * @param sortId
	 * @return
	 */
	Map<String,Object> copySort(ObjectContext context,TSysUser sysUser, Integer sortId);

	/**
	 * 删除一个货架
	 * @param context
	 * @param sysUser
	 * @param sortId
	 * @return
	 */
	Map<String,Object> deleteSort(ObjectContext context,TSysUser sysUser,Integer sortId);

	Map<String,Object> setFullSku(ObjectContext context, Integer channelId, Integer fullval);
}
