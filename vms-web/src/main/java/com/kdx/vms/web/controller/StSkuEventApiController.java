package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TStVmEvent;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.IStVmEventService;
import com.kdx.vms.service.IStVmStrategyService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品促销策略 REST API
 */
@Controller
@RequestMapping("/vms/api/stSkuEvent")
public class StSkuEventApiController {

    private static final Logger logger = LoggerFactory.getLogger(StSkuEventApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IStVmEventService stVmEventService;

    @Resource
    private IStVmStrategyService stVmStrategyService;

    @Resource
    private DataScopeService dataScopeService;

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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String msg) { super(msg); }
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

    @org.springframework.web.bind.annotation.ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleForbidden(ForbiddenException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 403);
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
     * 商品促销策略列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchValue", required = false) String searchValue) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = user.getObjectContext();
        List full = stVmEventService.findSkuEventList(user, context, searchValue, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Object obj = full.get(i);
            if (obj instanceof TStVmStrategy) {
                TStVmStrategy s = (TStVmStrategy) obj;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("strategyId", s.getStrategyId());
                row.put("strategyName", s.getStrategyName());
                row.put("strategyDesc", s.getStrategyDesc());
                row.put("eventCount", s.getStEvent() != null ? s.getStEvent().size() : 0);
                row.put("ctorName", userName(context, s.getCtor()));
                row.put("ctime", s.getCtime() != null ? s.getCtime().toInstant().toString() : null);
                row.put("utorName", userName(context, s.getUtor()));
                row.put("utime", s.getUtime() != null ? s.getUtime().toInstant().toString() : null);
                rows.add(row);
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
     * 策略详情（商品折扣明细）
     */
    @RequestMapping(value = "/{strategyId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("strategyId") Integer strategyId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TStVmStrategy strategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        if (strategy == null || strategy.getStrategyType() == null
                || strategy.getStrategyType() != TStVmStrategy.TYPE_ST_VM_EVENT) {
            return notFound("策略不存在");
        }
        if (!dataScopeService.canAccessCompany(user, strategy.getCompany())) {
            throw new ForbiddenException("无权访问该策略");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("strategyId", strategy.getStrategyId());
        result.put("strategyName", strategy.getStrategyName());
        result.put("strategyDesc", strategy.getStrategyDesc());

        List<Map<String, Object>> events = new ArrayList<>();
        List<TStVmEvent> list = strategy.getStEvent();
        if (list != null) {
            for (TStVmEvent e : list) {
                if (e.getEventType() != null && e.getEventType() != TStVmEvent.EVENT_TYPE_SKU) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("eventId", Cayenne.intPKForObject(e));
                row.put("discountType", e.getDiscountType());
                row.put("discountValue", e.getDiscountValue());
                TMtSku sku = e.getSku();
                if (sku != null) {
                    row.put("skuId", Cayenne.intPKForObject(sku));
                    row.put("skuName", sku.getSkuName());
                    row.put("skuCode", sku.getSkuCode());
                    row.put("offerPrice", sku.getOfferPrice());
                    row.put("skuImgUrl", sku.getImgUrl());
                }
                events.add(row);
            }
        }
        result.put("events", events);
        return JSON.toJSONString(result);
    }

    /**
     * 新建/编辑商品促销策略
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String save(
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "strategyDesc", required = false) String strategyDesc,
            @RequestParam(value = "strategyId", required = false) Integer strategyId,
            @RequestParam(value = "skuIds", required = false) String skuIdsCsv,
            @RequestParam(value = "discountTypes", required = false) String discountTypesCsv,
            @RequestParam(value = "discountValues", required = false) String discountValuesCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(strategyName) || StringUtil.isEmpty(skuIdsCsv)
                || StringUtil.isEmpty(discountTypesCsv) || StringUtil.isEmpty(discountValuesCsv)) {
            return errorJson("请填写策略名称并添加商品促销");
        }

        try {
            Integer[] skuIds = parseCsv(skuIdsCsv);
            Integer[] discountTypes = parseCsv(discountTypesCsv);
            Integer[] discountValues = parseCsv(discountValuesCsv);
            if (skuIds.length != discountTypes.length || skuIds.length != discountValues.length || skuIds.length == 0) {
                return errorJson("商品与折扣配置数量不一致");
            }
            Map<String, Object> result;
            if (strategyId == null) {
                result = stVmEventService.saveSkuEvent(user, user.getObjectContext(), strategyName, strategyDesc,
                        skuIds, discountTypes, discountValues);
            } else {
                result = stVmEventService.saveSkuEvent(user, user.getObjectContext(), strategyName, strategyDesc,
                        skuIds, discountTypes, discountValues, strategyId);
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Save sku event strategy failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除商品促销策略
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的策略");

        try {
            return JSON.toJSONString(stVmStrategyService.deleteStrategy(user, user.getObjectContext(),
                    parseCsv(idsCsv), TStVmStrategy.TYPE_ST_VM_EVENT));
        } catch (Exception e) {
            logger.error("Delete sku event strategy failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    private static String userName(ObjectContext context, Integer userId) {
        if (userId == null) return null;
        try {
            TSysUser u = Cayenne.objectForPK(context, TSysUser.class, userId);
            return u != null ? u.getUserName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer[] parseCsv(String csv) {
        String[] parts = csv.split(",");
        Integer[] values = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Integer.valueOf(parts[i].trim());
        }
        return values;
    }

    private static String notFound(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 404);
        err.put("message", message);
        return JSON.toJSONString(err);
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
