package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.cache.CardCache;
import com.kdx.vms.commons.util.*;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.OrderSaleVo;
import com.kdx.vms.model.enumeration.OrderRefundType;
import com.kdx.vms.mq.PublisherPool;
import com.kdx.vms.service.IMtExportService;
import com.kdx.vms.service.IOrderService;
import com.kdx.vms.service.IRefundService;
import com.kdx.vms.service.VendoutService;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.kdx.vms.model.bean.TMtExport.TYPE_ORDER_EXPORT;

/**
 * Created by yangshuxi on 2017/11/17.
 */
@Controller
@RequestMapping(value = "order")
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IOrderService orderService;
    @Resource
    private VendoutService vendoutService;
    @Resource
    private IRefundService refundService;
    @Resource
    IMtExportService mtExportService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        request.setAttribute("date", DateUtils.getTime1());
        Map<Integer, String> map = orderService.findPayType(TokenUtil.getToken().getSysUser());
        CardCache.refresh(map);
        List<TMtCardType> list = orderService.findPayTypeList(TokenUtil.getToken().getSysUser());
        list = list.stream().filter(cardType -> cardType.getTypeCode() > 0).filter(cardType -> cardType.getTypeCode() != 2000).collect(Collectors.toList());
        request.setAttribute("payTypeList", list);
        return "order/order_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize,
                           String startDate, String endDate,
                           Integer searchType, String searchValue,
                           String skuName, Integer status, Integer orderType,
                           Integer payType) {
        logger.info("[listPage]");
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/order/listPage";
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();
            if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
                startDate = endDate = DateUtils.getTime1();
            }
            List<?> list = orderService.findSaleOrder(sysUser, searchType, searchValue,
                    skuName, status, orderType, payType,
                    startDate, endDate, pageSize);
            logger.info("list:" + list);
            List<OrderSaleVo> subList = PageUtil.formatList(pageNo, pageSize, list)
                    .stream()
                    .map(TOrderDetail.class::cast)
                    .map(OrderSaleVo::of)
                    .collect(Collectors.toList());
            request.setAttribute("subList", subList);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", list.size());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "order/table/order_table";
    }

    /**
     * 退款
     *
     * @param detailId
     * @return
     */
    @RequestMapping("refund")
    @ResponseBody
    public Map<String, Object> refundByDetailId(Integer detailId) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            logger.info(currentUser.getUserAccount() + "发起退款，orderDetailId=" + detailId);
            TOrderMaster tOrderMaster = vendoutService.findByDetailId(token.getObjectContext(), detailId);
            TOrderRefund refund = new TOrderRefund();
            if (tOrderMaster.getOrderType().equals(TOrderMaster.RESERVE)) {
                refund = refundService.launchRefund(currentUser, OrderRefundType.VMS_GOOD, (String) PropertiesLoader.map.get("reserveRefundTopic"), detailId);
            } else if (tOrderMaster.getOrderType().equals(TOrderMaster.IMMEDIATE)) {
                refund = refundService.launchRefund(currentUser, OrderRefundType.VMS_GOOD, (String) PropertiesLoader.map.get("immediateRefundTopic"), detailId);
            }
            if (refund != null) {
                return ReturnCode.SUCCESS.toMap();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.FAIL.toMap();
    }

    @RequestMapping("export")
    @ResponseBody
    public Map<String, Object> export(HttpServletRequest request,
                                      @CookieValue(value = "pageSize", required = false) Integer cookiePageSize,
                                      String startDate, String endDate,
                                      Integer searchType, String searchValue,
                                      String skuName, Integer status, Integer orderType,
                                      Integer payType) {
        logger.info("[export]");
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("type", "ok");
            map.put("message", "导出成功，请稍后前往报表下载处下载");
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();
            Integer companyId = sysUser.getCompany().getId();
            if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
                startDate = endDate = DateUtils.getTime1();
            }
            if(DateUtils.daysBetween(startDate,endDate)>30){
                map.put("type","error");
                map.put("message", "导出数据范围最大31天");
                return map;
            }
            String condition= companyId+"_"+startDate+"_"+endDate + "_"+TMtExport.TYPE_ORDER_EXPORT;
            TMtExport exportCondition = mtExportService.findExportByMatchCondition(sysUser.getObjectContext(),condition);
            if(exportCondition != null){
                map.put("type","error");
                map.put("message", "导出文件存在，请稍后前往报表下载处下载");
                return map;
            }
            TMtExport export = sysUser.getObjectContext().newObject(TMtExport.class);
            export.setType(TMtExport.TYPE_ORDER);
            export.setCompanyId(companyId);
            export.setCreateTime(new Date());
            export.setCtor(sysUser.getId());
            export.setCondition(condition);
            sysUser.getObjectContext().commitChanges();

            Map<String,Object> message = buildOrderMessage(sysUser.getId(), TYPE_ORDER_EXPORT,export.getId(),startDate, endDate);
            PublisherPool.getInstance().publish((String) PropertiesLoader.map.get("payExchangeName"), JSON.toJSONString(message), (String) PropertiesLoader.map.get("exportRequestTopic"));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            map.put("type", "error");
            map.put("message", "导出失败，请重试");
        }
        return map;
    }

    private Map<String, Object> buildOrderMessage(Integer userId, Integer type, Integer exportId, String beginTime, String endTime) {
        Map<String, Object> head = new HashMap<>();
        head.put("id", UUID.randomUUID().toString());
        head.put("ts", System.currentTimeMillis());
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("beginTime", beginTime);
        body.put("endTime", endTime);
        body.put("exportId", exportId);
        body.put("type", type);
        Map<String, Object> message = new HashMap<>();
        message.put("head", head);
        message.put("body", body);
        return message;
    }
}
