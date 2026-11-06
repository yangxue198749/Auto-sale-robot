package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.auto._TMtVmShortcuts;

public class TMtVmShortcuts extends _TMtVmShortcuts implements Comparable<TMtVmShortcuts>{

    /**绑定类型为商品**/
    public final  static Integer BIND_TYPE_SKU = 1;
    /**绑定类型为链接**/
    public final  static Integer BIND_TYPE_URL = 2;

    @Override
    public int compareTo(TMtVmShortcuts o) {
        return StringUtil.getIntValue(this.getShortcutsCode(),0)-StringUtil.getIntValue(o.getShortcutsCode(),0);
    }
}
