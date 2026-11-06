package com.kdx.mp.web.controller;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.IOrderService;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("mp/slOrder")
public class MpSlOrderController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IOrderService orderService;

    @Resource(name = "mtVmService")
    private IMtVmService iMtVmService;

    @RequestMapping("list")
    public String list(HttpServletRequest request, String innerCode) {
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        request.setAttribute("list", iMtVmService.findMtVmListByUser(currentUser, innerCode));
        request.setAttribute("innerCode", innerCode);
        return "mp/slOrder/slOrder_list";
    }

    @RequestMapping("getOrderDetailHome")
    @ResponseBody
    public ModelAndView getOrderDetailHome(HttpServletRequest request, String vmId) {
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        ModelAndView modelAndView = new ModelAndView("mp/slOrder/slOrder_detail", "vmId", vmId);
        modelAndView.addObject("time", DateUtils.getTime1());
        modelAndView.addObject("vm", Cayenne.objectForPK(context, TMtVm.class, vmId));
        return modelAndView;
    }

    @RequestMapping("getOrderDetailList")
    @ResponseBody
    public Map<String, Object> getOrderDetailList(HttpServletRequest request, String vmId, String time, Integer pageNo, Integer pageSize) {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            if (StringUtils.isEmpty(time)) time = DateUtils.getTime1();
            if (null == pageNo) pageNo = 1;
            if (null == pageSize) pageSize = 30;

            List<?> orderList = orderService.findVendoutList(currentUser, 1, mtVm.getInnerCode(), null, null, 1, time, time, pageSize);
            List<TOrderDetail> list = orderList.stream().map(TOrderDetail.class::cast).collect(Collectors.toList());
            Integer payAmount = list.stream().mapToInt(orderDetail -> orderDetail.getCostMoney() + orderDetail.getCardCostMoney()).sum();
            resultMap.put("html", formatOrderDetail(PageUtil.formatList(pageNo, pageSize, list)));
            resultMap.put("payAmount", StringUtil.formatAmount(payAmount));
            resultMap.put("hasNextPage", pageNo * pageSize < list.size());
            return resultMap;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private String formatOrderDetail(List<TOrderDetail> list) {
        try {
            StringBuilder html = new StringBuilder();
            if (null != list && list.size() > 0) {
                for (TOrderDetail orderDetail : list) {
                    html.append("<div class=\"goodslist updown\">");
                    html.append("<p><span>");
                    html.append(Optional.of(orderDetail).map(TOrderDetail::getSku).map(TMtSku::getSkuName).orElse(""));
                    html.append("</span></p>");
                    html.append("<div>");
                    html.append("<section>");
                    html.append(" <ul><li><span>");
                    html.append(StringUtil.dateToStr(Optional.of(orderDetail).map(TOrderDetail::getVendout).map(TOrderDetailVendout::getVendoutTime).orElse(null), StringUtil.HMSPattern));
                    html.append(" </span></li>");
                    html.append(" <li><b class=\"cf_red saleMoney\">");
                    html.append(Optional.of(orderDetail).map(orderDetail1 -> orderDetail.getCostMoney() + orderDetail.getCardCostMoney()).map(StringUtil::formatAmount).orElse(""));
                    html.append(" </b></li></ul> </section></div></div>");
                }
            }
            return html.toString();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}
