package com.kdx.mp.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.mp.service.IMpOrderReportService;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.custom.MonthReport;
import com.kdx.vms.model.custom.YearReport;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/10/12.
 */
@Controller
@RequestMapping("mp/slReport")
public class MpOrderReportController {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Resource
    private IMpOrderReportService mpOrderReportService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        return "mp/slReport/slReport_list";
    }

    @RequestMapping(value = "otherdayList", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String loadDayData(HttpServletRequest request, String time) {
        try {
            if (null == time || time.split("-").length != 3) {
                return null;
            }
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            TMtCompany company = currentUser.getCompany();
            MonthReport monthReport = mpOrderReportService.calculateMonthReport(time, company).sort().chart().fillDay(time).fillTotal();
            return monthReport.toJson();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("type", "error");
        map.put("message", "加载失败");
        return JSON.toJSONString(map);
    }

    @RequestMapping("monthReport")
    public String monthReport(HttpServletRequest request) {
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            TMtCompany company = currentUser.getCompany();
            List<YearReport> list = mpOrderReportService.calculateYearReport(company);
            Collections.sort(list);
            request.setAttribute("list", list);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/slReport/slReport_monthList";
    }



}
