package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
     * 获取补货方式选项
     * GET /vms/api/taskOptions/deliveryPatterns
     */
    @RequestMapping(value = "/deliveryPatterns", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getDeliveryPatterns() {
        requireAuth();
        try {
            logger.info("Getting delivery patterns");
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
}
