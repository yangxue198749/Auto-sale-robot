package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtSim;
import org.apache.cayenne.Cayenne;

public class TMtSim extends _TMtSim {

    /**
     * 中国移动
     */
    public static final int ROVIDER_CHINA_MOBILE = 1;

    /**
     * 中国联通
     */
    public static final int PROVIDER_CHINA_UNICOM = 2;

    /**
     * 中国电信
     */
    public static final int PROVIDER_CHINA_TELECOM = 3;

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

}
