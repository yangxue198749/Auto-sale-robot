package com.kdx.vms.webapi.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 * Created by yangshuxi on 2016/12/30.
 */
public class SkuImportObject {
    private String skuCode;
    private String skuTypeId;
    private String skuName;
    private String skuColor;
    private Integer offerPrice;
    private Integer skuLength;
    private Integer skuWidth;
    private Integer skuHeight;
    private String skuShelfLife;
    private String skuMadeArea;
    private String skuUnit;
    private String skuProducer;
    private String storageMethod;
    private String packingSpecification;
    private String dbCode;
    private String saleArea;
    private String inventoryType;

    public void createMtSku(ObjectContext context, TMtSku mtSku, TMtSkuType skuType){
        if(null==mtSku){
            mtSku=context.newObject(TMtSku.class);
            mtSku.setCtor(UserUtils.getErpSystem().getId());
            mtSku.setCtime(new Date());
        }
        else{
            mtSku.setUtor(UserUtils.getErpSystem().getId());
            mtSku.setUtime(new Date());
        }
        mtSku.setDbCode(dbCode);
        mtSku.setInventoryType(inventoryType);
        mtSku.setOfferPrice(offerPrice);
        mtSku.setPackingSpecification(packingSpecification);
        mtSku.setSaleArea(saleArea);
        mtSku.setSkuCode(skuCode);
        mtSku.setSkuColor(skuColor);
        mtSku.setSkuHeight(skuHeight);
        mtSku.setSkuLength(skuLength);
        mtSku.setSkuMadeArea(skuMadeArea);
        mtSku.setSkuName(skuName);
        mtSku.setSkuProducer(skuProducer);
        mtSku.setSkuShelfLife(StringUtil.getIntValue(skuShelfLife,0));
        mtSku.setSkuUnit(skuUnit);
        mtSku.setSkuWidth(skuWidth);
        mtSku.setStorageMethod(storageMethod);
        mtSku.setSkuType(skuType);
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuTypeId() {
        return skuTypeId;
    }

    public void setSkuTypeId(String skuTypeId) {
        this.skuTypeId = skuTypeId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuColor() {
        return skuColor;
    }

    public void setSkuColor(String skuColor) {
        this.skuColor = skuColor;
    }

    public Integer getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(Integer offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Integer getSkuLength() {
        return skuLength;
    }

    public void setSkuLength(Integer skuLength) {
        this.skuLength = skuLength;
    }

    public Integer getSkuWidth() {
        return skuWidth;
    }

    public void setSkuWidth(Integer skuWidth) {
        this.skuWidth = skuWidth;
    }

    public Integer getSkuHeight() {
        return skuHeight;
    }

    public void setSkuHeight(Integer skuHeight) {
        this.skuHeight = skuHeight;
    }

    public String getSkuShelfLife() {
        return skuShelfLife;
    }

    public void setSkuShelfLife(String skuShelfLife) {
        this.skuShelfLife = skuShelfLife;
    }

    public String getSkuMadeArea() {
        return skuMadeArea;
    }

    public void setSkuMadeArea(String skuMadeArea) {
        this.skuMadeArea = skuMadeArea;
    }

    public String getSkuUnit() {
        return skuUnit;
    }

    public void setSkuUnit(String skuUnit) {
        this.skuUnit = skuUnit;
    }

    public String getSkuProducer() {
        return skuProducer;
    }

    public void setSkuProducer(String skuProducer) {
        this.skuProducer = skuProducer;
    }

    public String getStorageMethod() {
        return storageMethod;
    }

    public void setStorageMethod(String storageMethod) {
        this.storageMethod = storageMethod;
    }

    public String getPackingSpecification() {
        return packingSpecification;
    }

    public void setPackingSpecification(String packingSpecification) {
        this.packingSpecification = packingSpecification;
    }

    public String getDbCode() {
        return dbCode;
    }

    public void setDbCode(String dbCode) {
        this.dbCode = dbCode;
    }

    public String getSaleArea() {
        return saleArea;
    }

    public void setSaleArea(String saleArea) {
        this.saleArea = saleArea;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }
}
