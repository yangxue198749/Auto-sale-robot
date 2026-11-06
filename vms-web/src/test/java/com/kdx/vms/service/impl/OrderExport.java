package com.kdx.vms.service.impl;

import com.kdx.vms.commons.cache.CardCache;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.excel.ExcelField;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.enumeration.PayType;
import com.kdx.vms.orm.cache.AreaCache;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by yangshuxi on 2017/10/13.
 */
public class OrderExport {
    private String province;
    private String city;
    private String customerName;
    private String nodeAddress;
    private String vmStatus;
    private String mode;
    private String seq;
    private String innerCode;
    private String channelCode;
    private String skuCode;
    private String skuName;
    private String payType;
    private String payAmount;
    private String costTime;
    private String costStatus = "成功";
    private String outTime;
    private String outStatus;
    private String failReason;
    private String refundStatus;

    public OrderExport setProvince(String province) {
        this.province = province;
        return this;
    }

    public OrderExport setCity(String city) {
        this.city = city;
        return this;
    }

    public OrderExport setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public OrderExport setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
        return this;
    }

    public OrderExport setVmStatus(String vmStatus) {
        this.vmStatus = vmStatus;
        return this;
    }

    public OrderExport setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public OrderExport setSeq(String seq) {
        this.seq = seq;
        return this;
    }

    public OrderExport setInnerCode(String innerCode) {
        this.innerCode = innerCode;
        return this;
    }

    public OrderExport setChannelCode(String channelCode) {
        this.channelCode = channelCode;
        return this;
    }

    public OrderExport setSkuCode(String skuCode) {
        this.skuCode = skuCode;
        return this;
    }

    public OrderExport setSkuName(String skuName) {
        this.skuName = skuName;
        return this;
    }

    public OrderExport setPayType(String payType) {
        this.payType = payType;
        return this;
    }

    public OrderExport setPayAmount(String payAmount) {
        this.payAmount = payAmount;
        return this;
    }

    public OrderExport setCostTime(String costTime) {
        this.costTime = costTime;
        return this;
    }

    public OrderExport setCostStatus(String costStatus) {
        this.costStatus = costStatus;
        return this;
    }

    public OrderExport setOutTime(String outTime) {
        this.outTime = outTime;
        return this;
    }

    public OrderExport setOutStatus(String outStatus) {
        this.outStatus = outStatus;
        return this;
    }

    public OrderExport setFailReason(String failReason) {
        this.failReason = failReason;
        return this;
    }

    public OrderExport setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
        return this;
    }


    @ExcelField(title = "所属区域", align = 2, sort = 1)
    public String getProvince() {
        return province;
    }

    @ExcelField(title = "所属城市", align = 2, sort = 2)
    public String getCity() {
        return city;
    }

    @ExcelField(title = "客户名称", align = 2, sort = 3)
    public String getCustomerName() {
        return customerName;
    }

    @ExcelField(title = "点位地址", align = 2, sort = 4)
    public String getNodeAddress() {
        return nodeAddress;
    }

    @ExcelField(title = "售货机状态", align = 2, sort = 5)
    public String getVmStatus() {
        return vmStatus;
    }

    @ExcelField(title = "运营模式", align = 2, sort = 6)
    public String getMode() {
        return mode;
    }

    @ExcelField(title = "流水号", align = 2, sort = 7)
    public String getSeq() {
        return seq;
    }

    @ExcelField(title = "售货机编号", align = 2, sort = 8)
    public String getInnerCode() {
        return innerCode;
    }

    @ExcelField(title = "货道编号", align = 2, sort = 9)
    public String getChannelCode() {
        return channelCode;
    }

    @ExcelField(title = "商品编码", align = 2, sort = 10)
    public String getSkuCode() {
        return skuCode;
    }

    @ExcelField(title = "商品名称", align = 2, sort = 11)
    public String getSkuName() {
        return skuName;
    }

    @ExcelField(title = "支付方式", align = 2, sort = 12)
    public String getPayType() {
        return payType;
    }

    @ExcelField(title = "支付金额（元）", align = 2, sort = 13)
    public String getPayAmount() {
        return payAmount;
    }

    @ExcelField(title = "扣款时间", align = 2, sort = 14)
    public String getCostTime() {
        return costTime;
    }

    @ExcelField(title = "扣款状态", align = 2, sort = 15)
    public String getCostStatus() {
        return costStatus;
    }

    @ExcelField(title = "出货时间", align = 2, sort = 16)
    public String getOutTime() {
        return outTime;
    }

    @ExcelField(title = "出货状态", align = 2, sort = 17)
    public String getOutStatus() {
        return outStatus;
    }

    @ExcelField(title = "失败原因", align = 2, sort = 18)
    public String getFailReason() {
        return failReason;
    }

    @ExcelField(title = "退款状态", align = 2, sort = 19)
    public String getRefundStatus() {
        return refundStatus;
    }

    public static OrderExport of(TOrderDetail orderDetail){
        TOrderMaster orderMaster = orderDetail.getOrder();
        TMtArea area = AreaCache.getArea(orderMaster.getMtVm().getNode().getAreaId());
        TMtArea city = area.getParentArea();
        TMtArea province = city.getParentArea();
        OrderExport orderExport = new OrderExport();
        orderExport.setProvince(province.getAreaName())
                .setCity(city.getAreaName())
                .setCustomerName(orderMaster.getMtVm().getCompany().getCustomer().getCustomerName())
                .setNodeAddress(orderMaster.getMtVm().getNode().getNodeAddress())
                .setVmStatus(status(orderMaster.getMtVm().getVmStatus()))
                .setMode(mode(orderMaster.getMtVm().getCompany().getId()))
                .setSeq(orderDetail.getId()+"")
                .setInnerCode(orderMaster.getMtVm().getInnerCode())
                .setChannelCode(Optional.of(orderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutChannel).map(TMtVmChannel::getChannelCode).orElse(""))
                .setSkuCode(orderDetail.getSku().getSkuCode())
                .setSkuName(orderDetail.getSku().getSkuName()+orderDetail.getSku().getSkuUnit())
                .setPayType(CardCache.get(orderMaster.getCost().getPayType()))
                .setPayAmount(orderDetail.getCostMoney()+"")
                .setCostTime(Optional.of(orderMaster).map(TOrderMaster::getCost).map(TOrderCost::getPayTime).map(DateUtils::getTime2).orElse(""))
                .setOutTime(Optional.of(orderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutTime).map(DateUtils::getTime2).orElse(""))
                .setOutStatus(orderDetail.getOutStatusName())
                .setFailReason(orderDetail.getOutStatusFailReason())
                .setRefundStatus(orderDetail.getRefundInfo());
        return orderExport;
    }



    public static String status(Integer status) {
        switch (status) {
            case TMtVm.STATUS_UNDEPLOY:
                return "未投放";
            case TMtVm.STATUS_DEPLOY:
                return "在投放";
            case TMtVm.STATUS_SALE:
                return "在售";
            case TMtVm.STATUS_UNSLAE:
                return "停售";
            case TMtVm.STATUS_WITHDRAW:
                return "撤回中";
        }
        return "";
    }

    public static String mode(Integer companyId){
        if(companyId.equals(3)||companyId.equals(4820))return "直营";
        return "租赁";
    }

    public static String formatAmount(Integer price){
        if(null!=price){
            return new BigDecimal(price).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_EVEN).toString();
        }
        return "";
    }
}
