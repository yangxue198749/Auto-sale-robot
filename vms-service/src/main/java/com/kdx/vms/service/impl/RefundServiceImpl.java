package com.kdx.vms.service.impl;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderMaster;
import com.kdx.vms.model.bean.TOrderRefund;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.enumeration.OrderRefundStatus;
import com.kdx.vms.model.enumeration.OrderRefundType;
import com.kdx.vms.mq.PublisherPool;
import com.kdx.vms.service.IRefundService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhangguodong on 2017/11/17.
 */
@Service("refundService")
public class RefundServiceImpl implements IRefundService {
    Logger logger = LoggerFactory.getLogger(getClass());
    public TOrderRefund launchRefund(ObjectContext context, OrderRefundType type,String topic, Integer... goodIds) {
        try {
            // 创建退款数据库记录
            TOrderRefund refund = createRefundOrder(context, type, goodIds);
                if (null != refund) {
                    Map<String, Object> head = new HashMap<>();
                    head.put("ts", System.currentTimeMillis());
                    Map<String, Object> body = new HashMap<>();
                    body.put("seq", refund.getRefundSeq());
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("head", head);
                msg.put("body", body);
                PublisherPool.getInstance().publish((String) PropertiesLoader.map.get("payExchangeName"),JSON.toJSONString(msg), topic);
                return refund;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public TOrderRefund launchRefund(TSysUser user, OrderRefundType type, String topic, Integer... goodIds) {
        try {
            // 创建退款数据库记录
            TOrderRefund refund = createRefundOrder(user.getObjectContext(), type, goodIds);
            if (null != refund) {
                refund.setCtor(user.getId());
                user.getObjectContext().commitChanges();
                Map<String, Object> head = new HashMap<>();
                head.put("ts", System.currentTimeMillis());
                Map<String, Object> body = new HashMap<>();
                body.put("seq", refund.getRefundSeq());
                Map<String, Object> msg = new HashMap<>();
                msg.put("head", head);
                msg.put("body", body);
                PublisherPool.getInstance().publish((String) PropertiesLoader.map.get("payExchangeName"),JSON.toJSONString(msg), topic);
                return refund;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private TOrderRefund createRefundOrder(ObjectContext context, OrderRefundType type, Integer... goodIds) {
        logger.info(Arrays.asList(context, type, goodIds).toString());
        if (null == goodIds || goodIds.length == 0 || null == context || null == type)
            return null;
        TOrderRefund orderRefund = null;
        switch (type) {
            case VMS_GOOD:
                orderRefund = createOrderRefund(onlyGood(context, goodIds), type);
                break;
            case AUTO_GOOD:
                orderRefund = createOrderRefund(onlyGood(context, goodIds), type);
                break;
        }
        if (Objects.nonNull(orderRefund)) {
            context.commitChanges();
        }
        return orderRefund;
    }

    private TOrderRefund createOrderRefund(TOrderRefund orderRefund, OrderRefundType type) {
        return Optional.ofNullable(orderRefund).map(refund -> {
            if (type.equals(OrderRefundType.AUTO_GOOD_DEPOSIT) ) {
                refund.setType(OrderRefundType.AUTO_GOOD);
            } else if (type.equals(OrderRefundType.USER_GOOD_DEPOSIT)) {
                refund.setType(OrderRefundType.USER_GOOD);
            } else {
                refund.setType(type);
            }
            return refund;
        }).orElse(null);
    }


    /**
     * 只退商品
     *
     * @param context
     * @param goodIds
     * @return
     */
    private TOrderRefund onlyGood(ObjectContext context, Integer[] goodIds) {
        Integer id = Optional.ofNullable(goodIds)
                .filter(ids -> ids.length == 1)
                .map(ids -> ids[0])
                .orElseThrow(() -> new IllegalArgumentException("goodIds only one id."));
        TOrderDetail orderDetail = Cayenne.objectForPK(context, TOrderDetail.class, id);
        if (Objects.nonNull(orderDetail)) {
            TOrderMaster order = Optional.of(orderDetail)
                    .map(TOrderDetail::getOrder)
                    .orElseThrow(() -> new IllegalStateException("data error, not find order."));
            if (Objects.isNull(orderDetail.getRefund())) {
                TOrderRefund orderRefund = context.newObject(TOrderRefund.class);
                orderRefund.setRefundTime(new Date());
                orderRefund.setAmount(orderDetail.getRealPrice());
                orderRefund.setOrderRefundStatus(OrderRefundStatus.CREATE);
                orderRefund.setRefundSeq(order.getOrderSeq() + "_" + Optional.ofNullable(order.getRefunds()).map(List::size).orElse(0));
                orderRefund.setDetail(orderDetail);
                orderRefund.setOrder(order);
                orderDetail.setOrderRefundStatus(OrderRefundStatus.CREATE);
                return orderRefund;
            }
        }
        return null;
    }

}
