package com.kdx.mp.service;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.custom.MonthReport;
import com.kdx.vms.model.custom.YearReport;

import java.util.List;

/**
 * Created by yangshuxi on 2017/10/19.
 */
public interface IMpOrderReportService {
    public MonthReport calculateMonthReport(String month, TMtCompany company);

    public List<YearReport> calculateYearReport(TMtCompany company);
}
