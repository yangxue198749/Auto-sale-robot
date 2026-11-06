package com.kdx.vms.model.custom;

import com.kdx.vms.commons.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by yangshuxi on 2017/10/12.
 */
public class YearReport implements Comparable<YearReport> {
    private String month;
    private Integer salesVolume;
    private Integer salesAmount;

    public Integer getSalesVolume() {
        return salesVolume;
    }

    public YearReport setSalesVolume(Integer salesVolume) {
        this.salesVolume = salesVolume;
        return this;
    }

    public String getSalesAmount() {
        return StringUtil.formatAmount(salesAmount, 2) + "";
    }

    public YearReport setSalesAmount(Integer salesAmount) {
        this.salesAmount = salesAmount;
        return this;
    }

    public String getMonth() {
        return month.replaceAll("-", "年") + "月";
    }

    public YearReport setMonth(String month) {
        this.month = month;
        return this;
    }

    public synchronized YearReport addSalesAmount(String amount) {
        this.salesAmount = new BigDecimal(this.salesAmount).add(new BigDecimal(amount)).intValue();
        return this;
    }

    public synchronized YearReport addSalesVolume(String volume) {
        this.salesVolume = new BigDecimal(this.salesVolume).add(new BigDecimal(volume)).intValue();
        return this;
    }

    @Override
    public int compareTo(YearReport monthData) {
        return StringUtil.getIntValue(monthData.getMonth().split("年")[1], 0) - StringUtil.getIntValue(this.getMonth().split("年")[1], 0);
    }
}

