package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.auto._TMtVmResource;
import org.apache.cayenne.Cayenne;

public class TMtVmResource extends _TMtVmResource {

    /**
     * 闪购键策略资源
     */
    public static Integer MT_VM_SHORT_CURTS_RESOURCE_TYPE = 1;
    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

}
