package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtTheme;
import org.apache.cayenne.Cayenne;

public class TMtTheme extends _TMtTheme {

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

}
