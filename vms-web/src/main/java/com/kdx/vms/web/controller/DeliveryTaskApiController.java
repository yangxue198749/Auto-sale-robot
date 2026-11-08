package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
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
 * 配送工单 REST API Controller
 * 提供配送工单的 CRUD 操作
 */
@Controller
@RequestMapping("/vms/api/deliveryTask")
public class DeliveryTaskApiController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskApiController.class);

    @Resource
    private ITaskVmService taskVmService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private DataScopeService dataScopeService;

    /**
     * 获取当前登录用户
     */
    private TSysUser getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return null;
        }
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) {
            return null;
        }
        SSOToken token = (SSOToken) principal;
        return token.getSysUser();
    }

    /**
     * 鉴权：要求 Shiro 会话已认证，否则抛 401
     */
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
     * 配送工单列表（带分页和过滤）
     * GET /vms/api/deliveryTask/list
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param status 工单状态（可选）
     * @param searchField 搜索字段：1-售货机编号，2-点位名称，3-负责人（可选）
     * @param searchValue 搜索值（可选）
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listDeliveryTasks(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "searchField", required = false) Integer searchField,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

            // 限制分页参数
            if (page < 1) page = 1;
            if (pageSize < 1 || pageSize > 100) pageSize = 10;

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

            // 使用 service 方法查询（已包含数据范围过滤）
            // 注意：findPage 返回的是所有符合条件的数据，需要手动分页
            List<TTaskVm> allTasks = taskVmService.findPage(
                    context, user, searchField, searchValue, status,
                    startTime, endTime, TTaskVm.TYPE_DELIVERY, 10000); // 限制最大查询数量

            // 手动分页
            int total = allTasks != null ? allTasks.size() : 0;
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);

            List<TTaskVm> pagedTasks = new ArrayList<>();
            if (allTasks != null && fromIndex < total) {
                pagedTasks = allTasks.subList(fromIndex, toIndex);
            }

            // 构建响应
            List<Map<String, Object>> result = new ArrayList<>();
            for (TTaskVm t : pagedTasks) {
                result.add(convertTaskToMap(t));
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("total", total);
            response.put("page", page);
            response.put("pageSize", pageSize);
            response.put("data", result);

            logger.info("User {} queried delivery tasks: page={}, pageSize={}, total={}",
                    user.getId(), page, pageSize, total);

            return JSON.toJSONString(response);
        } catch (Exception e) {
            logger.error("Error listing delivery tasks", e);
            throw e;
        }
    }

    /**
     * 配送工单详情
     * GET /vms/api/deliveryTask/{id}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getDeliveryTask(@PathVariable("id") Integer id) {
        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            TTaskVm task = taskVmService.findByPk(context, id);

            if (task == null) {
                throw new BadRequestException("工单不存在");
            }

            // 检查数据范围权限
            if (task.getCompany() != null && !dataScopeService.canAccessCompany(user, task.getCompany())) {
                throw new BadRequestException("无权访问该工单");
            }

            Map<String, Object> result = convertTaskToMap(task);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Error getting delivery task {}", id, e);
            throw e;
        }
    }

    /**
     * 创建配送工单
     * POST /vms/api/deliveryTask/create
     *
     * @param vmIds 售货机 ID 列表（JSON 数组）
     * @param ownerId 负责人 ID
     * @param deliveryPattern 配送模式
     * @param taskChannelModels 货道信息（JSON 数组，可选）
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String createDeliveryTask(
            @RequestParam("vmIds") String vmIdsJson,
            @RequestParam("ownerId") Integer ownerId,
            @RequestParam(value = "deliveryPattern", defaultValue = "1") Integer deliveryPattern,
            @RequestParam(value = "taskChannelModels", required = false) String taskChannelModelsJson) {

        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

            // 解析参数
            String[] vmIds = JSON.parseObject(vmIdsJson, String[].class);
            if (vmIds == null || vmIds.length == 0) {
                throw new BadRequestException("请选择售货机");
            }

            if (ownerId == null) {
                throw new BadRequestException("请选择负责人");
            }

            List<com.kdx.vms.model.bean.TaskChannelModel> taskChannelModels = null;
            if (taskChannelModelsJson != null && !taskChannelModelsJson.isEmpty()) {
                taskChannelModels = JSON.parseArray(taskChannelModelsJson,
                        com.kdx.vms.model.bean.TaskChannelModel.class);
            }

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            String errorMsg = taskVmService.createDeliveryTaskByIds(
                    context, user, vmIds, ownerId, deliveryPattern, taskChannelModels);

            Map<String, Object> response = new LinkedHashMap<>();
            if (errorMsg != null && !errorMsg.isEmpty()) {
                response.put("success", false);
                response.put("message", errorMsg);
            } else {
                response.put("success", true);
                response.put("message", "创建成功");
            }

            logger.info("User {} created delivery task for {} VMs", user.getId(), vmIds.length);
            return JSON.toJSONString(response);
        } catch (Exception e) {
            logger.error("Error creating delivery task", e);
            throw e;
        }
    }

    /**
     * 编辑配送工单
     * POST /vms/api/deliveryTask/{id}/edit
     *
     * @param id 工单 ID
     * @param editOwnerId 新负责人 ID（可选）
     * @param remarks 备注（可选）
     * @param taskTime 执行时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     */
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String editDeliveryTask(
            @PathVariable("id") Integer id,
            @RequestParam(value = "editOwnerId", required = false) Integer editOwnerId,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "taskTime", required = false) String taskTime) {

        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

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

            logger.info("User {} edited delivery task {}", user.getId(), id);
            return JSON.toJSONString(response);
        } catch (Exception e) {
            logger.error("Error editing delivery task {}", id, e);
            throw e;
        }
    }

    /**
     * 取消配送工单
     * POST /vms/api/deliveryTask/{id}/cancel
     */
    @RequestMapping(value = "/{id}/cancel", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelDeliveryTask(@PathVariable("id") Integer id) {
        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

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

            logger.info("User {} cancelled delivery task {}", user.getId(), id);
            return JSON.toJSONString(response);
        } catch (Exception e) {
            logger.error("Error cancelling delivery task {}", id, e);
            throw e;
        }
    }

    /**
     * 将 TTaskVm 转换为 Map
     */
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
        row.put("deliveryPattern", task.getDeliveryPattern());
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
