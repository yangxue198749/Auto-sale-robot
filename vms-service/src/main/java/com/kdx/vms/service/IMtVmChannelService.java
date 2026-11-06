package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmChannel;
import com.kdx.vms.model.bean.TSysUser;

import com.kdx.vms.model.custom.ChannelConfigCache;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/13.
 */
public interface IMtVmChannelService {
    /**
     * 查看货道
     */
    String makeStVmChannelTableString(TMtVm mtVm,ChannelConfigCache cache);

    /**
     * 合并货道
     * @param firstChannelId
     * @param secondChannelId
     * @return
     */
    String mergeChannel(Integer firstChannelId, Integer secondChannelId,ChannelConfigCache cache,TMtVm mtVm);

    /**
     * 取消关联
     * @param channelIds 货道标识数组
     * @return
     */
    String cancelRelateSku(ChannelConfigCache cache, List<Integer> channelIds);

    /**
     * 关联商品
     * @param channelIds 货道标识数组
     * @param skuId 商品标识
     * @return resultMap中有三个key 。type,message,capaticy;type-->success/error;message-->失败信息;capaticy-->最大容量
     */
    Map<String,Object> relateSku(TSysUser user,ChannelConfigCache cache, Integer[] channelIds, Integer skuId);

    /**
     * 检测货道能否容纳商品
     * @param channelIds 货道标识
     * @param skuId 商品标识
     * @return resultMap中有三个key 。type,message,capaticy;type-->success/error;message-->失败信息;capaticy-->最大容量
     */
    Map<String,Object> checkSku(TSysUser sysUser, ChannelConfigCache cache, Integer[] channelIds, Integer skuId);

    /**
     * 解除合并
     * @param channelId 货道标识
     * @return
     */
    String cancelMergeChannel(ChannelConfigCache cache, Integer channelId);

    /**
     * 查看列表页策略
     * @param context
     * @param vmId
     * @return
     */
    String makeStringSort(ObjectContext context,Integer vmId);

    /**
     * 列表页位置互换
     * @param context
     * @param sysUser 操作用户
     * @param firstNumber 第一个位置序号
     * @param secondNumber 第二个位置序号
     * @return
     */
    Map<String, Object> changeSort(ObjectContext context,
                                   TSysUser sysUser, Integer firstNumber, Integer secondNumber,Integer vmId);

//    void copyInfoToNext(ObjectContext context,Integer vmId,TSysUser currentUser);

    public Map<String, Object> saveChannel(TSysUser currentUser, ObjectContext context, Integer[] ids, Integer strategyId);
    
    /**
     * 获取货道信息
     * @param context
     * @param mtVm 售货机
     * @param channelCode 货道编码
     * @return
     */
    public TMtVmChannel findByChannelCode(ObjectContext context,TMtVm mtVm,String channelCode);

    /**
     * 复制一个货架
     * @param context
     * @param sysUser
     * @param sortNumber
     * @param vmId
     * @return
     */
    public Map<String,Object> copySort(ObjectContext context,
                                       TSysUser sysUser, Integer sortNumber,Integer vmId);

    /**
     * 删除一个货架
     * @param context
     * @param sysUser
     * @param sortNumber
     * @param vmId
     * @return
     */
    public Map<String,Object> deleteSort(ObjectContext context,TSysUser sysUser,Integer sortNumber,Integer vmId);



    public ChannelConfigCache getChannelCache(TSysUser sysUser,TMtVm mtVm);
    public void deleteChannelCache(Integer vmId);
    public void updateChannelCache(ChannelConfigCache cache);
    public void saveChannelConfig(TMtVm mtVm,TSysUser sysUser,ChannelConfigCache cache);
    public Map<String, Object> adjustCapacity(ChannelConfigCache cache, Integer channelId,Integer adjustNum);

    Map<String,Object> setFullSku(ObjectContext context, TMtVm mtVm, String channelCode,Integer fullval);
}
