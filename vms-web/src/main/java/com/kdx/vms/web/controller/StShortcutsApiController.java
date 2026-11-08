package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TStVmShortcuts;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.IMtVmResourceService;
import com.kdx.vms.service.IShortcutsService;
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
 * 闪购键策略 REST API
 */
@Controller
@RequestMapping("/vms/api/stShortcuts")
public class StShortcutsApiController {

    private static final Logger logger = LoggerFactory.getLogger(StShortcutsApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IShortcutsService shortcutsService;

    @Resource
    private IMtVmResourceService mtVmResourceService;

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
     * 闪购键策略列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "modelId", required = false) Integer modelId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        List full = shortcutsService.getShortcutsStrategy(user.getCompany(), strategyName, modelId, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        ObjectContext context = user.getObjectContext();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Object obj = full.get(i);
            if (obj instanceof TStVmStrategy) {
                TStVmStrategy s = (TStVmStrategy) obj;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("strategyId", s.getStrategyId());
                row.put("strategyName", s.getStrategyName());
                row.put("strategyDesc", s.getStrategyDesc());
                row.put("vmModelId", s.getVmModel() != null ? Cayenne.intPKForObject(s.getVmModel()) : null);
                row.put("vmModelName", s.getVmModel() != null ? s.getVmModel().getModelName() : null);
                row.put("shortcutsQty", s.getVmModel() != null ? s.getVmModel().getShortcutsQty() : null);
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
     * 策略详情（含各闪购键绑定）
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
                || strategy.getStrategyType() != TStVmStrategy.TYPE_ST_VM_SHORTCUTS) {
            return notFound("策略不存在");
        }
        if (!dataScopeService.canAccessCompany(user, strategy.getCompany())) {
            throw new ForbiddenException("无权访问该策略");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("strategyId", strategy.getStrategyId());
        result.put("strategyName", strategy.getStrategyName());
        result.put("strategyDesc", strategy.getStrategyDesc());
        TMtVmModel model = strategy.getVmModel();
        if (model != null) {
            result.put("vmModelId", Cayenne.intPKForObject(model));
            result.put("vmModelName", model.getModelName());
            result.put("shortcutsQty", model.getShortcutsQty());
        }

        List<Map<String, Object>> shortcuts = new ArrayList<>();
        Map<String, TStVmShortcuts> map = strategy.getStShortcuts();
        if (map != null) {
            for (Map.Entry<String, TStVmShortcuts> entry : map.entrySet()) {
                TStVmShortcuts s = entry.getValue();
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("shortcutsCode", s.getShortcutsCode());
                row.put("bindType", s.getBindType());
                row.put("skuId", s.getSkuId());
                if (s.getSkuId() != null && s.getSkuId() != 0) {
                    TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, s.getSkuId());
                    if (sku != null) {
                        row.put("skuName", sku.getSkuName());
                        row.put("skuImgUrl", sku.getImgUrl());
                    }
                }
                if (s.getResource() != null) {
                    row.put("resourceId", Cayenne.intPKForObject(s.getResource()));
                    row.put("resourceName", s.getResource().getResourceName());
                }
                shortcuts.add(row);
            }
            Collections.sort(shortcuts, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> a, Map<String, Object> b) {
                    return Integer.compare(codeOf(a), codeOf(b));
                }
            });
        }
        result.put("shortcuts", shortcuts);
        return JSON.toJSONString(result);
    }

    /**
     * 新建策略
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "strategyDesc", required = false) String strategyDesc,
            @RequestParam(value = "modelId", required = false) Integer modelId,
            @RequestParam(value = "bindTypes", required = false) String bindTypesCsv,
            @RequestParam(value = "skuIds", required = false) String skuIdsCsv,
            @RequestParam(value = "resourceIds", required = false) String resourceIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(strategyName) || modelId == null || StringUtil.isEmpty(bindTypesCsv)) {
            return errorJson("请填写策略名称并完成闪购键配置");
        }

        try {
            SystemCode code = shortcutsService.addShortcutsStrategy(strategyName, strategyDesc, modelId,
                    parseNullableInts(skuIdsCsv), user,
                    parseNullableInts(bindTypesCsv), parseNullableInts(resourceIdsCsv));
            return JSON.toJSONString(code.toMap());
        } catch (Exception e) {
            logger.error("Create shortcuts strategy failed", e);
            return errorJson("创建失败：" + e.getMessage());
        }
    }

    /**
     * 编辑策略
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String edit(
            @RequestParam(value = "strategyId", required = false) Integer strategyId,
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "strategyDesc", required = false) String strategyDesc,
            @RequestParam(value = "modelId", required = false) Integer modelId,
            @RequestParam(value = "bindTypes", required = false) String bindTypesCsv,
            @RequestParam(value = "skuIds", required = false) String skuIdsCsv,
            @RequestParam(value = "resourceIds", required = false) String resourceIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (strategyId == null || StringUtil.isEmpty(strategyName)) {
            return errorJson("参数不完整");
        }

        try {
            SystemCode code = shortcutsService.editShortcutsStrategy(strategyId, strategyName, strategyDesc,
                    parseNullableInts(skuIdsCsv), user,
                    parseNullableInts(bindTypesCsv), parseNullableInts(resourceIdsCsv));
            return JSON.toJSONString(code.toMap());
        } catch (Exception e) {
            logger.error("Edit shortcuts strategy failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除策略
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的策略");

        try {
            SystemCode code = shortcutsService.deleteShortcutsStrategy(parseIds(idsCsv), user);
            return JSON.toJSONString(code.toMap());
        } catch (Exception e) {
            logger.error("Delete shortcuts strategy failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    /**
     * 跳转页资源列表（闪购键绑定 URL 用）
     */
    @RequestMapping(value = "/resources", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String resources(@RequestParam(value = "name", required = false) String name) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            List<TMtVmResource> list = mtVmResourceService.findPage(user, name, 100,
                    TMtVmResource.MT_VM_SHORT_CURTS_RESOURCE_TYPE);
            if (list != null) {
                for (TMtVmResource r : list) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(r));
                    row.put("resourceName", r.getResourceName());
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            logger.error("List resources failed", e);
        }
        return JSON.toJSONString(rows);
    }

    private static int codeOf(Map<String, Object> row) {
        Object code = row.get("shortcutsCode");
        try {
            return code == null ? 0 : Integer.parseInt(code.toString());
        } catch (NumberFormatException e) {
            return 0;
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

    private static Integer[] parseIds(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
    }

    /**
     * 解析允许空位的数组，如 "1,,2" -> {1, null, 2}
     */
    private static Integer[] parseNullableInts(String csv) {
        if (csv == null) return null;
        String[] parts = csv.split(",", -1);
        Integer[] values = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            values[i] = part.isEmpty() ? null : Integer.valueOf(part);
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
