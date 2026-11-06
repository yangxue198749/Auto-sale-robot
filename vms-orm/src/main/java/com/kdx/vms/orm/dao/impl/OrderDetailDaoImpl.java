package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.enumeration.OrderDetailStatus;
import com.kdx.vms.model.enumeration.OrderNotifyStatus;
import com.kdx.vms.model.enumeration.OrderRefundType;
import com.kdx.vms.model.enumeration.OrderVendoutStatus;
import com.kdx.vms.orm.dao.OrderDetailDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by yangshuxi on 2017/11/16.
 * <p>
 * Update by zhouxing on 2017/11/23
 */
@Repository
public class OrderDetailDaoImpl implements OrderDetailDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<?> findSaleOrder(TSysUser sysUser, String innerCode, String orderSeq, String nodeName, String outerSeq, String skuName, Integer status, Integer orderType, Integer payType, String beginDate, String endDate, Integer pageSize) {
        try {
            Expression qualifier = ExpressionUtils.fillCompanyQualifier(null, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COMPANY_PROPERTY, sysUser);
            Expression costExpression = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_STATUS_PROPERTY, 1)
                    .orExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ATTACHMENT_PROPERTY + "." + TOrderMasterAtt.ORDER_SOURCES_PROPERTY, TOrderMasterAtt.API_SOURCES));
            qualifier = qualifier.andExp(costExpression);//通过API创建的订单不判断支付状态
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, innerCode);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_SEQ_PROPERTY, orderSeq);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, nodeName);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY + "." + TOrderCost.COST_DETAIL_PROPERTY + "." + TOrderCostDetail.PAY_OUTER_SEQ_PROPERTY, outerSeq);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.SKU_PROPERTY + "." + TMtSku.SKU_NAME_PROPERTY, skuName);
            if (null != status) {
                switch (status) {
                    case 1://已完成
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.STATUS_PROPERTY, null));
                        break;
                    case 2://待配送
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.STATUS_PROPERTY, OrderDetailStatus.STATUS_PENDING));
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_REFUND_STATUS_PROPERTY, null));
                        break;
                    case 3://已取消
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.STATUS_PROPERTY, OrderDetailStatus.STATUS_PENDING));
                        qualifier = qualifier.andExp(ExpressionFactory.inExp(TOrderDetail.REFUND_PROPERTY + "." + TOrderRefund.TYPE_PROPERTY, OrderRefundType.USER_GOOD_DEPOSIT, OrderRefundType.USER_GOOD));
                        break;
                    case 4://配送中
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.STATUS_PROPERTY, OrderDetailStatus.STATUS_DISTRIBUTION));
                        qualifier = qualifier.andExp(ExpressionFactory.greaterOrEqualExp(TOrderDetail.VALID_TIME_PROPERTY, DateUtils.getTime1()));
                        break;
                    case 5://已送达
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.STATUS_PROPERTY, OrderDetailStatus.STATUS_DELIVERED));
                        break;
                    case 6://未送达
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.STATUS_PROPERTY, OrderDetailStatus.STATUS_DISTRIBUTION));
                        qualifier = qualifier.andExp(ExpressionFactory.lessExp(TOrderDetail.VALID_TIME_PROPERTY, DateUtils.getTime1()));
                        break;
                }
            }
            if (null != orderType) {
                switch (orderType) {
                    case 1://即售
                        Expression q1 = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE).
                                orExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.CARD));
                        qualifier = qualifier.andExp(q1);
                        break;
                    case 2://预定
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.RESERVE));
                        break;
                }
            }
            qualifier = ExpressionUtils.fillPayTypeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY + "." + TOrderCost.PAY_TYPE_PROPERTY, payType);
            qualifier = ExpressionUtils.fillBeginTimeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.C_TIME_PROPERTY, beginDate);
            qualifier = ExpressionUtils.fillEndTimeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.C_TIME_PROPERTY, endDate);
            SelectQuery query = new SelectQuery(TOrderDetail.class, qualifier);
            query.addOrdering(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.C_TIME_PROPERTY, SortOrder.DESCENDING);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ATTACHMENT_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.REFUNDS_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY + "." + TOrderCost.COST_DETAIL_PROPERTY);
            query.addPrefetch(TOrderDetail.SKU_PROPERTY);
            query.addPrefetch(TOrderDetail.SKU_PROPERTY + "." + TMtSku.SKU_IMG_PROPERTY);
            query.addPrefetch(TOrderDetail.DETAIL_COST_PROPERTY);
            query.addPrefetch(TOrderDetail.VENDOUT_PROPERTY);
            query.addPrefetch(TOrderDetail.REFUND_PROPERTY);
            query.addPrefetch(TOrderDetail.DETAIL_COST_PROPERTY);
            if (IntegerUtils.isPositiveInteger(pageSize)) query.setPageSize(pageSize);
            return sysUser.getObjectContext().performQuery(query);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<?> findVendoutList(TSysUser sysUser, String innerCode, String orderSeq, String nodeName,
                                   String skuName, Integer outStatus, Integer payType, String beginDate,
                                   String endDate, Integer pageSize) {
        try {
            Expression qualifier = ExpressionUtils.fillCompanyQualifier(null, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COMPANY_PROPERTY, sysUser);
            Expression costExpression = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_STATUS_PROPERTY, 1)
                    .orExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ATTACHMENT_PROPERTY + "." + TOrderMasterAtt.ORDER_SOURCES_PROPERTY, TOrderMasterAtt.API_SOURCES));
            qualifier = qualifier.andExp(costExpression);//通过API创建的订单不判断支付状态
            qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, null));
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, innerCode);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.SKU_PROPERTY + "." + TMtSku.SKU_NAME_PROPERTY, skuName);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_SEQ_PROPERTY, orderSeq);
            qualifier = ExpressionUtils.fillLikeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, nodeName);
            qualifier = ExpressionUtils.fillPayTypeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY + "." + TOrderCost.PAY_TYPE_PROPERTY, payType);

            //预售，预定，通知状态不为空，售空，离线，售货机超时
            Expression q1 = ExpressionFactory.noMatchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE)
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, null))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SOLD_OUT))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_OFFLINE))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SERVER_TIMEOUT));
            //即售，通知状态不为空
            Expression q2 = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE)
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, null));
            q1 = q1.orExp(q2);
            qualifier = qualifier.andExp(q1);
            if (null != outStatus) {
                switch (outStatus) {
                    case 1://正常
                        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY + ".", OrderVendoutStatus.SUCCESS));
                        break;
                    case 2://失败
                        //即售:出货状态不为空和成功和未上报，或者出货状态为空且通知状态为离线或服务器通讯超时
                        Expression e1 = ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, null)
                                .andExp(ExpressionFactory.matchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_OFFLINE))
                                .andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE));
                        Expression e2 = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE)
                                .andExp(ExpressionFactory.matchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SERVER_TIMEOUT))
                                .andExp(ExpressionFactory.matchExp(TOrderDetail.EXTRACT_CODE_PROPERTY, null));
                        Expression e3 = ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, null)
                                .andExp(ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.SUCCESS))
                                .andExp(ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.NOT_REPORT))
                                .andExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE));
                        //预定,预售:出货状态不为空和成功和未上报和售空
                        Expression e4 = ExpressionFactory.noMatchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE)
                                .andExp(ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, null))
                                .andExp(ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.SUCCESS))
                                .andExp(ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.EMPTY))
                                .andExp(ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.NOT_REPORT));
                        e1 = e1.orExp(e2).orExp(e3).orExp(e4);
                        qualifier = qualifier.andExp(e1);
                        break;
                    case 3://未知
                        //电控未上报
                        Expression e5 = ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, OrderVendoutStatus.NOT_REPORT);
                        //没有出货结果，有通知结果，且通知结果不为离线和服务器通讯超时
                        Expression e6 = ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY, null);
                        e6 = e6.andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, null));
                        e6 = e6.andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SERVER_TIMEOUT));
                        e6 = e6.andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_OFFLINE));
                        e6 = e6.andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SOLD_OUT));
                        e5 = e5.orExp(e6);
                        qualifier = qualifier.andExp(e5);
                        break;
                }
            }
            qualifier = ExpressionUtils.fillBeginTimeQualifier(qualifier, TOrderDetail.NOTIFY_PROPERTY + "." + TOrderDetailNotify.NOTIFY_TIME_PROPERTY, beginDate);
            qualifier = ExpressionUtils.fillEndTimeQualifier(qualifier, TOrderDetail.NOTIFY_PROPERTY + "." + TOrderDetailNotify.NOTIFY_TIME_PROPERTY, endDate);
            SelectQuery query = new SelectQuery(TOrderDetail.class, qualifier);
            query.addOrdering(TOrderDetail.NOTIFY_PROPERTY + "." + TOrderDetailNotify.NOTIFY_TIME_PROPERTY, SortOrder.DESCENDING);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ATTACHMENT_PROPERTY);
            query.addPrefetch(TOrderDetail.VENDOUT_PROPERTY + "." + TOrderDetailVendout.VENDOUT_CHANNEL_PROPERTY);
            query.addPrefetch(TOrderDetail.SKU_PROPERTY);
            query.addPrefetch(TOrderDetail.SKU_PROPERTY + "." + TMtSku.SKU_IMG_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY + "." + TOrderCost.COST_DETAIL_PROPERTY);
            query.addPrefetch(TOrderDetail.DETAIL_COST_PROPERTY);
            query.addPrefetch(TOrderDetail.VENDOUT_PROPERTY);
            query.addPrefetch(TOrderDetail.REFUND_PROPERTY);
            if (IntegerUtils.isPositiveInteger(pageSize)) query.setPageSize(pageSize);
            return sysUser.getObjectContext().performQuery(query);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<?> findVendoutErrorList(TSysUser sysUser, String beginDate, String endDate, Integer pageSize, String innerCode) {
        try {
            Expression qualifier = ExpressionUtils.fillCompanyQualifier(null, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COMPANY_PROPERTY, sysUser);
            Expression costExpression = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_STATUS_PROPERTY, 1)
                    .orExp(ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ATTACHMENT_PROPERTY, TOrderMasterAtt.API_SOURCES));
            qualifier = qualifier.andExp(costExpression);//通过API创建的订单不判断支付状态
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, innerCode);
            Expression q0 = ExpressionFactory.noMatchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY + ".", OrderVendoutStatus.SUCCESS)
                    .orExp(ExpressionFactory.matchExp(TOrderDetail.VENDOUT_STATUS_PROPERTY + ".", null));
            qualifier = qualifier.andExp(q0);//预售，预定，通知状态不为空，售空，离线，售货机超时
            Expression q1 = ExpressionFactory.noMatchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE)
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, null))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SOLD_OUT))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_OFFLINE))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, OrderNotifyStatus.STATUS_SERVER_TIMEOUT));
            //即售，通知状态不为空
            Expression q2 = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.ORDER_TYPE_PROPERTY, TOrderMaster.IMMEDIATE)
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.NOTIFY_STATUS_PROPERTY, null));
            q1 = q1.orExp(q2);
            qualifier = qualifier.andExp(q1);
            qualifier = ExpressionUtils.fillBeginTimeQualifier(qualifier, TOrderDetail.NOTIFY_PROPERTY + "." + TOrderDetailNotify.NOTIFY_TIME_PROPERTY, beginDate);
            qualifier = ExpressionUtils.fillEndTimeQualifier(qualifier, TOrderDetail.NOTIFY_PROPERTY + "." + TOrderDetailNotify.NOTIFY_TIME_PROPERTY, endDate);
            SelectQuery query = new SelectQuery(TOrderDetail.class, qualifier);
            query.addOrdering(TOrderDetail.NOTIFY_PROPERTY + "." + TOrderDetailNotify.NOTIFY_TIME_PROPERTY, SortOrder.DESCENDING);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY);
            query.addPrefetch(TOrderDetail.VENDOUT_PROPERTY + "." + TOrderDetailVendout.VENDOUT_CHANNEL_PROPERTY);
            query.addPrefetch(TOrderDetail.SKU_PROPERTY);
            query.addPrefetch(TOrderDetail.SKU_PROPERTY + "." + TMtSku.SKU_IMG_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY);
            query.addPrefetch(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.COST_PROPERTY + "." + TOrderCost.COST_DETAIL_PROPERTY);
            query.addPrefetch(TOrderDetail.DETAIL_COST_PROPERTY);
            query.addPrefetch(TOrderDetail.VENDOUT_PROPERTY);
            query.addPrefetch(TOrderDetail.REFUND_PROPERTY);
            if (IntegerUtils.isPositiveInteger(pageSize)) query.setPageSize(pageSize);
            return sysUser.getObjectContext().performQuery(query);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public TOrderDetail findByDetailId(ObjectContext context, Integer detailId) {
        try {
            ObjectId id = new ObjectId(TOrderDetail.class.getSimpleName(), TOrderDetail.ORDER_DETAIL_ID_PK_COLUMN, detailId);
            // 表示刷新缓存，从表中从新获取数据
            ObjectIdQuery query = new ObjectIdQuery(id, false, ObjectIdQuery.CACHE_REFRESH);
            return (TOrderDetail) Cayenne.objectForQuery(context, query);
        } catch (Exception e) {
            logger.error("select error:" + e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TOrderDetailNotify findNotify(ObjectContext context, Integer orderDetailId) {
        try {

            Expression expression = ExpressionFactory.matchExp(TOrderDetailNotify.DETAIL_PROPERTY, orderDetailId);
            SelectQuery query = new SelectQuery(TOrderDetailNotify.class, expression);

            List<TOrderDetailNotify> list = context.performQuery(query);
            if (null != list && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<?> findPayType(ObjectContext context) {
        return context.performQuery(new SelectQuery(TMtCardType.class));
    }

    @Override
    public List<?> findTodayOrder(TMtVm mtVm) {
        try {
            Expression expression = ExpressionFactory.matchExp(TOrderDetail.ORDER_PROPERTY + "." + TOrderMaster.MT_VM_PROPERTY, mtVm)
                    .andExp(ExpressionFactory.matchExp(TOrderDetail.VALID_TIME_PROPERTY, DateUtils.getTime1()))
                    .andExp(ExpressionFactory.noMatchExp(TOrderDetail.EXTRACT_CODE_PROPERTY, null));
            SelectQuery query = new SelectQuery(TOrderDetail.class, expression);
            return mtVm.getObjectContext().performQuery(query);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }
}
