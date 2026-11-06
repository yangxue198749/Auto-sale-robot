package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.VmStockDetail;
import com.kdx.vms.mq.PublisherPool;
import com.kdx.vms.service.IMtExportService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by liyang on 16-12-15.
 */
@Controller
@RequestMapping(value = "vmStock")
public class VmStockController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutorService asynThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Resource()
    private IMtVmService mtVmService;
    @Resource
    IMtExportService mtExportService;

    @RequestMapping("list")
    public String list() {
        return "vmStock/stock_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();

            String selectType = request.getParameter("selectType");
            logger.info("selectType:" + selectType);
            String selectValue = request.getParameter("selectValue");
            logger.info("selectText:" + selectValue);
            String innerCode = null;
            String nodeName = null;
            if (!StringUtil.isEmpty(selectType)) {
                switch (selectType) {
                    case "1":
                        innerCode = selectValue;
                        break;
                    case "2":
                        nodeName = selectValue;
                        break;
                }
            }
            List<TMtVm> list = mtVmService.getMtVmForStocks(sysUser.getCompany(),
                    innerCode,
                    nodeName,
                    pageSize);

            totalSize = list.size();
            Integer start = (pageNo - 1) * pageSize;
            List<TMtVm> vmList = list.stream().skip(start).limit(pageSize).collect(Collectors.toList());

            request.setAttribute("vmList", vmList);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "vmStock/table/vm_table";
    }

    @RequestMapping("stockList")
    public String stockList(HttpServletRequest request,
                            Integer pageSize,
                            Integer pageNo,
                            @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "stockList";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();

            String vmId = request.getParameter("vmId");
            logger.info("vmId:" + vmId);
            List<VmStockDetail> stockInfoList = null;
            if (null != vmId) {
                TMtVm mtVm = mtVmService.getMtVm(sysUser.getObjectContext(), Integer.parseInt(vmId));
                List<TMtVmChannel> channelList = mtVm.getChannelInfo().stream().
                        filter(channel -> channel.getSku() != null).collect(Collectors.toList());

                totalSize = channelList.size();
                Integer start = (pageNo - 1) * pageSize;

                stockInfoList = channelList.stream().sorted(
                        (o1, o2) -> {
                            String str1 = Optional.ofNullable(o1.getManufactureEndDate())
                                    .map(date -> DateUtils.getTime1(date)).orElse("");
                            String str2 = Optional.ofNullable(o2.getManufactureEndDate())
                                    .map(date -> DateUtils.getTime1(date)).orElse("");
                            return str1.equals(str2) ? o1.compareTo(o2) : str1.equals("") || str2.equals("") ? str2.compareTo(str1) : str1.compareTo(str2);
                        }
                ).skip(start)
                        .limit(pageSize)
                        .map(channel -> {
                            VmStockDetail detail = VmStockDetail.newInstance();
                            TMtSku sku = channel.getSku();
                            detail.setName(sku.getSkuName() + sku.getSkuUnit());
                            detail.setCurrCount(channel.getCurrNum());
                            detail.setChannel(channel.getChannelCode());
                            detail.setHasStock(channel.getHasStock());
                            detail.setMaxCount(channel.getTempMaxCapacity());
//                            Integer dayCount = channel.getManufactureEndDate() == null ? null : DateUtils.daysBetween(new Date(), channel.getManufactureEndDate());
//                            if (null != dayCount && dayCount > 0) dayCount++;
//                            if (null != dayCount && dayCount < 0) dayCount--;
                            detail.setDayCount(StringUtil.getIntValue(channel.getDay(),0));
                            return detail;
                        }).collect(Collectors.toList());

            }
            request.setAttribute("vmId", vmId);
            request.setAttribute("vmStockList", stockInfoList);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "vmStock/table/stock_table";
    }


    @RequestMapping("export")
    @ResponseBody
    public Map<String, Object> exportStockList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "ok");
        map.put("message", "导出成功，请稍后前往报表下载处下载");

        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();
            Integer companyid = sysUser.getCompany().getId();
            String condition = companyid + "_" + DateUtils.getTime1();
            TMtExport exportCondition = mtExportService.findExportByMatchCondition(sysUser.getObjectContext(), condition);
            if (exportCondition != null) {
                map.put("type", "error");
                map.put("message", "导出文件存在，请稍后前往报表下载处下载");
                return map;
            }
            TMtExport export = sysUser.getObjectContext().newObject(TMtExport.class);
            export.setType(TMtExport.TYPE_MANUAL);
            export.setCompanyId(companyid);
            export.setCreateTime(new Date());
            export.setCtor(sysUser.getId());
            export.setCondition(condition);
            sysUser.getObjectContext().commitChanges();

            Map<String, Object> message = bulidStockMessage(sysUser, TMtExport.TYPE_STOCK_EXPORT, export.getId());
            PublisherPool.getInstance().publish((String) PropertiesLoader.map.get("payExchangeName"), JSON.toJSONString(message), (String) PropertiesLoader.map.get("exportRequestTopic"));
        } catch (Exception e) {
            logger.error("exportStockList is Exception cause by=" + e.getMessage(), e);
            map.put("type", "error");
            map.put("message", "导出失败，请重试");
        }
        return map;
    }

    private Map<String, Object> bulidStockMessage(TSysUser sysUser, Integer type, Integer exportId) {
        Map<String, Object> head = new HashMap<>();
        head.put("id", UUID.randomUUID().toString());
        head.put("ts", System.currentTimeMillis());
        Map<String, Object> body = new HashMap<>();
        body.put("userId", sysUser.getId());
        body.put("type", type);
        body.put("exportId", exportId);
        Map<String, Object> message = new HashMap<>();
        message.put("head", head);
        message.put("body", body);
        return message;
    }

}

