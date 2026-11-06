package com.kdx.vms.service;

import com.kdx.vms.commons.util.mcs.message.data.Extention;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.custom.DeliveryTaskJson;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * 与mcs通信接口
 * @author zhangguodong
 *
 */
public interface IMcsService {

	String deliveryTaskFinish(DeliveryTaskJson deliveryTaskJson);

	/**
	 * 将走马灯信息发给MCS
	 * @param context
	 * @param vmIdList 售货机标识集合
	 * @param sn
	 * @return 失败返回失败原因，成功则返回空字符串
	 */
	String pushText(ObjectContext context,List<Integer> vmIdList,Long sn);

	/**
	 * 将闪购键信息发给MCS
	 * @param context
	 * @param vmIdList 售货机标识集合
	 * @param sn
	 * @return 失败返回失败原因，成功则返回空字符串
	 */
	String pushShortCuts(ObjectContext context,List<Integer> vmIdList,Long sn);

	/**
	 * 将节能信息发给MCS
	 * @param context
	 * @param vmIdList 售货机标识集合
	 * @param sn
	 * @return 失败返回失败原因，成功则返回空字符串
	 */
	String pushEnergy(ObjectContext context,List<Integer> vmIdList,Long sn);

	/**
	 * 商品价格有变更，同步商品价格
	 * @param context
	 * @param mtVm 售货机
	 * @param isMustSend 是否必须发送  true是
	 * @return 失败返回失败原因，成功则返回空字符串
	 */
	String sendSkuPriceToMcs(ObjectContext context,TMtVm mtVm,boolean isMustSend);

	/**
	 * 获取开门密码
	 * @param context
	 * @param vmId 售货机标识
	 * @param sn
	 * @return 失败返回失败原因，成功则返回空字符串
	 */
	String getOpenDoorPwd(ObjectContext context,Integer vmId,Long sn);

    /**
     * 主动通知开门密码
     * @param context
     * @param vmId 售货机标识
     */
    void notifyOpenDoorPwd(ObjectContext context, Integer vmId);

	/**
	 * 下发商品信息
	 * @param context
	 * @param mtVm
	 * @param isCommit
	 * @param isMustCurr
	 * @param sn
	 * @return
	 */
	public String sendSkuToMcs(ObjectContext context,TMtVm mtVm,boolean isCommit,boolean isMustCurr,Long sn);

	public String sendInnerCodeToMcs(TMtVm mtVm,String uniqueCode);

	/**
	 * 下发维修工单完成通知
	 * @param innerCode
	 * @return
	 */
	public String finishRepairTask(String innerCode);

	/**
	 * 下发货道配置
	 * @param mtVm 售货机
	 * @param isMustCurr 是否当前货道
	 * @param sn
	 * @return
	 */
	String sendChannelPolicy(TMtVm mtVm,boolean isMustCurr ,Long sn);

    /**
     * 下发基本信息 区域 点位 售货机 客户Id 场景
     * @param context
     * @return
     */
    String sendArea(ObjectContext context, String form, Long sn);

    /**
     * 通过phone获取用户id
     * @param context
     * @param phone
     * @param form
     * @param sn
     * @return
     */
    String sendBCustomer(ObjectContext context, String phone, String form, Long sn);

    /**
     * 下发售货机基本信息
     * @param context
     * @param innerCode
     * @param form
     * @param sn
     * @return
     */
    String sendVmBaseInfo(ObjectContext context, String innerCode, String form, Long sn);

	String openAllBox(String innerCode);

	/**
	 * 下发货架信息
	 * @param context
	 * @param innerCode
	 * @param sn
	 * @return
	 */
	String sendSkuOrder(ObjectContext context,String innerCode,Long sn);

	String sendWithdrawVmTaskInfo(ObjectContext context,String innerCode,Long sn);

	String dealWithdraw(ObjectContext context,TMtVm mtVm);


	/**
	 * 将节能信息发给MCS（new）
	 * 5.0.0a 一拖多
	 * @param context
	 * @param vmIdList 售货机标识集合
	 * @param sn
	 * @return 失败返回失败原因，成功则返回空字符串
	 */
	String pushEnergyNew(ObjectContext context,List<Integer> vmIdList,Long sn);

	/**
	 * 将入口配置信息发给MCS
	 * @param context
	 * @param vmIdList
	 * @param sn
	 * @return
	 */
	String sendEntrance(ObjectContext context,List<Integer> vmIdList, Long sn);

	String sendDeployVmTaskInfo(ObjectContext context, String innerCode, Long sn, List<Extention> list);

	/**
	 * 下发商品信息
	 * 5.0.0a
	 * @param context
	 * @param mtVm
	 * @param isCommit
	 * @param isMustCurr
	 * @param sn
	 * @return
	 */
	 String sendSkuInfoToMcs(ObjectContext context,TMtVm mtVm,boolean isCommit,boolean isMustCurr,Long sn);
}
