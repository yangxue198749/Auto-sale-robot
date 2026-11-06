package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.utils.VmVersion;

/**
 * Created by yangshuxi on 2017/6/21.
 */
public interface IDealVmUpgradeRequestService {
    public TMtUpgradePlan findHighestVersion(TMtVm mtVm, String version, VmVersion type);

    /**
     * 当前售货机下一版本售货机程序
     *
     * @param mtVm
     * @return
     */
    public TMtUpgradePlan findNextVmBoxVersion(TMtVm mtVm, String version);

    public String updateDownloadedVersion(TMtVm mtVm, String version, Integer type);

}
