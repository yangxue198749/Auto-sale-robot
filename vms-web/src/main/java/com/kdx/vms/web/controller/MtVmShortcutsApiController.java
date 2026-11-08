package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmShortcuts;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmShortcutsService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
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
 * 设备闪购键配置 REST API
 */
@Controller
@RequestMapping("/vms/api/vmShortcuts")
public class MtVmShortcutsApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmShortcutsApiController.class);

    @Resource
    private IMtVmShortcutsService mtVmShortcutsService;

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
     * 设备闪购键列表
     */
    @RequestMapping(value = "/{vmId}/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(@PathVariable("vmId") Integer vmId) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, vmId);
        if (vm == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "设备不存在");
            return JSON.toJSONString(err);
        }

        SelectQuery query = new SelectQuery(TMtVmShortcuts.class);
        query.setQualifier(ExpressionFactory.matchExp("mtVM", vm));
        query.addOrdering("shortcutsCode", SortOrder.ASCENDING);
        List<TMtVmShortcuts> list = context.performQuery(query);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVmShortcuts s : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", Cayenne.intPKForObject(s));
            row.put("shortcutsCode", s.getShortcutsCode());
            row.put("bindType", s.getBindType());
            row.put("skuId", s.getSku() != null ? Cayenne.intPKForObject(s.getSku()) : null);
            row.put("skuName", s.getSku() != null ? s.getSku().getSkuName() : null);
            row.put("resourceId", s.getResource() != null ? Cayenne.intPKForObject(s.getResource()) : null);
            rows.add(row);
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 保存闪购键配置
     * 参数为逗号分隔的批量数据（ids, skuIds, shortcutsCodes, bindTypes 一一对应）
     */
    @RequestMapping(value = "/{vmId}/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String save(
            @PathVariable("vmId") Integer vmId,
            @RequestParam(value = "ids", required = false) String idsCsv,
            @RequestParam(value = "skuIds", required = false) String skuIdsCsv,
            @RequestParam(value = "shortcutsCodes", required = false) String shortcutsCodesCsv,
            @RequestParam(value = "bindTypes", required = false) String bindTypesCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            Integer[] ids = parseIntegerArray(idsCsv);
            Integer[] skuIds = parseIntegerArray(skuIdsCsv);
            String[] codes = parseStringArray(shortcutsCodesCsv);
            Integer[] bindTypes = parseIntegerArray(bindTypesCsv);

            Map<String, Object> result = mtVmShortcutsService.saveShortcuts(
                    user, context, ids, skuIds, codes, null, bindTypes);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Save shortcuts failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "保存失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    private Integer[] parseIntegerArray(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new Integer[0];
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            ids[i] = p.isEmpty() ? null : Integer.parseInt(p);
        }
        return ids;
    }

    private String[] parseStringArray(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new String[0];
        return csv.split(",");
    }
}
