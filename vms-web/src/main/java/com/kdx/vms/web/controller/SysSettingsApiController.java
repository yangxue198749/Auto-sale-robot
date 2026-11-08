package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysSettingsService;
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
 * 系统设置 REST API
 */
@Controller
@RequestMapping("/vms/api/settings")
public class SysSettingsApiController {

    private static final Logger logger = LoggerFactory.getLogger(SysSettingsApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private ISysSettingsService sysSettingsService;

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
     * 设置类型列表（一级分类）
     */
    @RequestMapping(value = "/types", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String types(@RequestParam(value = "searchValue", required = false) String searchValue) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List full = sysSettingsService.findAllSettings(user.getObjectContext(), MAX_QUERY, searchValue);
        List<Map<String, Object>> rows = new ArrayList<>();
        if (full != null) {
            for (Object obj : full) {
                if (obj instanceof TSysSettings) {
                    rows.add(convert((TSysSettings) obj, false));
                }
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 类型下的配置项（分页）
     */
    @RequestMapping(value = "/{pid}/settings", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String settings(
            @PathVariable("pid") Integer pid,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = user.getObjectContext();
        TSysSettings parent = Cayenne.objectForPK(context, TSysSettings.class, pid);
        if (parent == null) return notFound("类型不存在");

        List<TSysSettings> children = parent.getChild();
        int total = children == null ? 0 : children.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            rows.add(convert(children.get(i), true));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 新建/更新配置（更新仅限 refund_type / upload_type 分组，与旧逻辑一致）
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String save(
            @RequestParam(value = "settingId", required = false) Integer settingId,
            @RequestParam(value = "pid", required = false) Integer pid,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "value", required = false) String value) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (pid == null || StringUtil.isEmpty(key)) return errorJson("请填写 key 并选择类型");

        try {
            Map<String, Object> result;
            if (settingId == null) {
                result = sysSettingsService.addSetting(user, key, value, pid, name);
            } else {
                result = sysSettingsService.updateSetting(user, key, value, pid, name, settingId);
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Save setting failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除配置
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的配置");

        try {
            List<Integer> ids = new ArrayList<>();
            for (String part : idsCsv.split(",")) {
                if (!part.trim().isEmpty()) ids.add(Integer.parseInt(part.trim()));
            }
            return JSON.toJSONString(sysSettingsService.deleteSetting(user, ids.toArray(new Integer[0])));
        } catch (Exception e) {
            logger.error("Delete setting failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    private static Map<String, Object> convert(TSysSettings s, boolean withValue) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", s.getId());
        row.put("name", s.getName());
        row.put("key", s.getKey());
        if (withValue) {
            row.put("value", s.getValue());
            row.put("utime", s.getUtime() != null ? s.getUtime().toInstant().toString() : null);
        }
        row.put("childCount", s.getChild() != null ? s.getChild().size() : 0);
        return row;
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
