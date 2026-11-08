package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.DataScopeService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
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
     * 根据用户公司层级过滤数据：
     * - 一级公司：所有数据
     * - 二级公司：本公司及子公司数据
     * - 三级公司：只本公司数据
     */
    @RequestMapping(value = "/deliveryTask/listAll", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listAllDeliveryTasks() {
        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            SelectQuery query = new SelectQuery(TTaskVm.class);

            // 基础过滤：只查询配送工单
            Expression expression = ExpressionFactory.matchExp("taskType", TTaskVm.TYPE_DELIVERY);

            // 应用数据范围过滤
            Expression scopeExpression = dataScopeService.getCompanyScopeExpression(user, "company");
            if (scopeExpression != null) {
                expression = expression.andExp(scopeExpression);
                logger.info("Applied company scope filter for user {}: {}", user.getId(), scopeExpression);
            } else {
                logger.info("No company scope filter for user {} (LEVEL_ONE company)", user.getId());
            }

            query.setQualifier(expression);
            query.addOrdering("ctime", SortOrder.DESCENDING);

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

                    // 添加公司层级信息（用于调试）
                    if (t.getCompany() != null) {
                        Map<String, Object> companyInfo = new HashMap<>();
                        companyInfo.put("id", t.getCompany().getId());
                        companyInfo.put("level", t.getCompany().getCompanyLevel());
                        row.put("company", companyInfo);
                    }

                    result.add(row);
                }
            }

            logger.info("User {} (company level: {}) queried {} delivery tasks",
                user.getId(),
                user.getCompany() != null ? user.getCompany().getCompanyLevel() : "null",
                result.size());

            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return "[]";
        }
    }

}
