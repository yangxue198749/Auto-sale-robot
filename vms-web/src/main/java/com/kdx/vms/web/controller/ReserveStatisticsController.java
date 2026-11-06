package com.kdx.vms.web.controller;

import com.kdx.mp.web.excel.ExportExcel;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TReserveStatistics;
import com.kdx.vms.service.IReserveStatisticsService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 预定统计
 * Created by zhangguodong on 2017/8/15.
 */
@Controller
@RequestMapping("reserveStatistics")
public class ReserveStatisticsController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IReserveStatisticsService reserveStatisticsService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        request.setAttribute("date", DateUtils.getTime1());
        return "reserveStatistics/reserve_statistics_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize, String innerCode, String skuName, String statisticsDate) {

        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/reserveStatistics/listPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = reserveStatisticsService.findByPage(token.getObjectContext(), innerCode, skuName, statisticsDate, pageSize, token.getSysUser());
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("reserveStatisticsList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "reserveStatistics/page/reserve_statistics_page";
    }

    @RequestMapping("export")
    public String export( HttpServletResponse response, String innerCode, String skuName, String statisticsDate) {
        logger.info("[export]");
        Integer totalSize = 10000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            List list = reserveStatisticsService.findByPage(token.getObjectContext(),innerCode, skuName, statisticsDate, null,token.getSysUser());
            String fileName = "预定统计" + DateUtils.getTime1().replaceAll("-", "") + ".xlsx";
            new ExportExcel<>(null, TReserveStatistics.class).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }}
