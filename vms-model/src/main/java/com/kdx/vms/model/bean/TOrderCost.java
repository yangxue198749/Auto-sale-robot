package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TOrderCost;

public class TOrderCost extends _TOrderCost {
    /**
     * 支付结果:成功
     */
    public static final int COST_STATUS_SUCCESS = 1;
    /**
     * 支付结果:失败
     */
    public static final int COST_STATUS_FAIL = 2;

    /**
     * 支付关闭
     */
    public static final int COST_STATUS_CLOSE = -1;
    /**
     * 微信支付
     */
    public static final int WECHAT_PAY = 1101;
    /**
     * 支付宝支付
     */
    public static final int ALI_PAY = 1201;
    /**
     * 银联支付
     */
    public static final int UNION_PAY = 1301;

    public String getOuterSeq() {
        if (null == getCostDetail() || getCostDetail().size() == 0) return "";
        for (TOrderCostDetail tOrderCostDetail : getCostDetail()) {
            if (tOrderCostDetail.getCostType() == null) continue;
            switch (tOrderCostDetail.getCostType()) {
                case WECHAT_PAY:
                    return tOrderCostDetail.getPayOuterSeq();
                case ALI_PAY:
                    return tOrderCostDetail.getPayOuterSeq();
                case UNION_PAY:
                    return tOrderCostDetail.getPayOuterSeq();
                default:
                    continue;
            }
        }
        //如果在没有符合以上条件，取第一条
        return getCostDetail().get(0).getPayOuterSeq();
    }
}
