package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TStVmText;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.IStVmStrategyService;
import com.kdx.vms.service.IStVmTextService;
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
 * 走马灯策略 REST API（策略级，区别于设备级 /vms/api/vmText）
 */
@Controller
@RequestMapping("/vms/api/stVmText")
public class StVmTextApiController {

    private static final Logger logger = LoggerFactory.getLogger(StVmTextApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IStVmTextService stVmTextService;

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
     * 走马灯策略列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchStVmName", required = false) String searchStVmName,
            @RequestParam(value = "searchStVmText", required = false) String searchStVmText) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = user.getObjectContext();
        List<TStVmStrategy> full = stVmTextService.findStVmTextPage(context, user, searchStVmName, searchStVmText, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            TStVmStrategy s = full.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("strategyId", s.getStrategyId());
            row.put("strategyName", s.getStrategyName());
            row.put("strategyDesc", s.getStrategyDesc());
            TStVmText text = s.getStText();
            row.put("textContents", text != null ? text.getTextContents() : null);
            row.put("ctorName", userName(context, s.getCtor()));
            row.put("ctime", s.getCtime() != null ? s.getCtime().toInstant().toString() : null);
            row.put("utorName", userName(context, s.getUtor()));
            row.put("utime", s.getUtime() != null ? s.getUtime().toInstant().toString() : null);
            rows.add(row);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 策略详情
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
                || strategy.getStrategyType() != TStVmStrategy.TYPE_ST_VM_TEXT) {
            return notFound("策略不存在");
        }
        if (!dataScopeService.canAccessCompany(user, strategy.getCompany())) {
            throw new ForbiddenException("无权访问该策略");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("strategyId", strategy.getStrategyId());
        result.put("strategyName", strategy.getStrategyName());
        result.put("strategyDesc", strategy.getStrategyDesc());
        TStVmText text = strategy.getStText();
        result.put("textContents", text != null ? text.getTextContents() : null);
        result.put("startTime", text != null ? text.getStartTime() : null);
        result.put("endTime", text != null ? text.getEndTime() : null);
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
            @RequestParam(value = "textContents", required = false) String textContents) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(strategyName) || StringUtil.isEmpty(textContents)) {
            return errorJson("请填写策略名称和文案");
        }

        try {
            String errMsg = stVmTextService.addStVmText(user.getObjectContext(), strategyName, strategyDesc,
                    textContents, user);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Create text strategy failed", e);
            return errorJson("新建失败：" + e.getMessage());
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
            @RequestParam(value = "textContents", required = false) String textContents) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (strategyId == null || StringUtil.isEmpty(strategyName) || StringUtil.isEmpty(textContents)) {
            return errorJson("请填写策略名称和文案");
        }

        ObjectContext context = user.getObjectContext();
        TStVmStrategy strategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        if (strategy == null || !dataScopeService.canAccessCompany(user, strategy.getCompany())) {
            return errorJson("策略不存在或无权访问");
        }

        try {
            String errMsg = stVmTextService.editStVmText(context, strategyName, strategyDesc, textContents, user, strategyId);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Edit text strategy failed", e);
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
            String errMsg = stVmTextService.deleteByStrategyIds(user.getObjectContext(), user, Arrays.asList(parseIds(idsCsv)));
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Delete text strategy failed", e);
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

    private static Integer[] parseIds(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
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
