package com.kdx.vms.model.custom;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;

/**
 * Created by yangshuxi on 2017/9/30.
 */
public class MpVmStatus implements Comparable<MpVmStatus>{
    public static final String RED = "red";
    public static final String GREEN = "";
    private Integer vmId;
    private String innerCode;
    private String vmcStatus;

    public String getStockStatus() {
        return stockStatus;
    }

    public MpVmStatus setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
        return this;
    }

    private String networkStatus;
    private String stockStatus;
    private String nodeName;

    public static MpVmStatus of(TMtVm mtVm) {
        MpVmStatus mpVmStatus = new MpVmStatus(mtVm.getId(), mtVm.getInnerCode(), mtVm.getNodeName());
        mpVmStatus.setNetworkStatus(mtVm.getNetworkStatus() > 0 ? RED : GREEN);
        mpVmStatus.setVmcStatus(StringUtil.getIntValue(mtVm.getVmcStatus(), 0) > 0 ? RED : GREEN);
        mpVmStatus.setStockStatus(mtVm.getStockStatus() > 0 ? RED : GREEN);
        return mpVmStatus;
    }

    private MpVmStatus(Integer vmId, String innerCode, String nodeName) {
        this.vmId = vmId;
        this.innerCode = innerCode;
        this.nodeName = nodeName;
    }

    public MpVmStatus setVmcStatus(String vmcStatus) {
        this.vmcStatus = vmcStatus;
        return this;
    }

    public MpVmStatus setNetworkStatus(String networkStatus) {
        this.networkStatus = networkStatus;
        return this;
    }


    public Integer getVmId() {
        return vmId;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public String getVmcStatus() {
        return vmcStatus;
    }

    public String getNetworkStatus() {
        return networkStatus;
    }


    public String getNodeName() {
        return nodeName;
    }

    @Override
    public int compareTo(MpVmStatus o) {
        //先判断设备状态，优先显示故障的
        if (!o.getVmcStatus().equals(GREEN) && this.getVmcStatus().equals(GREEN)) return 1;
        if (o.getVmcStatus().equals(GREEN) && !this.getVmcStatus().equals(GREEN)) return -1;
        //再判断网络状态，优先显示故障的
        if (!o.getNetworkStatus().equals(GREEN) && this.getNetworkStatus().equals(GREEN)) return 1;
        if (o.getNetworkStatus().equals(GREEN) && !this.getNetworkStatus().equals(GREEN)) return -1;
        //再判断商品状态，优先显示缺货的
        if (!o.getStockStatus().equals(GREEN) && this.getStockStatus().equals(GREEN)) return 1;
        if (o.getStockStatus().equals(GREEN) && !this.getStockStatus().equals(GREEN)) return -1;
        //最后按照innerCode从小到大排序
        String innerCode1 = this.getInnerCode();
        String innerCode2 = o.getInnerCode();
        int i1 = StringUtil.getIntValue(innerCode1, 0);
        int i2 = StringUtil.getIntValue(innerCode2, 0);
        return i1 - i2;
    }
}
