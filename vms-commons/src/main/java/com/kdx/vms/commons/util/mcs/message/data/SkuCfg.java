package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Created by liyang on 2016/11/7.
 */
public class SkuCfg implements Serializable{

    private static final long serialVersionUID = -3929981499666143031L;
    private Integer skuId;
    private Integer offerPrice;
    private Integer index;
    private String  skuImageUrl;
    private String  skuName;
    private String  className;
    private Integer classId;
    private String  unit;
    private String  desc;
    private String brand;

    private Integer saleType;
    private Integer generalizeType;

    private List<SkuImageUrl> skuImageUrls;
    public SkuCfg() {
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(Integer offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getSkuImageUrl() {
        return skuImageUrl;
    }

    public void setSkuImageUrl(String skuImageUrl) {
        this.skuImageUrl = skuImageUrl;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Integer getGeneralizeType() {
        return generalizeType;
    }

    public void setGeneralizeType(Integer generalizeType) {
        this.generalizeType = generalizeType;
    }

    public List<SkuImageUrl> getSkuImageUrls() {
        return skuImageUrls;
    }

    public void setSkuImageUrls(List<SkuImageUrl> skuImageUrls) {
        this.skuImageUrls = skuImageUrls;
    }
}
