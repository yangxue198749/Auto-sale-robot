package com.kdx.mp.web.controller;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IOrderService;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出货异常
 * Created by zhangguodong on 2017/4/25.
 */
@RequestMapping("mp/outSkuError")
@Controller
public class MpOutSkuErrorController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IOrderService orderService;

    /**
     * 进入出货异常页面
     *
     * @return
     */
    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        request.setAttribute("time", StringUtil.dateToStr(new Date(), StringUtil.YMDPattern));
        return "mp/slOrder/out_error_list";
    }

    /**
     * 获取下一页数据
     *
     * @param request
     * @param pageNo
     * @param pageSize
     * @param time
     * @return
     */
    @RequestMapping("listPage")
    @ResponseBody
    public Map<String, Object> listPage(HttpServletRequest request, Integer pageNo, Integer pageSize, String time,String innerCode) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            if (!CheckUtils.check(time)) {
                time = DateUtils.getTime1();
            }
            if (null == pageNo) pageNo = 1;
            if (null == pageSize) pageSize = 30;
            List<TOrderDetail> list = orderService.findVendoutErrorList(currentUser, time, time, pageSize,innerCode).stream()
                    .map(TOrderDetail.class::cast)
                    .collect(Collectors.toList());
            List<TOrderDetail> resultList = PageUtil.formatList(pageNo, pageSize, list);
            resultMap.put("hasNextPage", pageNo * pageSize < list.size());
            resultMap.put("html", formatErrorResultHtml(resultList));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return resultMap;
    }

    private String formatErrorResultHtml(List<TOrderDetail> list) {
        StringBuilder html = new StringBuilder();
        try {
            if (null != list && list.size() > 0) {
                for (TOrderDetail orderDetail : list) {
                    html.append("");
                    html.append("<a href=\"#\">");
                    html.append("<div class=\"detailBox head\">");
                    html.append("<b class=\"f18\">");
                    html.append(Optional.of(orderDetail)
                            .map(TOrderDetail::getOrder)
                            .map(TOrderMaster::getMtVm)
                            .map(TMtVm::getInnerCode)
                            .orElse(""));
                    html.append("</b>");
                    html.append(" <span class=\"fr f14\">");
                    html.append(Optional.of(orderDetail)
                            .map(TOrderDetail::getOrder)
                            .map(TOrderMaster::getCost)
                            .map(TOrderCost::getPayTime)
                            .map(DateUtils::getTime2).orElse(""));
                    html.append("</span> </div> </div><hgroup><section><p>");
                    html.append(Optional.of(orderDetail)
                            .map(TOrderDetail::getVendout)
                            .map(TOrderDetailVendout::getVendoutChannel)
                            .map(TMtVmChannel::getChannelCode)
                            .orElse(""));
                    html.append("<span>");
                    if (!StringUtil.isEmpty(orderDetail.getSku().getSkuName())) {
                        html.append(orderDetail.getSku().getSkuName());
                    }
                    if (null != orderDetail.getSku() && !StringUtil.isEmpty(orderDetail.getSku().getSkuUnit())) {
                        html.append(orderDetail.getSku().getSkuUnit());
                    }
                    html.append("<span></p>");
                    html.append("<p>状态：<span class=\"cf_red\">").append(orderDetail.getOutStatusName()).append("</span></p>");
                    html.append("<p>原因：<span>").append(orderDetail.getOutStatusFailReason()).append("</span></p>  </section> </hgroup></a>");
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return html.toString();
    }

}
