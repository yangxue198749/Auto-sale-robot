package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TOrderRefund;

public class TOrderRefund extends _TOrderRefund {

    /** 退款状态:新创见退款订单 */
    public static final int REFUND_STATUS_CREATE   = 0;
    /** 退款状态:退款通知成功 */
    public static final int REFUND_STATUS_NOTICE_SUCCESS  = 1;
    /** 退款状态:退款通知失败 */
    public static final int REFUND_STATUS_NOTICE_FAIL   = 2;
    /** 退款状态:退款到账 */
    public static final int REFUND_STATUS_SUCCESS  = 3;
    /** 退款状态:退款失败 */
    public static final int REFUND_STATUS_FAIL   = 4;
    /** 退款状态:退款处理中 */
    public static final int REFUND_STATUS_PROCESSING   = 5;
    /** 退款状态:转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款。 */
    public static final int REFUND_STATUS_CHANGE   = 6;

}
