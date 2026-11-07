package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

/**
 * 前后端分离 REST 端点（Vue 前端消费）。
 * 独立 controller 避免与现有 DeliveryTaskController 的类级 @RequestMapping("deliveryTask") 拼接冲突。
 * 路径统一 /vms/api/*，Shiro anon 放行请求到 controller，controller 内部鉴权。
 */
@Controller
@RequestMapping("/vms/api")
public class VmsApiController {

    private static final Logger logger = LoggerFactory.getLogger(VmsApiController.class);

    /**
     * 鉴权：要求 Shiro 会话已认证，否则抛 401。
     * 前端 api client 捕获 401 后跳登录页。
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

    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    /**
     * 配送工单列表（JSON）。
     * 一期简化：直接查 T_TASK_VM 全表，按 TYPE_DELIVERY 过滤。
     * 后续应改为分页 + 用户权限过滤。
     */
    @RequestMapping(value = "/deliveryTask/listAll", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listAllDeliveryTasks() {
        requireAuth();
        try {
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            SelectQuery query = new SelectQuery(TTaskVm.class);
            query.setQualifier(ExpressionFactory.matchExp("taskType", TTaskVm.TYPE_DELIVERY));
            List<TTaskVm> tasks = context.performQuery(query);
            List<Map<String, Object>> result = new ArrayList<>();
            if (tasks != null) {
                for (TTaskVm t : tasks) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", t.getTaskVmId());
                    row.put("title", t.getTaskTitle());
                    row.put("type", t.getTaskType());
                    row.put("status", t.getTaskVmStatus());
                    row.put("vmId", t.getMtVm() != null ? t.getMtVm().getId() : null);
                    row.put("ownerId", t.getOwner());
                    row.put("ctime", t.getCtime() != null ? t.getCtime().toInstant().toString() : null);
                    result.add(row);
                }
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return "[]";
        }
    }

}
