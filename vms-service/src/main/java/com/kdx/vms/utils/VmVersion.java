package com.kdx.vms.utils;

import com.kdx.vms.model.bean.TMtUpgradePackage;

/**
 * Created by yangshuxi on 2017/6/21.
 */
public enum VmVersion {
    VM_BOX(0,"vmBox", TMtUpgradePackage.VMBOX_TYPE),
    COMMUNICATION_BOARD(1,"通讯板软件", TMtUpgradePackage.COMMUNICATION_BOARD_TYPE),
    VEND_OUT_BOARD(2,"出货板软件", TMtUpgradePackage.DELIVERY_BOARD_TYPE)
    ;
    private int    level;
    private String name;
    private Integer type;

    VmVersion(int level, String name, Integer type) {
        this.level = level;
        this.name = name;
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }
}
