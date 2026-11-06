package com.kdx.vms.model.custom;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.excel.ExcelField;
import com.kdx.vms.model.bean.*;
import org.apache.cayenne.Cayenne;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class OrderVendoutVo {
    private String nodeName;
    private String innerCode;
    private String orderSeq;
    private String channelCode;
    private String skuName;
    private String skuUnit;
    private Integer skuId;
    private Integer id;
    private String costTypeName;
    private String skuMoney;
    private Integer costMoney;
    private Integer cardCostMoney;
    private Date payTime;
    private Date vendoutTime;
    private String outStatusName;
    private String outStatusFailReason;
    private String refundInfo;
    private String refundButton;
    private String memo;
    private String color;


    public static OrderVendoutVo of(TOrderDetail tOrderDetail){
        if (null == tOrderDetail) return null;

        /*BigDecimal b1 = new BigDecimal(tOrderDetail.getCardCostMoney());
        BigDecimal b2 = tOrderDetail.getDeduction(Integer.valueOf(tOrderDetail.getCardCostMoney()));
        BigDecimal b3 = new BigDecimal(tOrderDetail.getCostMoney());
        String cardMoney = b1.subtract(b2) +"";
        String reallyMoney = b3.add(b2) +"";*/

        OrderVendoutVo orderVendoutVo = new OrderVendoutVo();

        orderVendoutVo.setNodeName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNodeName).orElse(""));
        orderVendoutVo.setInnerCode(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getInnerCode).orElse(""));
        orderVendoutVo.setOrderSeq(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getOrderSeq).orElse(""));
        orderVendoutVo.setChannelCode(Optional.of(tOrderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutChannel).map(TMtVmChannel::getChannelCode).orElse(""));
        orderVendoutVo.setSkuName(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuName).orElse(""));
        orderVendoutVo.setSkuId(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getId).orElse(Integer.valueOf("")));
        orderVendoutVo.setSkuUnit(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuUnit).orElse(""));
        orderVendoutVo.setId(Optional.of(tOrderDetail).map(TOrderDetail::getId).orElse(Integer.valueOf("")));
        orderVendoutVo.setCostTypeName(Optional.of(tOrderDetail).map(TOrderDetail::getCostTypeName).orElse(""));
        orderVendoutVo.setSkuMoney(Optional.of(tOrderDetail).map(TOrderDetail::getSkuMoney).orElse(""));
        orderVendoutVo.setCostMoney(Optional.of(tOrderDetail).map(TOrderDetail::getCostMoney).orElse(Integer.valueOf("")));
        orderVendoutVo.setCardCostMoney(Optional.of(tOrderDetail).map(TOrderDetail::getCardCostMoney).orElse(Integer.valueOf("")));
        orderVendoutVo.setPayTime(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getCost).map(TOrderCost::getPayTime).orElse(null));
        orderVendoutVo.setVendoutTime(Optional.of(tOrderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutTime).orElse(null));
        orderVendoutVo.setOutStatusName(Optional.of(tOrderDetail).map(TOrderDetail::getOutStatusName).orElse(""));
        orderVendoutVo.setOutStatusFailReason(Optional.of(tOrderDetail).map(TOrderDetail::getOutStatusFailReason).orElse(""));
        orderVendoutVo.setRefundInfo(Optional.of(tOrderDetail).map(TOrderDetail::getRefundInfo).orElse(""));
        orderVendoutVo.setRefundButton(Optional.of(tOrderDetail).map(TOrderDetail::getRefundButton).orElse(""));
        orderVendoutVo.setMemo(Optional.of(tOrderDetail).map(TOrderDetail::getMemo).orElse(""));
        orderVendoutVo.setColor(Optional.of(tOrderDetail).map(TOrderDetail::getColor).orElse(""));

        /*orderVendoutVo.setProvinceName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNode).map(TMtNode::getProvinceId).map(id -> Cayenne.objectForPK(tOrderDetail.getObjectContext(), TMtArea.class, id)).map(TMtArea::getAreaName).orElse(""))
                        .setCityName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNode).map(TMtNode::getCityId).map(id -> Cayenne.objectForPK(tOrderDetail.getObjectContext(), TMtArea.class, id)).map(TMtArea::getAreaName).orElse(""))
                        .setAreaName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNode).map(TMtNode::getAreaId).map(id -> Cayenne.objectForPK(tOrderDetail.getObjectContext(), TMtArea.class, id)).map(TMtArea::getAreaName).orElse(""))
                        .setCompanyName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getCompany).map(TMtCompany::getCompanyName).orElse(""))
                        .setNodeAddress(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNode).map(TMtNode::getNodeAddress).orElse(""))
                        .setBusinessDistrictName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getNode).map(TMtNode::getBusinessDistrictId).map(id ->  Cayenne.objectForPK(tOrderDetail.getObjectContext(),TMtBusinessDistrict.class, id)).map(TMtBusinessDistrict::getBusinessDistrictName).orElse(""))
                        .setMtVmStatusName(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getMtVmStatusName).orElse(""))
                        .setOrderSeq(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getOrderSeq).orElse(""))
                        .setOuterSeq(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getCost).map(TOrderCost::getOuterSeq).orElse(""))
                        .setInnerCode(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getInnerCode).orElse(""))
                        .setChannelCode(Optional.of(tOrderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutChannel).map(TMtVmChannel::getChannelCode).orElse(""))
                        .setCurrNum(Optional.of(tOrderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutChannel).map(TMtVmChannel::getCurrNum).map(String::valueOf).orElse(""))
                        .setPayTypeName(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuType).map(TMtSkuType::getParent).map(TMtSkuType::getTypeName).orElse(""))
                        .setTypeName(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuType).map(TMtSkuType::getTypeName).orElse(""))
                        .setSkuCode(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuCode).orElse(""))
                        .setSkuName(Optional.of(tOrderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuName).orElse(""))
                        .setCostTypeName("虚拟卡".equals(tOrderDetail.getCostTypeName()) ? "微信支付" : tOrderDetail.getCostTypeName())
                        .setSkuMoney(Optional.of(tOrderDetail).map(TOrderDetail::getSkuMoney).orElse(""))
                        .setCostMoney(StringUtil.changeF2Y(tOrderDetail.getCostMoney() +""))
                        .setCardCostMoney(StringUtil.changeF2Y(tOrderDetail.getCardCostMoney() +""))
                        .setCardRebate(StringUtil.changeF2Y(cardMoney))
                        .setCardMoney(StringUtil.changeF2Y(b2 +""))
                        .setReallyMoney(StringUtil.changeF2Y(reallyMoney))
                        .setPayTime(DateUtils.getTime2(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getCost).map(TOrderCost::getPayTime).orElse(null)))
                        .setVendoutTime(DateUtils.getTime2(Optional.of(tOrderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutTime).orElse(null)))
                        .setOutStatusName(Optional.of(tOrderDetail).map(TOrderDetail::getOutStatusName).orElse(""))
                        .setOutStatusFailReason(Optional.of(tOrderDetail).map(TOrderDetail::getOutStatusFailReason).orElse(""))
                        .setRefundInfo(Optional.of(tOrderDetail).map(TOrderDetail::getRefundInfo).orElse(""))
                        .setMemo(Optional.of(tOrderDetail).map(TOrderDetail::getMemo).orElse(""))
                        .setHardwareCode(Optional.of(tOrderDetail).map(TOrderDetail::getOrder).map(TOrderMaster::getMtVm).map(TMtVm::getHardwareCode).orElse(""))
                        .setColor(tOrderDetail.getColor())*/
        ;
        return orderVendoutVo;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCostTypeName() {
        return costTypeName;
    }

    public void setCostTypeName(String costTypeName) {
        this.costTypeName = costTypeName;
    }

    public String getSkuMoney() {
        return skuMoney;
    }

    public void setSkuMoney(String skuMoney) {
        this.skuMoney = skuMoney;
    }

    public Integer getCostMoney() {
        return costMoney;
    }

    public void setCostMoney(Integer costMoney) {
        this.costMoney = costMoney;
    }

    public Integer getCardCostMoney() {
        return cardCostMoney;
    }

    public void setCardCostMoney(Integer cardCostMoney) {
        this.cardCostMoney = cardCostMoney;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getVendoutTime() {
        return vendoutTime;
    }

    public void setVendoutTime(Date vendoutTime) {
        this.vendoutTime = vendoutTime;
    }

    public String getOutStatusName() {
        return outStatusName;
    }

    public void setOutStatusName(String outStatusName) {
        this.outStatusName = outStatusName;
    }

    public String getOutStatusFailReason() {
        return outStatusFailReason;
    }

    public void setOutStatusFailReason(String outStatusFailReason) {
        this.outStatusFailReason = outStatusFailReason;
    }

    public String getRefundInfo() {
        return refundInfo;
    }

    public void setRefundInfo(String refundInfo) {
        this.refundInfo = refundInfo;
    }

    public String getRefundButton() {
        return refundButton;
    }

    public void setRefundButton(String refundButton) {
        this.refundButton = refundButton;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSkuUnit() {
        return skuUnit;
    }

    public void setSkuUnit(String skuUnit) {
        this.skuUnit = skuUnit;
    }
}