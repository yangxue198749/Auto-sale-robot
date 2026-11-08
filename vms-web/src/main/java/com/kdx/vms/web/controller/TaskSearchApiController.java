package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
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
 * 工单查询 REST API Controller
 * 四类工单统一查询（配送/维护/投放/撤机）
 */
@Controller
@RequestMapping("/vms/api/taskSearch")
public class TaskSearchApiController {

    private static final Logger logger = LoggerFactory.getLogger(TaskSearchApiController.class);

    @Resource
    private ITaskVmService taskVmService;

    @Resource
    private DataScopeService dataScopeService;

    private TSysUser getCurrentUser() {
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
     * 工单查询（四类工单）
     * taskType 为空时查询全部类型
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listTasks(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "taskType", required = false) Integer taskType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "searchField", required = false) Integer searchField,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        // taskType 为 null 时查询全部四类工单
        List<TTaskVm> allTasks = taskVmService.findPage(
                context, user, searchField, searchValue, status,
                startTime, endTime, taskType, 10000);

        int total = allTasks != null ? allTasks.size() : 0;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<TTaskVm> pagedTasks = new ArrayList<>();
        if (allTasks != null && fromIndex < total) {
            pagedTasks = allTasks.subList(fromIndex, toIndex);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (TTaskVm t : pagedTasks) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", t.getTaskVmId());
            row.put("title", t.getTaskTitle());
            row.put("type", t.getTaskType());
            row.put("typeName", getTaskTypeName(t.getTaskType()));
            row.put("status", t.getTaskVmStatus());
            row.put("vmId", t.getMtVm() != null ? t.getMtVm().getId() : null);
            row.put("vmInnerCode", t.getMtVm() != null ? t.getMtVm().getInnerCode() : null);
            row.put("ownerId", t.getOwner());
            row.put("remarks", t.getTaskRemarks());
            row.put("ctime", t.getCtime() != null ? t.getCtime().toInstant().toString() : null);
            row.put("taskTime", t.getTaskTime() != null ? t.getTaskTime().toInstant().toString() : null);
            row.put("finishTime", t.getFinishTime() != null ? t.getFinishTime().toInstant().toString() : null);
            if (t.getCompany() != null) {
                Map<String, Object> companyInfo = new HashMap<>();
                companyInfo.put("id", t.getCompany().getId());
                companyInfo.put("name", t.getCompany().getCompanyName());
                companyInfo.put("level", t.getCompany().getCompanyLevel());
                row.put("company", companyInfo);
            }
            result.add(row);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", result);
        return JSON.toJSONString(response);
    }

    /**
     * 工单类型选项
     */
    @RequestMapping(value = "/taskTypes", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getTaskTypes() {
        requireAuth();
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(typeOption(TTaskVm.TYPE_DELIVERY, "配送工单"));
        result.add(typeOption(TTaskVm.TYPE_REPAIR, "维护工单"));
        result.add(typeOption(TTaskVm.TYPE_DEPLOY, "投放工单"));
        result.add(typeOption(TTaskVm.TYPE_WITHDRAW, "撤机工单"));
        return JSON.toJSONString(result);
    }

    private Map<String, Object> typeOption(int value, String label) {
        Map<String, Object> opt = new LinkedHashMap<>();
        opt.put("value", value);
        opt.put("label", label);
        return opt;
    }

    private String getTaskTypeName(int type) {
        if (type == TTaskVm.TYPE_DELIVERY) return "配送";
        if (type == TTaskVm.TYPE_REPAIR) return "维护";
        if (type == TTaskVm.TYPE_DEPLOY) return "投放";
        if (type == TTaskVm.TYPE_WITHDRAW) return "撤机";
        return "未知(" + type + ")";
    }
}
