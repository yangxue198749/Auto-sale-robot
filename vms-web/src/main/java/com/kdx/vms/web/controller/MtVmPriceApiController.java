package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TStVmPrice;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmPriceService;
import com.kdx.vms.service.IStSkuPriceService;
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
 * 设备价格配置 REST API
 */
@Controller
@RequestMapping("/vms/api/vmPrice")
public class MtVmPriceApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmPriceApiController.class);

    @Resource
    private IMtVmPriceService mtVmPriceService;

    @Resource
    private IStSkuPriceService stSkuPriceService;

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
     * 价格策略列表（供设备应用选择）
     */
    @RequestMapping(value = "/strategies", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listStrategies(
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        List rawList = stSkuPriceService.findStrategyList(user, context, strategyName, 200);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (rawList != null) {
            for (Object obj : rawList) {
                if (obj instanceof TStVmStrategy) {
                    TStVmStrategy s = (TStVmStrategy) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(s));
                    row.put("strategyName", s.getStrategyName());
                    row.put("strategyType", s.getStrategyType());
                    rows.add(row);
                }
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 价格策略明细
     */
    @RequestMapping(value = "/strategy/{id}/detail", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String strategyDetail(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TStVmStrategy strategy = Cayenne.objectForPK(context, TStVmStrategy.class, id);
        if (strategy == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "价格策略不存在");
            return JSON.toJSONString(err);
        }
        List<TStVmPrice> prices = strategy.getStPrice();
        List<Map<String, Object>> rows = new ArrayList<>();
        if (prices != null) {
            for (TStVmPrice p : prices) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", Cayenne.intPKForObject(p));
                row.put("skuId", p.getSku() != null ? Cayenne.intPKForObject(p.getSku()) : null);
                row.put("skuName", p.getSku() != null ? p.getSku().getSkuName() : null);
                row.put("skuPrice", p.getRealPrice());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 应用价格策略到设备
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String applyPrice(
            @RequestParam("strategyId") Integer strategyId,
            @RequestParam("vmIds") String vmIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Integer[] ids = parseIds(vmIdsCsv);
        try {
            Map<String, Object> result = mtVmPriceService.savePrice(user, context, strategyId, ids);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Apply price failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("type", "error");
            err.put("message", "应用价格失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    private Integer[] parseIds(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
    }
}
