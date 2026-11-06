package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtSkuCompany;
import org.apache.cayenne.Cayenne;

public class TMtSkuCompany extends _TMtSkuCompany {

    private Integer id;

    public Integer getId(){
        if(id != null) return id;
        return Cayenne.intPKForObject(this);
    }
}
