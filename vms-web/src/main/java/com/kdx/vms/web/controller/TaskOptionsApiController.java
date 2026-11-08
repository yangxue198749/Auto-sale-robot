package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

/**
 * 工单表单选项 REST API Controller
 * 提供工单表单所需的下拉选项数据
 */
@Controller
@RequestMapping("/vms/api/taskOptions")
public class TaskOptionsApiController {

    private static final Logger logger = LoggerFactory.getLogger(TaskOptionsApiController.class);

    @Autowired
    private ITaskVmService taskVmService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
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
     * 获取可选售货机列表
     * GET /vms/api/taskOptions/vendingMachines
     */
    @RequestMapping(value = "/vendingMachines", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getVendingMachines(
            @RequestParam(value = "searchNodeName", required = false) String searchNodeName,
            @RequestParam(value = "searchInnerCode", required = false) String searchInnerCode,
            @RequestParam(value = "pageSize", defaultValue = "50") Integer pageSize) {

        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

            // 查询售货机列表
            SelectQuery query = new SelectQuery(TMtVm.class);
            Expression expression = ExpressionFactory.expTrue();

            // 应用数据范围过滤
            Expression scopeExpression = dataScopeService.getCompanyScopeExpression(user, "company");
            if (scopeExpression != null) {
                expression = expression.andExp(scopeExpression);
            }

            // 按点位名称过滤
            if (searchNodeName != null && !searchNodeName.isEmpty()) {
                expression = expression.andExp(
                    ExpressionFactory.likeExp("node.nodeName", "%" + searchNodeName + "%")
                );
            }

            // 按售货机编号过滤
            if (searchInnerCode != null && !searchInnerCode.isEmpty()) {
                expression = expression.andExp(
                    ExpressionFactory.likeExp("innerCode", "%" + searchInnerCode + "%")
                );
            }

            query.setQualifier(expression);

            List<TMtVm> vmList = context.performQuery(query);

            List<Map<String, Object>> result = new ArrayList<>();
            int count = 0;
            for (TMtVm vm : vmList) {
                if (count >= pageSize) break;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", vm.getId());
                row.put("innerCode", vm.getInnerCode());
                row.put("vmDesc", vm.getVmDesc());
                row.put("nodeId", vm.getNode() != null ? vm.getNode().getId() : null);
                row.put("nodeName", vm.getNode() != null ? vm.getNode().getNodeName() : null);
                row.put("status", vm.getVmStatus());
                result.add(row);
                count++;
            }

            logger.info("User {} queried {} vending machines", user.getId(), result.size());
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Error getting vending machines", e);
            throw e;
        }
    }

    /**
     * 获取可选配送员列表
     * GET /vms/api/taskOptions/deliveryStaff
     */
    @RequestMapping(value = "/deliveryStaff", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getDeliveryStaff() {
        requireAuth();
        try {
            TSysUser user = getCurrentUser();
            if (user == null) {
                throw new UnauthorizedException("用户信息获取失败");
            }

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

            // 获取当前用户公司范围内的所有用户
            List<TSysUser> staffList = new ArrayList<>();

            if (user.getCompany() != null) {
                List<Integer> accessibleCompanyIds = dataScopeService.getAccessibleCompanyIds(user);

                if (accessibleCompanyIds == null) {
                    // 一级公司：数据范围为全部，查询所有未删除用户
                    Expression expression = ExpressionFactory.matchExp(TSysUser.DELETE_MARK_PROPERTY, 0);
                    SelectQuery query = new SelectQuery(TSysUser.class, expression);
                    staffList = context.performQuery(query);
                } else {
                    // 二三级公司，获取指定公司的用户
                    for (Integer cid : accessibleCompanyIds) {
                        List<TSysUser> companyUsers = sysUserService.findSysUsersByRoleLevelForCompany(context, (String) null, null, cid);
                        if (companyUsers != null) {
                            staffList.addAll(companyUsers);
                        }
                    }
                }
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (TSysUser staff : staffList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", staff.getId());
                row.put("userName", staff.getUserName());
                row.put("userAccount", staff.getUserAccount());
                if (staff.getCompany() != null) {
                    row.put("companyId", staff.getCompany().getId());
                    row.put("companyName", staff.getCompany().getCompanyName());
                }
                result.add(row);
            }

            logger.info("User {} queried {} delivery staff options", user.getId(), result.size());
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Error getting delivery staff", e);
            throw e;
        }
    }

    /**
     * 获取补货方式选项
     * GET /vms/api/taskOptions/deliveryPatterns
     */
    @RequestMapping(value = "/deliveryPatterns", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getDeliveryPatterns() {
        requireAuth();
        try {
            List<Map<String, Object>> result = new ArrayList<>();

            Map<String, Object> pattern1 = new LinkedHashMap<>();
            pattern1.put("value", 0);
            pattern1.put("label", "补满");
            result.add(pattern1);

            Map<String, Object> pattern2 = new LinkedHashMap<>();
            pattern2.put("value", 1);
            pattern2.put("label", "手动");
            result.add(pattern2);

            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Error getting delivery patterns", e);
            throw e;
        }
    }

    /**
     * 获取工单状态选项
     * GET /vms/api/taskOptions/taskStatuses
     */
    @RequestMapping(value = "/taskStatuses", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getTaskStatuses() {
        requireAuth();
        try {
            List<Map<String, Object>> result = new ArrayList<>();

            Map<String, Object> status1 = new LinkedHashMap<>();
            status1.put("value", 1);
            status1.put("label", "未开始");
            result.add(status1);

            Map<String, Object> status2 = new LinkedHashMap<>();
            status2.put("value", 2);
            status2.put("label", "进行中");
            result.add(status2);

            Map<String, Object> status3 = new LinkedHashMap<>();
            status3.put("value", 4);
            status3.put("label", "完成");
            result.add(status3);

            Map<String, Object> status4 = new LinkedHashMap<>();
            status4.put("value", 7);
            status4.put("label", "关闭");
            result.add(status4);

            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Error getting task statuses", e);
            throw e;
        }
    }
}
