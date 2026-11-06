package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.model.bean.auto._TStVmShortcuts;
import org.apache.cayenne.Cayenne;

public class TStVmShortcuts extends _TStVmShortcuts {

    public TMtSku getSku() {
        Integer skuId = getSkuId();
        if (IntegerUtils.isPositiveInteger(skuId)) return Cayenne.objectForPK(getObjectContext(), TMtSku.class, skuId);
        return null;
    }

}
