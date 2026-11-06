package com.kdx.vms.model.custom;

import java.util.List;

/**
 * Created by guoyang on 2018/2/9.
 */
public class OpenApiInfo {
    private Integer companyId;
    private String companyName;
    private List<Integer> serviceId;
    private List<String> serviceName;
    private String customerId;
    private String key;

    public List<Integer> getServiceId() {
        return serviceId;
    }

    public void setServiceId(List<Integer> serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getServiceName() {
        return serviceName;
    }

    public void setServiceName(List<String> serviceName) {
        this.serviceName = serviceName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
