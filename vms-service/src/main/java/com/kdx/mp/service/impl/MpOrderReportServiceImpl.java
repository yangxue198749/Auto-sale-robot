package com.kdx.mp.service.impl;

import com.kdx.mp.service.IMpOrderReportService;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.custom.MonthReport;
import com.kdx.vms.model.custom.YearReport;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.redis.MyJedisCluster;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/10/19.
 */
@Service
public class MpOrderReportServiceImpl implements IMpOrderReportService {
    @Resource
    private MtVmDao mtVmDao;
    @Resource
    private MyJedisCluster myJedisCluster;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String DAY_KEY = "report_day_";
    private final String YEAR_KEY = "report_month_";

    @Override
    public MonthReport calculateMonthReport(String date, TMtCompany company) {
        try {
            String month = date2month(date);
            List<Integer> companyList = company.getAllChildCompany()
                    .parallelStream()
                    .map(TMtCompany::getId)
                    .collect(Collectors.toList());
            //计算vmCount
            List<TMtVm> vmList = mtVmDao.findAllMtVmByCompany(company.getObjectContext(), company);
            //计算昨天
            BigDecimal yesterdayVolume = new BigDecimal(0);
            BigDecimal yesterdayAmount = new BigDecimal(0);
            if (date.equals(DateUtils.getTime1())) {//只有查看今天时才需要查询昨天
                String yesterday = DateUtils.getTime1(DateUtils.getDayBefore(1));
                List<String> yesterdayList = companyList
                        .parallelStream()
                        .map(id -> {
                            String key = DAY_KEY + date2month(yesterday) + "_" + id;
                            String result = myJedisCluster.hget(key, yesterday);
                            logger.debug("hget key=" + key + ",field=" + yesterday + ",result=\n" + result);
                            return result;
                        })
                        .filter(s -> null != s)
                        .collect(Collectors.toList());
                for (String s : yesterdayList) {
                    yesterdayVolume = yesterdayVolume.add(new BigDecimal(s.split("_")[0]));
                    yesterdayAmount = yesterdayAmount.add(new BigDecimal(s.split("_")[1]));
                }
            }
            //计算每天
            List<Map<String, String>> dayList = companyList
                    .parallelStream()
                    .map(id -> {
                        String key = DAY_KEY + month + "_" + id;
                        Map<String, String> result = myJedisCluster.hgetAll(key);
                        logger.debug("hgetAll key=" + key + ",result=\n" + result);
                        return result;
                    })
                    .collect(Collectors.toList());
            List<MonthReport.DayData> dayDataList = this.sumDayList(dayList);
            //计算每种商品
            List<MonthReport.SkuData> skuDataList = new ArrayList<>();
            if (date.equals(DateUtils.getTime1())) {//只有查看今天时才需要查询商品列表
                List<Map<String, String>> skuList = companyList
                        .parallelStream()
                        .map(id -> {
                            String key = YEAR_KEY + month + "_" + id;
                            Map<String, String> result = myJedisCluster.hgetAll(key);
                            logger.debug("hgetAll key=" + key + ",result=\n" + result);
                            return result;
                        })
                        .collect(Collectors.toList());
                skuDataList = this.sumSkuList(skuList, company.getObjectContext());
            }
            MonthReport monthReport = new MonthReport();
            monthReport.setVmCount(vmList.size())
                    .setYesterdayAmount(yesterdayAmount.intValue())
                    .setYesterdayVolume(yesterdayVolume.intValue())
                    .setMonth(month)
                    .setDayDataList(dayDataList)
                    .setSkuDataList(skuDataList);
            return monthReport;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public List<YearReport> calculateYearReport(TMtCompany company) {
        try {
            List<Integer> companyList = company.getAllChildCompany()
                    .parallelStream()
                    .map(TMtCompany::getId)
                    .collect(Collectors.toList());
            Integer year = DateUtils.getYear(new Date());
            Integer month = DateUtils.getMonth(new Date());
            List<String> monthList = new ArrayList<>();
            for (int i = 1; i <= month; i++) {
                String date = year + "-" + (i >= 10 ? i : "0" + i);
                monthList.add(date);
            }
            return monthList.parallelStream().map(s -> this.sumMonthList(s, companyList)).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    public List<MonthReport.DayData> sumDayList(List<Map<String, String>> list) {//key = 日期，value = 数量-销售额
        try {
            Map<String, MonthReport.DayData> map = new HashMap<>();
            List<MonthReport.DayData> dayDataList = new ArrayList<>();
            list.forEach(companyMap -> {
                for (String date : companyMap.keySet()) {
                    String value = companyMap.get(date);
                    if (map.containsKey(date)) {
                        MonthReport.DayData dayData = map.get(date);
                        dayData.addSalesVolume(value.split("_")[0]).addSalesAmount(value.split("_")[1]);
                        map.put(date, dayData);
                    } else {
                        MonthReport.DayData dayData = MonthReport.DayData.newInstance(date);
                        dayData.addSalesVolume(value.split("_")[0]).addSalesAmount(value.split("_")[1]);
                        map.put(date, dayData);
                    }
                }
            });
            map.forEach((s, dayData) -> dayDataList.add(dayData));
            return dayDataList;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }


    public List<MonthReport.SkuData> sumSkuList(List<Map<String, String>> list, ObjectContext context) {//key = skuId，value = 数量-销售额
        try {
            SelectQuery query = new SelectQuery(TMtSku.class);
            List<TMtSku> skuList = context.performQuery(query);
            Map<String, String> skuMap = new HashMap<>();
            for (TMtSku mtSku : skuList) {
                skuMap.put(mtSku.getId() + "", mtSku.getSkuName());
            }
            Map<String, MonthReport.SkuData> map = new HashMap<>();
            List<MonthReport.SkuData> skuDataList = new ArrayList<>();
            list.forEach(companyMap -> {
                for (String skuId : companyMap.keySet()) {
                    String value = companyMap.get(skuId);
                    if (map.containsKey(skuId)) {
                        MonthReport.SkuData skuData = map.get(skuId);
                        skuData.addSalesVolume(value.split("_")[0]).addSalesAmount(value.split("_")[1]);
                        map.put(skuId, skuData);
                    } else {
                        MonthReport.SkuData skuData = MonthReport.SkuData.newInstance(skuMap.get(skuId));
                        skuData.addSalesVolume(value.split("_")[0]).addSalesAmount(value.split("_")[1]);
                        map.put(skuId, skuData);
                    }
                }
            });
            map.forEach((s, dayData) -> skuDataList.add(dayData));
            return skuDataList;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }


    public YearReport sumMonthList(String month, List<Integer> companyList) {
        YearReport yearReport = new YearReport();
        yearReport.setMonth(month);
        yearReport.setSalesVolume(0);
        yearReport.setSalesAmount(0);
        try {
            List<Map<String, String>> list = companyList
                    .parallelStream()
                    .map(id -> {
                        String key = YEAR_KEY + month + "_" + id;
                        Map<String, String> result = myJedisCluster.hgetAll(key);
                        logger.debug("hgetAll key=" + key + ",result=\n" + result);
                        return result;
                    })
                    .collect(Collectors.toList());
            list.forEach(companyMap -> {
                for (String skuId : companyMap.keySet()) {
                    String value = companyMap.get(skuId);
                    yearReport.addSalesVolume(value.split("_")[0]);
                    yearReport.addSalesAmount(value.split("_")[1]);
                }
            });
            return yearReport;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return yearReport;
    }

    private static String date2month(String date) {
        return date.substring(0, date.length() - 3);
    }
}
