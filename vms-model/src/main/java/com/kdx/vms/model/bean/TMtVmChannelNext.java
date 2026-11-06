package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.model.bean.auto._TMtVmChannelNext;
import com.kdx.vms.model.custom.ChannelConfigCache;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;
import java.util.Map;

public class TMtVmChannelNext extends _TMtVmChannelNext {

    /**
     * 默认的当前余量
     */
    public static int DEFAULT_CURRNUM = 0;

//    public void setProperties(TMtVmChannel mtVmChannel) {
//        if (null != mtVmChannel.getMergePid()) {
//            TMtVmChannel pMtVmChannel = Cayenne.objectForPK(objectContext, TMtVmChannel.class, mtVmChannel.getMergePid());
//            String channelCode = pMtVmChannel.getChannelCode();
//            TMtVm mtVm = this.getMtVm();
//            Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
//            TMtVmChannelNext pTMtVmChannelNext = map.get(channelCode);
//            this.setMergePid(pTMtVmChannelNext.getChannelId());
//        }
//        this.setMergeQty(mtVmChannel.getMergeQty());
//        this.setSku(mtVmChannel.getSku());
//        this.setMaxCapacity(mtVmChannel.getMaxCapacity());
//    }

    public void setProperties(TStVmChannel stVmChannel) {
        if (null != stVmChannel.getMergePid()) {
            TStVmChannel pStVmChannel = Cayenne.objectForPK(objectContext, TStVmChannel.class, stVmChannel.getMergePid());
            String channelCode = pStVmChannel.getChannelCode();
            TMtVm mtVm = this.getMtVm();
            Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
            TMtVmChannelNext pTMtVmChannelNext = map.get(channelCode);
            this.setMergePid(pTMtVmChannelNext.getChannelId());
        }
        this.setMergeQty(stVmChannel.getMergeQty());
        this.setSku(null == stVmChannel.getSkuId() || stVmChannel.getSkuId() == 0 ? null : Cayenne.objectForPK(objectContext, TMtSku.class, stVmChannel.getSkuId()));
        this.setMaxCapacity(stVmChannel.getMaxCapacity());
        this.setAdjustMaxCapacity(stVmChannel.getTempMaxCapacity());
    }

    public void setProperties(ChannelConfigCache.ChannelConfig channelConfig) {
        this.setMergePid(channelConfig.getMergePid());
        this.setMergeQty(channelConfig.getMergeQty());
        this.setSku(!IntegerUtils.isPositiveInteger(channelConfig.getSkuId()) ? null : Cayenne.objectForPK(objectContext, TMtSku.class, channelConfig.getSkuId()));
        this.setMaxCapacity(channelConfig.getMaxCapacity());
        this.setAdjustMaxCapacity(channelConfig.getAdjustMaxCapacity());
    }


    public Integer getChannelId() {
        return Cayenne.intPKForObject(this);
    }

    public List<TMtVmChannelNext> findMergedChannels(List<TMtVmChannelNext> list) {
        Expression qualifier = ExpressionFactory.matchExp(TMtVmChannelNext.MERGE_PID_PROPERTY, this.getChannelId());
        return qualifier.filterObjects(list);
//
//        SelectQuery select = new SelectQuery(TMtVmChannelNext.class, qualifier);
//        return objectContext.performQuery(select);
    }


    public Integer getMaxNum() {
        if(IntegerUtils.isPositiveInteger(super.getAdjustMaxCapacity()))
            return super.getAdjustMaxCapacity();
        Integer maxNum = getMaxCapacity();
        if (getIsFixedCapacity() == 1) {
            //固定容量
            maxNum = getFixedCapacity();
        }
        if (null == maxNum) maxNum = 0;
        return maxNum;
    }

}
