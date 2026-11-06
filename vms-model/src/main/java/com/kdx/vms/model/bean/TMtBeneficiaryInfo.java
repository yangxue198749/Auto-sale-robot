package com.kdx.vms.model.bean;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.auto._TMtBeneficiaryInfo;
import com.kdx.vms.model.custom.beneficiary.AliPayInfo;
import com.kdx.vms.model.custom.beneficiary.PayInfo;
import com.kdx.vms.model.custom.beneficiary.WxPayInfo;
import com.kdx.vms.model.enumeration.PayType;
import org.apache.cayenne.Cayenne;

public class TMtBeneficiaryInfo extends _TMtBeneficiaryInfo {
    public PayInfo getPayInfo() {
        if (getBeneficiaryType().equals(PayType.WECHAT_PAY.getCode())) {
            return JSON.parseObject(getBeneficiaryInfo(), WxPayInfo.class);
        }
        if (getBeneficiaryType().equals(PayType.ALI_PAY.getCode())) {
            return JSON.parseObject(getBeneficiaryInfo(), AliPayInfo.class);
        }
        return null;
    }

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    public boolean canDelete() {
        return getEnableMark() == 0;
    }
}
