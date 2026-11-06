package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TOpenService;
import org.apache.cayenne.Cayenne;

public class TOpenService extends _TOpenService {

    public int getId() {
        return Cayenne.intPKForObject(this);
    }
}
