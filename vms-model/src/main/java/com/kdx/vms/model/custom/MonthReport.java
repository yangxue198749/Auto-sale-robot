package com.kdx.vms.model.custom;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangshuxi on 2017/10/12.
 */
public class MonthReport {
    private String month;
    private List<DayData> dayDataList;
    private List<SkuData> skuDataList;
    private Integer totalVolume;
    private Integer totalAmount;
    private Integer vmCount;
    private Chart chart;
    private Integer dayVolume;
    private Integer dayAmount;
    private Integer yesterdayVolume;
    private Integer yesterdayAmount;
    private String time;

    public String getType() {
        return "ok";
    }

    public String getTime() {
        return time;
    }

    public Integer getDayVolume() {
        return dayVolume;
    }

    public String getDayAmount() {
        return StringUtil.formatAmount(dayAmount, 2) + "";
    }

    public Integer getYesterdayVolume() {
        return yesterdayVolume;
    }

    public MonthReport setYesterdayVolume(Integer yesterdayVolume) {
        this.yesterdayVolume = yesterdayVolume;
        return this;
    }

    public String getYesterdayAmount() {
        return StringUtil.formatAmount(yesterdayAmount, 2) + "";
    }

    public MonthReport setYesterdayAmount(Integer yesterdayAmount) {
        this.yesterdayAmount = yesterdayAmount;
        return this;
    }

    public String getMonth() {
        return month;
    }

    public MonthReport setMonth(String month) {
        this.month = month;
        return this;
    }

    public Chart getChart() {
        return chart;
    }

    public MonthReport setDayDataList(List<DayData> dayDataList) {
        this.dayDataList = dayDataList;
        return this;
    }

    public List<SkuData> getSkuDataList() {
        return skuDataList;
    }

    public MonthReport setSkuDataList(List<SkuData> skuDataList) {
        this.skuDataList = skuDataList;
        return this;
    }

    public Integer getTotalVolume() {
        return totalVolume;
    }

    public MonthReport setTotalVolume(Integer totalVolume) {
        this.totalVolume = totalVolume;
        return this;
    }

    public String getTotalAmount() {
        return StringUtil.formatAmount(totalAmount, 2) + "";
    }

    public MonthReport setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public Integer getVmCount() {
        return vmCount;
    }

    public MonthReport setVmCount(Integer vmCount) {
        this.vmCount = vmCount;
        return this;
    }


    public static class DayData{
        private String date;
        private Integer salesVolume;
        private Integer salesAmount;

        public static DayData newInstance(String date) {
            return new DayData().setDate(date).setSalesAmount(0).setSalesVolume(0);
        }

        public String getDate() {
            return date;
        }

        public DayData setDate(String date) {
            this.date = date;
            return this;
        }

        public Integer getSalesVolume() {
            return salesVolume;
        }

        public DayData setSalesVolume(Integer salesVolume) {
            this.salesVolume = salesVolume;
            return this;
        }

        public Double getSalesAmount() {
            return StringUtil.formatAmount(salesAmount, 2);
        }

        public DayData setSalesAmount(Integer salesAmount) {
            this.salesAmount = salesAmount;
            return this;
        }

        public synchronized DayData addSalesAmount(String amount) {
            this.salesAmount = new BigDecimal(this.salesAmount).add(new BigDecimal(amount)).intValue();
            return this;
        }

        public synchronized DayData addSalesVolume(String volume) {
            this.salesVolume = new BigDecimal(this.salesVolume).add(new BigDecimal(volume)).intValue();
            return this;
        }
    }

    public static class SkuData implements Comparable<SkuData> {
        private String skuName;
        private Integer salesVolume;
        private Integer salesAmount;

        public String getSkuName() {
            return skuName;
        }

        public SkuData setSkuName(String skuName) {
            this.skuName = skuName;
            return this;
        }

        public Integer getSalesVolume() {
            return salesVolume;
        }

        public SkuData setSalesVolume(Integer salesVolume) {
            this.salesVolume = salesVolume;
            return this;
        }

        public Double getSalesAmount() {
            return StringUtil.formatAmount(salesAmount, 2);
        }

        public SkuData setSalesAmount(Integer salesAmount) {
            this.salesAmount = salesAmount;
            return this;
        }
        public static SkuData newInstance(String skuName){
            return new SkuData().setSkuName(skuName).setSalesAmount(0).setSalesVolume(0);
        }

        public synchronized SkuData addSalesAmount(String amount) {
            this.salesAmount = new BigDecimal(this.salesAmount).add(new BigDecimal(amount)).intValue();
            return this;
        }

        public synchronized SkuData addSalesVolume(String volume) {
            this.salesVolume = new BigDecimal(this.salesVolume).add(new BigDecimal(volume)).intValue();
            return this;
        }

        @Override
        public int compareTo(SkuData skuData) {
            return skuData.getSalesVolume() - this.getSalesVolume();
        }
    }


    public static class Chart {
        private List<Integer> dayList;
        private List<Integer> salesVolumeList;
        private List<Double> salesAmountList;

        public static Chart of(MonthReport monthReport) {
            String month = monthReport.month;
            Integer days = DateUtils.getDaysOfMonth(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1]));
            List<MonthReport.DayData> list = monthReport.dayDataList;
            Chart chart = new Chart(days);
            if (null != list && list.size() > 0) {
                list.forEach(chart::fillDay);
            }
            return chart;
        }

        public Chart(Integer days) {
            this.dayList = new ArrayList<>();
            this.salesVolumeList = new ArrayList<>();
            this.salesAmountList = new ArrayList<>();
            for (int i = 1; i <= days; i++) {
                this.dayList.add(i);
                this.salesVolumeList.add(0);
                this.salesAmountList.add(0.0);
            }
        }

        public void fillDay(MonthReport.DayData dayData) {
            int day = DateUtils.getDay(dayData.getDate());
            this.salesVolumeList.set(day - 1, dayData.getSalesVolume());
            this.salesAmountList.set(day - 1, dayData.getSalesAmount());
        }


        public List<Integer> getDayList() {
            return dayList;
        }

        public Chart setDayList(List<Integer> dayList) {
            this.dayList = dayList;
            return this;
        }

        public List<Integer> getSalesVolumeList() {
            return salesVolumeList;
        }

        public Chart setSalesVolumeList(List<Integer> salesVolumeList) {
            this.salesVolumeList = salesVolumeList;
            return this;
        }

        public List<Double> getSalesAmountList() {
            return salesAmountList;
        }

        public Chart setSalesAmountList(List<Double> salesAmountList) {
            this.salesAmountList = salesAmountList;
            return this;
        }
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public MonthReport chart() {
        this.chart = Chart.of(this);
        return this;
    }

    public MonthReport sort() {
        Collections.sort(this.skuDataList);
        return this;
    }

    public MonthReport fillDay(String time) {
        this.time = time;
        DayData dayData1 = this.dayDataList.stream().filter(dayData -> dayData.getDate().equals(time)).findFirst().orElse(null);
        if (null == dayData1) {
            dayData1 = new DayData();
            dayData1.setSalesAmount(0);
            dayData1.setSalesVolume(0);
        }
        this.dayAmount = dayData1.salesAmount;
        this.dayVolume = dayData1.salesVolume;
        return this;
    }

    public MonthReport fillTotal() {
        this.totalVolume = this.dayDataList.stream().mapToInt(dayVolume -> dayVolume.salesVolume).sum();
        this.totalAmount = this.dayDataList.stream().mapToInt(dayVolume -> dayVolume.salesAmount).sum();
        return this;
    }
}
