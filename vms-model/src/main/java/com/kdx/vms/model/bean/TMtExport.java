package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.auto._TMtExport;
import org.apache.cayenne.Cayenne;

public class TMtExport extends _TMtExport {
    public static final int TYPE_STOCK = 1;
    public static final int TYPE_ORDER = 2;
    public static final int TYPE_MANUAL = 3;

    public static final int TYPE_ORDER_EXPORT = 1;
    public static final int TYPE_VENDOUT_EXPORT = 2;
    public static final int TYPE_STOCK_EXPORT = 3;

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }
    public String getUrl() {
        return PropertiesLoader.map.get("imgUrlPrefix") + "/" + getAddress();
    }

    public String getTime() {
        return DateUtils.getTime2(getCreateTime());
    }

    public String getName() {
        String address = getAddress();
        if(null != address){
            return address.split("/")[address.split("/").length - 1].replaceAll(".xlsx", "").replaceAll(".xls", "");
        }
        return null;
    }

}
