package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
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
 * 系统日志 REST API
 */
@Controller
@RequestMapping("/vms/api/sysLog")
public class SysLogApiController {

    private static final Logger logger = LoggerFactory.getLogger(SysLogApiController.class);

    @Resource
    private ISysLogService sysLogService;

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
     * 日志列表（按操作人姓名筛选，公司数据范围）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "userName", required = false) String userName) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        Map<String, Object> model = sysLogService.list(user, userName, pageSize, page, user.getObjectContext());
        Object nodeList = model.get("nodeList");
        Object totalSize = model.get("totalSize");

        ObjectContext context = user.getObjectContext();
        List<Map<String, Object>> rows = new ArrayList<>();
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (!(obj instanceof com.kdx.vms.model.bean.TSysLog)) continue;
                com.kdx.vms.model.bean.TSysLog log = (com.kdx.vms.model.bean.TSysLog) obj;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("logDesc", log.getLogDesc());
                row.put("logOperation", log.getLogOperation());
                row.put("moduleId", log.getModuleId());
                row.put("logTime", log.getLogTime() != null ? log.getLogTime().toInstant().toString() : null);
                row.put("userName", userName(context, log.getUserId()));
                rows.add(row);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", totalSize);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
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
}
