package com.kdx.vms.service;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmModelChannel;
import com.kdx.vms.model.bean.TSysUser;

public interface IMtVmModelChannelService {

	/**
	 * 创建售货机型号配置
	 * @param modelId
	 * @param list
	 * @param user
	 * @param context
	 * @return Boolean
	 * @date 2016年12月6日 上午11:05:31
	 * @author chenifan
	 */
	public Boolean createVmModelChannel(String modelId ,List<TMtVmModelChannel> list ,TSysUser user, ObjectContext context);
	
	/**
	 * 修改售货机型号
	 * @param modelId
	 * @param list
	 * @param user
	 * @param context
	 * @return
	 * @return Boolean
	 * @date 2016年12月6日 上午11:06:08
	 * @author chenifan
	 * @QQ 995998760
	 */
	public Boolean updateVmModelChannel(String modelId ,List<TMtVmModelChannel> list ,TSysUser user, ObjectContext context);
	
	/**
	 * 根据货道行编码得到这一行的深高和横竖
	 * @param vmModelId
	 * @param channelCode
	 * @return
	 * @return TMtVmModelChannel
	 * @date 2016年12月14日 上午11:27:06
	 * @author chenifan
	 * @QQ 995998760
	 */
	public TMtVmModelChannel getVmModelChannelRowByCode(String vmModelId ,String channelCode);
	
	/**
	 * 根据货道编码得到TMtVmModelChannel
	 * @param vmModelId
	 * @param channelCode
	 * @return
	 * @return TMtVmModelChannel
	 * @date 2016年12月14日 上午11:27:06
	 * @author chenifan
	 * @QQ 995998760
	 */
	public TMtVmModelChannel getVmModelChannelByCode(String vmModelId ,String channelCode);
	
	/**
	 * 设置售货机型号配置行
	 * @param channelCode 货道行编码
	 * @param vmModelId 售货机型号id
	 * @param channelLength 深度
	 * @param channelHeight 高度
	 * @param displayMode 摆放方式
	 * @param user
	 * @param context
	 * @return Boolean
	 */
	public Integer setModelChannelRow(Integer channelCode ,String vmModelId ,Integer channelLength ,Integer channelHeight ,Integer displayMode ,TSysUser user, ObjectContext context);
	
	/**
	 * 设置售货机型号配置
	 * @param channelCode
	 * @param vmModelId
	 * @param channelWidth
	 * @param acceptWidthMax
	 * @param isFixedCapacity
	 * @param fixedCapacity
	 * @param user
	 * @param context
	 * @return
	 * @return Boolean
	 * @date 2016年12月14日 下午2:01:21
	 * @author chenifan
	 * @QQ 995998760
	 */
	public Integer setModelChannel(String channelCode ,String vmModelId ,Integer channelWidth ,Integer acceptWidthMax ,Integer isFixedCapacity ,Integer fixedCapacity ,TSysUser user, ObjectContext context);
	
	/**
	 * 新增默认货道配置
	 * @param tMtVmModel
	 * @param user
	 * @param context
	 * @return Boolean
	 * @date 2016年12月21日 下午3:11:24
	 * @author chenifan
	 * @QQ 995998760
	 */
	public Boolean createModelChannel(TMtVmModel tMtVmModel ,TSysUser user, ObjectContext context);
}
