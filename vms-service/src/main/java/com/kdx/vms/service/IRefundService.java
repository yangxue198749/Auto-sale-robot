package com.kdx.vms.service;

import com.kdx.vms.model.bean.TOrderRefund;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.enumeration.OrderRefundType;
import org.apache.cayenne.ObjectContext;

/**
 * 退款
 * Created by zhangguodong on 2017/11/17.
 */
public interface IRefundService {
    public TOrderRefund launchRefund(ObjectContext context, OrderRefundType type, String topic, Integer... goodIds);
    public TOrderRefund launchRefund(TSysUser user, OrderRefundType type, String topic, Integer... goodIds);
}
