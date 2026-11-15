package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.service.IWorkbenchService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 个人工作台 REST API：聚合今日 10 项指标（四类工单按状态计数 + 6 个标量指标）。
 * 口径与旧 WorkbenchController 完全一致，复用 IWorkbenchService，含公司数据范围。
 */
@Controller
@RequestMapping("/vms/api/workbench")
public class WorkbenchApiController {

    private static final Logger logger = LoggerFactory.getLogger(WorkbenchApiController.class);

    @Resource
    private IWorkbenchService workbenchService;

    private SSOToken currentToken() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return (SSOToken) principal;
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

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    @ExceptionHandler(Exception.class)
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
     * 今日指标汇总。
     * 四类工单计数沿用旧口径：key 1 = 未完成（未开始+进行中）、4 = 已完成、7 = 已关闭。
     */
    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String summary() {
        requireAuth();
        SSOToken token = currentToken();
        if (token == null) throw new UnauthorizedException("用户信息获取失败");
        TSysUser user = token.getSysUser();
        ObjectContext context = token.getObjectContext();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("date", DateUtils.getTime1());
        data.put("delivery", statusCounts(workbenchService.findDeliveryTaskList(context, user)));
        data.put("repair", statusCounts(workbenchService.findRepairTaskList(context, user)));
        data.put("deploy", statusCounts(workbenchService.findDeployTaskList(context, user)));
        data.put("withdraw", statusCounts(workbenchService.findWithdrawTaskList(context, user)));
        data.put("newNodeCount", nz(workbenchService.findNewNode(context, user)));
        data.put("newVmCount", nz(workbenchService.findNewVm(context, user)));
        data.put("eventVmCount", nz(workbenchService.findEventVm(context, user)));
        data.put("oosVmCount", nz(workbenchService.findOutOfStockVm(context, user)));
        data.put("salesVolume", nz(workbenchService.findSalesVolume(context, user)));
        data.put("salesAmount", workbenchService.findSalesAmount(context, user));
        return JSON.toJSONString(data);
    }

    private static Map<String, Integer> statusCounts(Map<Long, Integer> source) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        if (source == null) {
            counts.put("ongoing", 0);
            counts.put("finished", 0);
            counts.put("closed", 0);
            return counts;
        }
        counts.put("ongoing", nz(source.get((long) TTaskVm.STATUS_INITIALIZATION)));
        counts.put("finished", nz(source.get((long) TTaskVm.STATUS_FINISHED)));
        counts.put("closed", nz(source.get((long) TTaskVm.STATUS_CLOSED)));
        return counts;
    }

    private static int nz(Integer value) {
        return value == null ? 0 : value;
    }
}
