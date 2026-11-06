package com.kdx.vms.model.bean;

import com.kdx.vms.commons.cache.CardCache;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.excel.ExcelField;
import com.kdx.vms.model.bean.auto._TOrderDetail;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.enumeration.OrderNotifyStatus;
import com.kdx.vms.model.enumeration.PayType;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class TOrderDetail extends _TOrderDetail {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    private static final long REFUND_MAX_MILLIS = 1000 * 60 * 60 * 24 * 7;
    private static final long REFUND_MIN_MILLIS = 1000 * 60 * 5;

    @ExcelField(title = "支付方式", align = 2, sort = 8)
    public String getCostTypeName() {
        return Optional.of(this).map(TOrderDetail::getOrder).map(TOrderMaster::getCost).map(TOrderCost::getPayType).map(CardCache::get).orElse("未知");
//        if (null == getDetailCost()) return "未知";
//        return "微信";
    }

    @ExcelField(title = "支付金额", align = 2, sort = 8)
    public Integer getCostMoney() {
        int i = 0;
        if (null == getDetailCost()) return 0;
        for (TOrderDetailCost cost : getDetailCost()) {
            if (!Objects.equals(2000, cost.getCostType())) {
                i += cost.getCostPrice();
            }
        }
        return i;
    }

    @ExcelField(title = "卡包支付金额", align = 2, sort = 8)
    public Integer getCardCostMoney() {
        int i = 0;
        if (null == getDetailCost()) return 0;
        for (TOrderDetailCost cost : getDetailCost()) {
            if (cost.getCostType().equals(PayType.FICT_CARD.getCode())) {
                i += cost.getCostPrice();
            }
        }
        return i;
    }

    @ExcelField(title = "商品金额", align = 2, sort = 8)
    public String getSkuMoney() {
        BigDecimal b1 = new BigDecimal(StringUtil.formatAmount(getCostMoney()));
        BigDecimal b2 = new BigDecimal(StringUtil.formatAmount(getCardCostMoney()));
        BigDecimal b3 = b1.add(b2);
        return b3.toString();
    }


    @ExcelField(title = "折后额", align = 2, sort = 10)
    public BigDecimal getDeduction(Integer cardCostMoney) {
        /*DecimalFormat df=new DecimalFormat("#.000000");*/
        BigDecimal b4 = new BigDecimal(0);
        if (null == getDetailCost()) return b4;
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        for (TOrderDetailCost cost : getDetailCost()) {
            if (cost.getCostType().equals(PayType.FICT_CARD.getCode())) {
                Expression expression = ExpressionFactory.matchExp(TMallApplyMaster.CARD_NUMBER_PROPERTY, cost.getCostAccount());
                SelectQuery selectQuery = new SelectQuery(TMallApplyMaster.class, expression);
                List<TMallApplyMaster> list = context.performQuery(selectQuery);
                if (list.size() != 0 && null != list) {
                    TMallApplyMaster tMallApplyMaster = list.get(0);
                    BigDecimal b1 = new BigDecimal(tMallApplyMaster.getPayAmount());
                    BigDecimal b2 = new BigDecimal(tMallApplyMaster.getTotalAmount());
                    BigDecimal b3 = b1.divide(b2, 6, BigDecimal.ROUND_HALF_UP);
                    BigDecimal b5 = new BigDecimal(cost.getCostPrice());
                    b5 = b3.multiply(b5);
                    b4 = b4.add(b5);
                }
            }
        }
        return b4;
    }

    @ExcelField(title = "出货状态", align = 2, sort = 15)
    public String getOutStatusName() {
        if (null == getVendoutStatus()) {
            if (getOrder().getOrderType().equals(TOrderMaster.IMMEDIATE) || getOrder().getOrderType().equals(TOrderMaster.CARD)) {
                if (null != getNotifyStatus() && (Objects.equals(TOrderDetailNotify.NOTIFY_STATUS_OFFLINE, getNotifyStatus().getCode()) || (Objects.equals(TOrderDetailNotify.NOTIFY_STATUS_SERVER_TIMEOUT, getNotifyStatus().getCode())))) {
                    return "失败";
                }
                if (getOrder().getAttachment().getOrderScene() == TOrderMasterAtt.SCENE_ONLINE && null == getNotifyStatus()) {
                    return "";
                }
                return "未知";
            } else {
                if(null==getNotifyStatus()|| OrderNotifyStatus.STATUS_SOLD_OUT.equals(getNotifyStatus())||OrderNotifyStatus.STATUS_SOLD_OUT.equals(getNotifyStatus())){
                    return "";
                }else{
                    return "未知";
                }
            }
        }
        switch (getVendoutStatus()) {
            case TOrderDetailVendout.OUT_STATUS_SUCCESS:
                return "成功";
            case TOrderDetailVendout.OUT_STATUS_FAIL_EMPTY:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_BROKEN:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_OUT_TIMEOUT:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_COMMUNICATION_TIMEOUT:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_MEANWHILE:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_ORDER_TIMEOUT:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_BUSY:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_NOT_REPORT:
                return "未知";
            case TOrderDetailVendout.OUT_STATUS_FAIL_PRINTER_BROKEN:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_NOT_EXISTENT:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_FAIL_DEVICE_NOT_EXISTENT:
                return "失败";
            case TOrderDetailVendout.OUT_STATUS_UNKNOWN:
                return "未知";
            default:
                return "未知";
        }
    }

    @ExcelField(title = "失败原因", align = 2, sort = 16)
    public String getOutStatusFailReason() {
        String value = null;
        if (null != getVendoutStatus()) {
            switch (getVendoutStatus()) {
                case TOrderDetailVendout.OUT_STATUS_SUCCESS:
                    value = "";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_EMPTY:
                    value = "货道售空";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_BROKEN:
                    value = "货道故障";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_OUT_TIMEOUT:
                    value = "出货超时";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_COMMUNICATION_TIMEOUT:
                    value = "通讯超时";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_MEANWHILE:
                    value = "连续支付";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_ORDER_TIMEOUT:
                    value = "接收订单超时";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_BUSY:
                    value = "设备忙碌";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_NOT_REPORT:
                    value = "电控未上报结果";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_PRINTER_BROKEN:
                    value = "打印机故障";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_NOT_EXISTENT:
                    value = "货道不存在";
                    break;
                case TOrderDetailVendout.OUT_STATUS_FAIL_DEVICE_NOT_EXISTENT:
                    value = "设备不存在或离线";
                    break;
                default:
                    value = null;
            }
        }
        if (null == value && null != getNotifyStatus()) {
            switch (getNotifyStatus()) {
                //                case NOTIFY_STATUS_FAIL:
                //                    value = "通知失败";
                //                    break;
                case STATUS_OFFLINE:
                    value = getNotify().getNotifyStatus().getTitle();
                    break;
                //                case NOTIFY_STATUS_ERROR:
                //                    value = "通知出错";
                //                    break;
                case STATUS_SERVER_TIMEOUT:
                    value = getNotify().getNotifyStatus().getTitle();
                    break;
                default:
                    value = null;
            }
        }
        return null == value ? "未知" : value;
    }


//    @ExcelField(title = "失败原因", align = 2, sort = 15)
//    public String getOutStatusFailReason() {
//        if (null == getVendout() || null == getVendout().getVendoutStatus()) {
//            return "";
//        }
//        switch (getVendout().getVendoutStatus()) {
//            case TOrderDetailVendout.OUT_STATUS_FAIL_EMPTY:
//                return OrderVendoutStatus.EMPTY.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_BROKEN:
//                return OrderVendoutStatus.CHANNEL_BROKEN.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_OUT_TIMEOUT:
//                return OrderVendoutStatus.OUT_TIMEOUT.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_COMMUNICATION_TIMEOUT:
//                return OrderVendoutStatus.COMMUNICATION_TIMEOUT.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_MEANWHILE:
//                return OrderVendoutStatus.MEANWHILE.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_ORDER_TIMEOUT:
//                return OrderVendoutStatus.OUT_TIMEOUT.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_BUSY:
//                return OrderVendoutStatus.BUSY.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_NOT_REPORT:
//                return OrderVendoutStatus.NOT_REPORT.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_PRINTER_BROKEN:
//                return OrderVendoutStatus.PRINTER_BROKEN.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_NOT_EXISTENT:
//                return OrderVendoutStatus.CHANNEL_MISSING.getFailReason();
//            case TOrderDetailVendout.OUT_STATUS_UNKNOWN:
//                return OrderVendoutStatus.NULL.getFailReason();
//            default:
//                return "未知";
//        }
//    }


    public String getRefundButton() {
        if (canRefund()) {
            return "<button type=\"button\" class=\"btn btn-danger btn-sm btn_refund\" href=\"/vendout/refund\" data-sale-id=\"" + getId() + "\" >退款</button>";
        }
        return "";
    }

    public boolean canRefund() {
        TOrderRefund tOrderRefund = this.getRefund();
        if (null == tOrderRefund) {
//            if (null == getVendout() || getVendout().getVendoutStatus() != TOrderDetailVendout.OUT_STATUS_SUCCESS) {
            if (!getOrder().getOrderType().equals(TOrderMaster.CARD)) {
                return null != getOrder() && !(null == getOrder().getCost() || null == getOrder().getCost().getPayTime()) && inRefundTime();
            }
//            }
        }
        return false;
    }

    @ExcelField(title = "退款状态", align = 2, sort = 17)
    public String getRefundInfo() {
//        TOrderRefund tOrderRefund = this.getRefund();
        if (null == this.getOrderRefundStatus()) {
            if (null == getVendout() || Integer.valueOf(TOrderDetailVendout.OUT_STATUS_UNKNOWN).equals(getVendout().getVendoutStatus())) {
                Date date;
                if (getOrder().getOrderType().equals(TOrderMaster.IMMEDIATE) || getOrder().getOrderType().equals(TOrderMaster.CARD)) {
                    date = Optional.ofNullable(getOrder().getCost()).map(TOrderCost::getPayTime).orElse(null);
                } else {
                    date = DateUtils.getDate1(getValidTime());
                }
                long costTime = date != null ? date.getTime() : 0;
                long currTime = System.currentTimeMillis();
                if (currTime - costTime > REFUND_MAX_MILLIS) {
                    return "未退款";
                }
            }
            return "";
        }
        String info = null;
        switch (this.getOrderRefundStatus().getCode()) {
            case TOrderRefund.REFUND_STATUS_NOTICE_SUCCESS:
                info = "退款中";
                break;
            case TOrderRefund.REFUND_STATUS_NOTICE_FAIL:
                info = "退款失败";
                break;
            case TOrderRefund.REFUND_STATUS_SUCCESS:
                info = "已退款";
                break;
            case TOrderRefund.REFUND_STATUS_PROCESSING:
                info = "退款中";
                break;
            case TOrderRefund.REFUND_STATUS_FAIL:
                info = "退款失败";
                break;
            case TOrderRefund.REFUND_STATUS_CHANGE:
                info = "退款失败";
                break;
            case TOrderRefund.REFUND_STATUS_CREATE:
                info = "退款中";
                break;
        }
        return info;
    }

    @ExcelField(title = "扣款时间", align = 2, sort = 10)
    public Date getPayTime() {
        return Optional.ofNullable(getOrder().getCost()).map(TOrderCost::getPayTime).orElse(null);
    }

    public String getOrderStatusCn() {
        try {
            return Optional.ofNullable(getStatus()).map(orderStatus -> {
                switch (orderStatus) {
                    case STATUS_PENDING:
                        if (null == super.getOrderRefundStatus()) return "待配送";
                        return "已取消";
                    case STATUS_DISTRIBUTION:
                        if (null != getValidTime() && (getValidTime().equals(DateUtils.getTime1()) || DateUtils.getDate1(getValidTime()).after(DateUtils.getDate1(DateUtils.getTime1()))))
                            return "配送中";
                        return "未送达";
                    case STATUS_DELIVERED:
                        return "已送达";
                }
                return "已完成";
            }).orElse("已完成");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "";
    }

    public boolean inRefundTime() {
        Date date;
        if (getOrder().getOrderType().equals(TOrderMaster.IMMEDIATE) || getOrder().getOrderType().equals(TOrderMaster.CARD)) {
            date = Optional.ofNullable(getOrder().getCost()).map(TOrderCost::getPayTime).orElse(null);
        } else {
            date = Optional.ofNullable(getValidTime()).map(DateUtils::getDate1).orElse(null);
        }
        if (null == date) return false;
        long time = date.getTime();
        long currTime = System.currentTimeMillis();
        return currTime - time > REFUND_MIN_MILLIS && currTime - time < REFUND_MAX_MILLIS;
    }

    public String getColor() {
        if (Optional.ofNullable(getOrder()).map(TOrderMaster::getAttachment).map(TOrderMasterAtt::getOrderSources).isPresent())
            switch (getOrder().getAttachment().getOrderSources()) {
                case TOrderMasterAtt.DEFAULT_SOURCES:
                    return "";
                case TOrderMasterAtt.INDEPENDENT_SOURCES:
                    return "cf_purple";
                case TOrderMasterAtt.API_SOURCES:
                    return "cf_green";
            }
        return "";
    }

    public String getNodeName() {
        return Optional.of(getOrder()).map(TOrderMaster::getMtVm).map(TMtVm::getNodeName).orElse("");
    }

    public String getInnerCode() {
        return Optional.of(getOrder()).map(TOrderMaster::getMtVm).map(TMtVm::getInnerCode).orElse("");
    }

    public String getOrderSeq() {
        return Optional.of(getOrder()).map(TOrderMaster::getOrderSeq).orElse("");
    }

    public String getChannelCode() {
        return Optional.ofNullable(getVendout()).map(TOrderDetailVendout::getVendoutChannel).map(TMtVmChannel::getChannelCode).orElse("");
    }

    public String getSkuName() {
        return Optional.of(getSku()).map(TMtSku::getSkuName).orElse("");
    }

    public String getSkuUnit() {
        return Optional.of(getSku()).map(TMtSku::getSkuUnit).orElse("");
    }

    public String getPayDay() {
        return Optional.ofNullable(getOrder()).map(TOrderMaster::getCost).map(TOrderCost::getPayTime).map(DateUtils::getTime2).orElse(null);
    }

    public String getVendoutDay() {
        return Optional.ofNullable(getVendout()).map(TOrderDetailVendout::getVendoutTime).map(DateUtils::getTime2).orElse(null);
    }
}
