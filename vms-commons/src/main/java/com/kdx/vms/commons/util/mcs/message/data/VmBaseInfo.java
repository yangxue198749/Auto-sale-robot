package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;

/**
 *
 * Created by liyang on 2017/5/22.
 */
public class VmBaseInfo implements Serializable{

    private static final long serialVersionUID = -4394107669064419932L;
    private String nodeName;
    private String nodeAddress;
    private Integer printer;
    private Integer themeFormat;
    private Integer reserve;
    private Integer vmType;
    private String[] supportedPayTypes;

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getPrinter() {
        return printer;
    }

    public void setPrinter(Integer printer) {
        this.printer = printer;
    }

    public Integer getThemeFormat() {
        return themeFormat;
    }

    public void setThemeFormat(Integer themeFormat) {
        this.themeFormat = themeFormat;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getVmType() {
        return vmType;
    }

    public VmBaseInfo setVmType(Integer vmType) {
        this.vmType = vmType;
        return this;
    }

    public String[] getSupportedPayTypes() {
        return supportedPayTypes;
    }

    public void setSupportedPayTypes(String[] supportedPayTypes) {
        this.supportedPayTypes = supportedPayTypes;
    }
}
