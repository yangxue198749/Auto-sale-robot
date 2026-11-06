package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by zhouxing on 18/2/8.
 */
public class Extentions implements Serializable {

    private static final long serialVersionUID = 2896862688008890949L;

    private Integer          extentionCode;
    private Integer          tempBoxCount;
    private List<MtEnergy>   mtEnergys = new ArrayList<>();


    public Integer getExtentionCode() {
        return extentionCode;
    }

    public void setExtentionCode(Integer extentionCode) {
        this.extentionCode = extentionCode;
    }

    public Integer getTempBoxCount() {
        return tempBoxCount;
    }

    public void setTempBoxCount(Integer tempBoxCount) {
        this.tempBoxCount = tempBoxCount;
    }

    public List<MtEnergy> getMtEnergys() {
        return mtEnergys;
    }

    public void setMtEnergys(List<MtEnergy> mtEnergys) {
        this.mtEnergys = mtEnergys;
    }

    public void addMtEnergys(MtEnergy mtEnergy) {
        this.mtEnergys.add(mtEnergy);
    }
}