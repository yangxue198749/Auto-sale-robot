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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 撤机工单 REST API Controller
 * 提供撤机工单的 CRUD 操作
 */
@Controller
@RequestMapping("/vms/api/withdrawTask")
public class WithdrawTaskApiController {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawTaskApiController.class);

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) { super(msg); }
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

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(BadRequestException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 400);
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
     * 撤机工单列表（分页 + 过滤 + 数据范围）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listRepairTasks(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
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
        List<TTaskVm> allTasks = taskVmService.findPage(
                context, user, searchField, searchValue, status,
                startTime, endTime, TTaskVm.TYPE_WITHDRAW, 10000);

        int total = allTasks != null ? allTasks.size() : 0;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<TTaskVm> pagedTasks = new ArrayList<>();
        if (allTasks != null && fromIndex < total) {
            pagedTasks = allTasks.subList(fromIndex, toIndex);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (TTaskVm t : pagedTasks) {
            result.add(convertTaskToMap(t));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", result);
        return JSON.toJSONString(response);
    }

    /**
     * 撤机工单详情
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getRepairTask(@PathVariable("id") Integer id) {
        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TTaskVm task = taskVmService.findByPk(context, id);
        if (task == null || task.getTaskType() != TTaskVm.TYPE_WITHDRAW) {
            throw new BadRequestException("撤机工单不存在");
        }
        if (task.getCompany() != null && !dataScopeService.canAccessCompany(user, task.getCompany())) {
            throw new BadRequestException("无权访问该工单");
        }
        return JSON.toJSONString(convertTaskToMap(task));
    }

    /**
     * 创建撤机工单
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String createRepairTask(
            @RequestParam("vmId") Integer vmId,
            @RequestParam("ownerId") Integer ownerId,
            @RequestParam(value = "taskTime", required = false) String taskTime) {

        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        String errorMsg = taskVmService.createwithdrawTask(context, user, vmId, ownerId, taskTime);

        Map<String, Object> response = new LinkedHashMap<>();
        if (errorMsg != null && !errorMsg.isEmpty()) {
            response.put("success", false);
            response.put("message", errorMsg);
        } else {
            response.put("success", true);
            response.put("message", "创建成功");
        }
        return JSON.toJSONString(response);
    }

    /**
     * 修改撤机工单
     */
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String editRepairTask(
            @PathVariable("id") Integer id,
            @RequestParam(value = "editOwnerId", required = false) Integer editOwnerId,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "taskTime", required = false) String taskTime) {

        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        String errorMsg = taskVmService.editTaskVm(context, user, id, editOwnerId, remarks, taskTime);

        Map<String, Object> response = new LinkedHashMap<>();
        if (errorMsg != null && !errorMsg.isEmpty()) {
            response.put("success", false);
            response.put("message", errorMsg);
        } else {
            response.put("success", true);
            response.put("message", "更新成功");
        }
        return JSON.toJSONString(response);
    }

    /**
     * 取消撤机工单
     */
    @RequestMapping(value = "/{id}/cancel", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelRepairTask(@PathVariable("id") Integer id) {
        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        String errorMsg = taskVmService.cancelTask(context, user, new Integer[]{id});

        Map<String, Object> response = new LinkedHashMap<>();
        if (errorMsg != null && !errorMsg.isEmpty()) {
            response.put("success", false);
            response.put("message", errorMsg);
        } else {
            response.put("success", true);
            response.put("message", "取消成功");
        }
        return JSON.toJSONString(response);
    }

    private Map<String, Object> convertTaskToMap(TTaskVm task) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", task.getTaskVmId());
        row.put("title", task.getTaskTitle());
        row.put("type", task.getTaskType());
        row.put("status", task.getTaskVmStatus());
        row.put("level", task.getTaskLevel());
        row.put("remarks", task.getTaskRemarks());
        row.put("vmId", task.getMtVm() != null ? task.getMtVm().getId() : null);
        row.put("vmInnerCode", task.getMtVm() != null ? task.getMtVm().getInnerCode() : null);
        row.put("ownerId", task.getOwner());
        row.put("ctime", task.getCtime() != null ? task.getCtime().toInstant().toString() : null);
        row.put("taskTime", task.getTaskTime() != null ? task.getTaskTime().toInstant().toString() : null);
        row.put("finishTime", task.getFinishTime() != null ? task.getFinishTime().toInstant().toString() : null);
        if (task.getCompany() != null) {
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("id", task.getCompany().getId());
            companyInfo.put("name", task.getCompany().getCompanyName());
            companyInfo.put("level", task.getCompany().getCompanyLevel());
            row.put("company", companyInfo);
        }
        return row;
    }
}
