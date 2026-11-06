package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtUpgradePlan;
import org.apache.cayenne.Cayenne;

public class TMtUpgradePlan extends _TMtUpgradePlan {

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

}
