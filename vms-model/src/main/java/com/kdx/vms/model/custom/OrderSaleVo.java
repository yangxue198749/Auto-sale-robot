package com.kdx.vms.model.custom;

import com.kdx.vms.commons.cache.CardCache;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.excel.ExcelField;
import com.kdx.vms.model.bean.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by yangshuxi on 2017/12/4.
 */
public class OrderSaleVo {
    private String nodeName;
    private String innerCode;
    private String detailId;
    private String orderSeq;
    private String outerSeq;
    private String orderType;
    private String skuId;
    private String skuName;
    private String payType;
    private String skuAmount;
    private String payAmount;
    private String cardAmount;
    private String orderTime;
    private String validTime;
    private String orderStatus;
    private String outStatus;
    private String refundButton;
    private String refundStatus;
    private String color;


    public static OrderSaleVo of(TOrderDetail orderDetail) {
        if (null == orderDetail) return null;
        OrderSaleVo orderSaleVo = new OrderSaleVo();
        orderSaleVo.setNodeName(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNodeName).orElse(""))
                .setInnerCode(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getInnerCode).orElse(""))
                .setDetailId(Optional.of(orderDetail).map(TOrderDetail::getId).map(Object::toString).orElse(""))
                .setOrderSeq(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getOrderSeq).orElse(""))
                .setOuterSeq(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getCost).map(TOrderCost::getOuterSeq).orElse(""))
                .setOrderType(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getOrderTypeCn).orElse(""))
                .setSkuId(Optional.of(orderDetail).map(TOrderDetail::getSku).map(TMtSku::getId).map(Object::toString).orElse(""))
                .setSkuName(Optional.of(orderDetail).map(TOrderDetail::getSku).map(TMtSku::getFullSkuName).orElse(""))
                .setPayType(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getCost).map(TOrderCost::getPayType).map(CardCache::get).orElse("未知"))
                .setSkuAmount(Optional.of(orderDetail).map(TOrderDetail::getRealPrice).map(StringUtil::formatAmount).orElse("0.00"))
                .setOrderTime(Optional.of(orderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getCTime).map(DateUtils::getTime2).orElse(""))
                .setValidTime(Optional.of(orderDetail).map(TOrderDetail::getValidTime).orElse(""))
                .setOrderStatus(Optional.of(orderDetail).map(TOrderDetail::getOrderStatusCn).orElse(""))
                .setOutStatus(Optional.of(orderDetail).map(TOrderDetail::getOutStatusName).orElse("未知"))
                .setRefundButton(Optional.of(orderDetail).map(TOrderDetail::getRefundButton).map(s->s.replaceAll("/vendout/","/order/")).orElse(""))
                .setRefundStatus(Optional.of(orderDetail).map(TOrderDetail::getRefundInfo).orElse(""))
                .setColor(orderDetail.getColor())
        ;
        Integer cardAmount = Optional.of(orderDetail)
                .map(TOrderDetail::getDetailCost)
                .orElse(new ArrayList<>())
                .stream()
                .filter(cost -> Objects.equals(2000, cost.getCostType()))
                .mapToInt(TOrderDetailCost::getCostPrice)
                .sum();
        Integer payAmount = Optional.of(orderDetail)
                .map(TOrderDetail::getDetailCost)
                .orElse(new ArrayList<>())
                .stream()
                .filter(cost -> !Objects.equals(2000, cost.getCostType()))
                .mapToInt(TOrderDetailCost::getCostPrice)
                .sum();
        orderSaleVo.setCardAmount(StringUtil.formatAmount(cardAmount));
        orderSaleVo.setPayAmount(StringUtil.formatAmount(payAmount));
        return orderSaleVo;
    }

    public OrderSaleVo setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public OrderSaleVo setInnerCode(String innerCode) {
        this.innerCode = innerCode;
        return this;
    }

    public OrderSaleVo setDetailId(String detailId) {
        this.detailId = detailId;
        return this;
    }

    public OrderSaleVo setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public OrderSaleVo setOuterSeq(String outerSeq) {
        this.outerSeq = outerSeq;
        return this;
    }

    public OrderSaleVo setOrderType(String orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderSaleVo setSkuId(String skuId) {
        this.skuId = skuId;
        return this;
    }

    public OrderSaleVo setSkuName(String skuName) {
        this.skuName = skuName;
        return this;
    }

    public OrderSaleVo setPayType(String payType) {
        this.payType = payType;
        return this;
    }

    public OrderSaleVo setSkuAmount(String skuAmount) {
        this.skuAmount = skuAmount;
        return this;
    }

    public OrderSaleVo setPayAmount(String payAmount) {
        this.payAmount = payAmount;
        return this;
    }

    public OrderSaleVo setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
        return this;
    }

    public OrderSaleVo setOrderTime(String orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public OrderSaleVo setValidTime(String validTime) {
        this.validTime = validTime;
        return this;
    }

    public OrderSaleVo setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderSaleVo setOutStatus(String outStatus) {
        this.outStatus = outStatus;
        return this;
    }

    public OrderSaleVo setRefundButton(String refundButton) {
        this.refundButton = refundButton;
        return this;
    }

    public OrderSaleVo setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
        return this;
    }

    public OrderSaleVo setColor(String color) {
        this.color = color;
        return this;
    }

    @ExcelField(title = "点位名称", align = 2, sort = 1)
    public String getNodeName() {
        return nodeName;
    }
    @ExcelField(title = "售货机编号", align = 2, sort = 2)
    public String getInnerCode() {
        return innerCode;
    }
    @ExcelField(title = "订单编号", align = 2, sort = 3)
    public String getDetailId() {
        return detailId;
    }
    @ExcelField(title = "流水号", align = 2, sort = 4)
    public String getOrderSeq() {
        return orderSeq;
    }
    @ExcelField(title = "外部订单号", align = 2, sort = 5)
    public String getOuterSeq() {
        return outerSeq;
    }
    @ExcelField(title = "业务类型", align = 2, sort = 6)
    public String getOrderType() {
        return orderType;
    }
    @ExcelField(title = "商品ID", align = 2, sort = 7)
    public String getSkuId() {
        return skuId;
    }
    @ExcelField(title = "商品名称", align = 2, sort = 8)
    public String getSkuName() {
        return skuName;
    }
    @ExcelField(title = "支付类型", align = 2, sort = 9)
    public String getPayType() {
        return payType;
    }
    @ExcelField(title = "商品价格", align = 2, sort = 10)
    public String getSkuAmount() {
        return skuAmount;
    }
    @ExcelField(title = "商品金额", align = 2, sort = 11)
    public String getPayAmount() {
        return payAmount;
    }
    @ExcelField(title = "卡包支付金额", align = 2, sort = 12)
    public String getCardAmount() {
        return cardAmount;
    }
    @ExcelField(title = "下单时间", align = 2, sort = 13)
    public String getOrderTime() {
        return orderTime;
    }
    @ExcelField(title = "取货有效期", align = 2, sort = 14)
    public String getValidTime() {
        return validTime;
    }
    @ExcelField(title = "订单状态", align = 2, sort = 15)
    public String getOrderStatus() {
        return orderStatus;
    }
    @ExcelField(title = "出货状态", align = 2, sort = 16)
    public String getOutStatus() {
        return outStatus;
    }

    public String getRefundButton() {
        return refundButton;
    }
    @ExcelField(title = "退款状态", align = 2, sort = 17)
    public String getRefundStatus() {
        return refundStatus;
    }

    public String getColor() {
        return color;
    }
}

