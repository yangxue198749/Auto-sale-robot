package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderMaster;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.OrderSaleVo;
import com.kdx.vms.model.enumeration.OrderRefundType;
import com.kdx.vms.service.IOrderService;
import com.kdx.vms.service.IRefundService;
import com.kdx.vms.service.VendoutService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.commons.util.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 订单流水 REST API
 */
@Controller
@RequestMapping("/vms/api/order")
public class OrderApiController {

    private static final Logger logger = LoggerFactory.getLogger(OrderApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IOrderService orderService;

    @Resource
    private VendoutService vendoutService;

    @Resource
    private IRefundService refundService;

    private TSysUser currentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return ((SSOToken) principal).getSysUser();
    }

    private static void requireAuth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new UnauthorizedException("未登录");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception ex) {
        logger.error("Internal error", ex);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 500);
        err.put("message", "服务器内部错误");
        return err;
    }

    /**
     * 订单流水（默认当天）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "searchType", required = false) Integer searchType,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestParam(value = "skuName", required = false) String skuName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "orderType", required = false) Integer orderType,
            @RequestParam(value = "payType", required = false) Integer payType) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            startDate = endDate = DateUtils.getTime1();
        }

        List<?> list = orderService.findSaleOrder(user, searchType, searchValue, skuName,
                status, orderType, payType, startDate, endDate, MAX_QUERY);
        int total = list == null ? 0 : list.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<OrderSaleVo> rows = new ArrayList<>();
        if (list != null) {
            for (int i = from; i < to; i++) {
                Object obj = list.get(i);
                if (obj instanceof TOrderDetail) {
                    rows.add(OrderSaleVo.of((TOrderDetail) obj));
                }
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 发起退款（即时/预约订单走对应 MQ 主题）
     */
    @RequestMapping(value = "/refund", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String refund(@RequestParam(value = "detailId", required = false) Integer detailId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (detailId == null) return errorJson("参数不完整");

        try {
            TOrderMaster master = vendoutService.findByDetailId(user.getObjectContext(), detailId);
            if (master == null) return errorJson("订单不存在");
            String reserveTopic = (String) com.kdx.vms.commons.util.PropertiesLoader.map.get("reserveRefundTopic");
            String immediateTopic = (String) com.kdx.vms.commons.util.PropertiesLoader.map.get("immediateRefundTopic");
            com.kdx.vms.model.bean.TOrderRefund refund;
            if (master.getOrderType().equals(TOrderMaster.RESERVE)) {
                refund = refundService.launchRefund(user, OrderRefundType.VMS_GOOD, reserveTopic, detailId);
            } else if (master.getOrderType().equals(TOrderMaster.IMMEDIATE)) {
                refund = refundService.launchRefund(user, OrderRefundType.VMS_GOOD, immediateTopic, detailId);
            } else {
                return errorJson("该订单类型不支持退款");
            }
            if (refund != null) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson("退款发起失败");
        } catch (Exception e) {
            logger.error("Refund failed", e);
            return errorJson("退款失败：" + e.getMessage());
        }
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
