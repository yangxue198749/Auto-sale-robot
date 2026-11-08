package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISkuTypeService;
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
 * 商品类型管理 REST API
 */
@Controller
@RequestMapping("/vms/api/skuType")
public class SkuTypeApiController {

    private static final Logger logger = LoggerFactory.getLogger(SkuTypeApiController.class);

    @Resource
    private ISkuTypeService skuTypeService;

    private static void requireAuth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new UnauthorizedException("未登录");
        }
    }

    private Integer currentUserId() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return ((SSOToken) principal).getUserId();
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
     * 商品类型列表（按父级 pid；pid 为空查询全部）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "pid", required = false) Integer pid,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "50") Integer pageSize) {

        requireAuth();
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 201) pageSize = 50;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> data = skuTypeService.list(pid, pageSize, page, context);

        List<Map<String, Object>> rows = new ArrayList<>();
        Object nodeList = data.get("nodeList");
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtSkuType) {
                    TMtSkuType t = (TMtSkuType) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(t));
                    row.put("typeName", t.getTypeName());
                    row.put("externalId", t.getExternalId());
                    row.put("pId", t.getParent() != null ? Cayenne.intPKForObject(t.getParent()) : null);
                    rows.add(row);
                }
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", data.get("totalSize"));
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 新增商品类型（支持指定父级）
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "pid", required = false) Integer pid,
            @RequestParam("typeName") String typeName) {

        requireAuth();
        Integer userId = currentUserId();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer id = skuTypeService.newSkuType(pid, typeName, userId, context);
            response.put("success", id != null && id > 0);
            response.put("message", id != null && id > 0 ? "创建成功" : "创建失败");
            response.put("id", id);
        } catch (Exception e) {
            logger.error("Create skuType failed", e);
            response.put("success", false);
            response.put("message", "创建失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 更新商品类型名称
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(@PathVariable("id") Integer id, @RequestParam("typeName") String typeName) {
        requireAuth();
        Integer userId = currentUserId();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer result = skuTypeService.updateSkuType(id, typeName, userId, context);
            response.put("success", result != null && result > 0);
            response.put("message", result != null && result > 0 ? "更新成功" : "更新失败");
        } catch (Exception e) {
            logger.error("Update skuType failed", e);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 删除商品类型
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Boolean ok = skuTypeService.deleteSkuType(new String[]{id.toString()}, context);
            response.put("success", ok != null && ok);
            response.put("message", ok != null && ok ? "删除成功" : "删除失败（可能被引用）");
        } catch (Exception e) {
            logger.error("Delete skuType failed", e);
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }
}
