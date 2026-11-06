package com.kdx.vms.model.custom;

/**
 *
 * Created by liyang on 17-1-3.
 */
public class GiroInfo {

    private Integer customerId;
    private String  customerName;
    private String  customerAccount;
    private String  customerAccName;
    private String  month;
    private Integer totle;
    private Integer count;

    public GiroInfo() {
        totle = 0;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public String getCustomerAccName() {
        return customerAccName;
    }

    public void setCustomerAccName(String customerAccName) {
        this.customerAccName = customerAccName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getTotle() {
        return totle;
    }

    public synchronized void addTotle(Integer totle) {
        this.totle += totle;
    }

    public String getCount() {
        return (count / 100) + "." + ((count % 100) < 10 ? "0" + (count % 100) : (count % 100));
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
