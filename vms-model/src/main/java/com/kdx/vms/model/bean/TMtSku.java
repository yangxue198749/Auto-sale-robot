package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.auto._TMtSku;
import org.apache.cayenne.Cayenne;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class TMtSku extends _TMtSku {

    public static int VALIDITY_NO = 0;
    public static int VALIDITY_YES = 1;
    /**
     * 保质期：天
     */
    public static int VALIDITY_DAY = 0;
    /**
     * 保质期：月
     */
    public static int VALIDITY_MONTH = 1;

    private Integer id;
    /**
     * 可预订
     */
    public static final Integer RESERVE_TRUE = 1;

    public Integer getId() {
        if (id != null) return id;
        return Cayenne.intPKForObject(this);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTMtSku(TMtSku tMtSku) {
        setInventoryType(tMtSku.getInventoryType());
        setOfferPrice(tMtSku.getOfferPrice());
        setPackingSpecification(tMtSku.getPackingSpecification());

        setDbCode(tMtSku.getDbCode());
        setSkuColor(tMtSku.getSkuColor());
        setSkuHeight(tMtSku.getSkuHeight());
        setSkuLength(tMtSku.getSkuLength());
        setSkuMadeArea(tMtSku.getSkuMadeArea());
        setSkuName(tMtSku.getSkuName());
        setSkuProducer(tMtSku.getSkuProducer());
        setSkuWidth(tMtSku.getSkuWidth());
        setStorageMethod(tMtSku.getStorageMethod());
        setSkuUnit(tMtSku.getSkuUnit());
        setSaleArea(tMtSku.getSaleArea());
        setSkuShelfLife(tMtSku.getSkuShelfLife());
        if (StringUtils.isNotEmpty(tMtSku.getSkuCode())) {
            setSkuCode(tMtSku.getSkuCode());
        }
        setDeposit(tMtSku.getDeposit());
    }

    @Override
    public String getImgUrl() {
        if (CheckUtils.check(this.getSkuImg())) {
            String imgUrl = this.getSkuImg().stream()
                    .filter(tMtSkuImg -> tMtSkuImg.getSource() == TMtSkuImg.IMG_SOURCE_VMS && tMtSkuImg.getImgIndex() == TMtSkuImg.IMG_MASTER_URL)
                    .findFirst()
                    .map(TMtSkuImg::getImgUrl)
                    .orElse(null);
            if (CheckUtils.check(imgUrl)) {
                return PropertiesLoader.map.get("imgUrlPrefix") + imgUrl;
            }
        }
        return PropertiesLoader.map.get("imgUrlPrefix") + "default.png";
    }

    public String getImgUrlForVmBox() {
        if (CheckUtils.check(this.getSkuImg())) {
            String imgUrl = this.getSkuImg().stream()
                    .filter(tMtSkuImg -> tMtSkuImg.getSource() == TMtSkuImg.IMG_SOURCE_VMS && tMtSkuImg.getImgIndex() == TMtSkuImg.IMG_MASTER_URL)
                    .findFirst()
                    .map(TMtSkuImg::getImgUrl)
                    .orElse(null);
            if (CheckUtils.check(imgUrl)) {
                return PropertiesLoader.map.get("imgUrlPrefix") + imgUrl;
            }
        }
        return PropertiesLoader.map.get("imgUrlPrefix") + "null";
    }

    public String getImgName() {
        return super.getImgUrl();
    }

    public String getFullSkuName() {
        return Optional.of(this).map(TMtSku::getSkuName).orElse("") + Optional.of(this).map(TMtSku::getSkuUnit).orElse("");
    }
}
